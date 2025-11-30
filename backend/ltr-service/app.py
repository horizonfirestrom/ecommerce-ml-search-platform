from fastapi import FastAPI
from pydantic import BaseModel
from typing import List, Dict
import numpy as np
import logging
import traceback

from model_loader import model  # your trained sklearn model

app = FastAPI()

# --------------------------------------------------------------------
# Logging setup
# --------------------------------------------------------------------
logger = logging.getLogger("ltr-service")
logging.basicConfig(level=logging.INFO)


# --------------------------------------------------------------------
# Request / Response models
# --------------------------------------------------------------------

class RerankRequest(BaseModel):
    """
    Request body for /rerank

    Example:
    {
      "query": "red shoes",
      "productIds": ["product_12", "product_61"],
      "features": {
        "bm25": [1.2, 0.8],
        "vector_score": [0.9, 0.7],
        "popularity": [10.0, 5.0]
      }
    }
    """
    query: str
    productIds: List[str]
    features: Dict[str, List[float]]  # featureName -> list aligned with productIds


class RerankResponse(BaseModel):
    rerankedProductIds: List[str]


# --------------------------------------------------------------------
# Helper: build feature matrix X with padding / truncation
# --------------------------------------------------------------------
def build_feature_matrix(req: RerankRequest) -> np.ndarray:
    """
    Build a 2D numpy array X from req.features aligned with req.productIds.
    Also adjust number of feature columns to match model.n_features_in_.
    """
    ids = req.productIds
    num_products = len(ids)

    if num_products == 0:
        logger.warning("LTR: empty productIds list")
        return np.zeros((0, getattr(model, "n_features_in_", 0)))

    if not req.features:
        logger.warning("LTR: no features provided, creating zeros matrix")
        return np.zeros((num_products, getattr(model, "n_features_in_", 0)))

    feature_names = list(req.features.keys())

    # Validate each feature vector length
    for fname in feature_names:
        vals = req.features[fname]
        if len(vals) != num_products:
            raise ValueError(
                f"Feature '{fname}' length {len(vals)} != num_products {num_products}"
            )

    # Shape: (num_products, num_features_from_request)
    X = np.vstack([req.features[fname] for fname in feature_names]).T

    logger.info(
        "LTR: initial X shape %s (products=%d, features=%d). Features=%s",
        X.shape,
        num_products,
        X.shape[1],
        feature_names,
    )

    expected = getattr(model, "n_features_in_", None)
    if expected is not None:
        current = X.shape[1]

        if current < expected:
            diff = expected - current
            logger.warning(
                "LTR: model expects %d features, got %d. Padding %d zero columns.",
                expected,
                current,
                diff,
            )
            padding = np.zeros((num_products, diff), dtype=X.dtype)
            X = np.hstack([X, padding])

        elif current > expected:
            logger.warning(
                "LTR: model expects %d features, got %d. Truncating to %d.",
                expected,
                current,
                expected,
            )
            X = X[:, :expected]

        logger.info("LTR: final X shape after adjustment: %s", X.shape)
    else:
        logger.warning(
            "LTR: model has no n_features_in_ attribute. Using X as-is with shape %s",
            X.shape,
        )

    return X


# --------------------------------------------------------------------
# /rerank endpoint
# --------------------------------------------------------------------
@app.post("/rerank", response_model=RerankResponse)
def rerank(req: RerankRequest):
    """
    Rerank the given productIds based on feature vectors using a trained model.
    On ANY error, log it and return original productIds instead of 500.
    """
    ids = req.productIds
    num_products = len(ids)

    logger.info(
        "LTR /rerank called. query='%s', num_products=%d, feature_keys=%s",
        req.query,
        num_products,
        list(req.features.keys()) if req.features else [],
    )

    if num_products == 0:
        # Nothing to rerank
        return RerankResponse(rerankedProductIds=[])

    try:
        # 1) Build feature matrix X with correct dimensionality
        X = build_feature_matrix(req)

        # If no features columns, just return original order
        if X.shape[1] == 0:
            logger.warning("LTR: X has 0 feature columns. Returning original order.")
            return RerankResponse(rerankedProductIds=ids)

        # 2) Predict scores
        scores = model.predict(X)

        # 3) Sort indices by descending score
        sorted_idx = np.argsort(scores)[::-1]
        reranked = [ids[i] for i in sorted_idx]

        logger.info(
            "LTR: rerank successful. Top product after rerank=%s",
            reranked[0] if reranked else "N/A",
        )

        return RerankResponse(rerankedProductIds=reranked)

    except Exception as e:
        # Catch ALL exceptions so FastAPI doesn't return 500
        logger.error("LTR: error during rerank: %s", str(e))
        logger.error("LTR: traceback:\n%s", traceback.format_exc())
        logger.warning("LTR: returning original productIds order as fallback.")
        return RerankResponse(rerankedProductIds=ids)
