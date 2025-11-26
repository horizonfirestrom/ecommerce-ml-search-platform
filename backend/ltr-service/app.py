from fastapi import FastAPI
from pydantic import BaseModel
from typing import List, Dict
import numpy as np
from model_loader import model

app = FastAPI()

class RerankRequest(BaseModel):
    query: str
    productIds: List[str]
    features: Dict[str, List[float]]  # key: featureName, value: list aligned with productIds

class RerankResponse(BaseModel):
    rerankedProductIds: List[str]

@app.post("/rerank", response_model=RerankResponse)
def rerank(req: RerankRequest):
    # build feature matrix
    ids = req.productIds
    feature_names = list(req.features.keys())
    X = np.vstack([req.features[f] for f in feature_names]).T

    # model.predict returns scores
    scores = model.predict(X)
    # sort by descending score
    sorted_idx = np.argsort(scores)[::-1]
    reranked = [ids[i] for i in sorted_idx]

    return RerankResponse(rerankedProductIds=reranked)
