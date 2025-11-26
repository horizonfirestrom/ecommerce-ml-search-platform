from fastapi import FastAPI
from pydantic import BaseModel
from typing import List
import numpy as np
from faiss_index import load_index

app = FastAPI(title="Vector Search Service")

index = None
product_ids = None


class VectorSearchRequest(BaseModel):
    embedding: List[float]
    topK: int = 10


@app.post("/search")
def search(req: VectorSearchRequest):

    global index, product_ids

    embedding_dim = len(req.embedding)

    # If index not loaded yet, or dimension mismatch → reload
    if index is None:
        index, product_ids = load_index(embedding_dim)

    vector = np.array(req.embedding).astype("float32").reshape(1, -1)

    # FAISS search
    distances, indices = index.search(vector, req.topK)

    results = []
    for i in range(req.topK):
        pid = product_ids[indices[0][i]]
        score = float(distances[0][i])
        results.append({"id": pid, "score": score})

    return results
