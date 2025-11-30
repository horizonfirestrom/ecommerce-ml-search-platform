from fastapi import FastAPI
from pydantic import BaseModel
from typing import List

app = FastAPI()

class QueryRequest(BaseModel):
    query: str

class QueryResponse(BaseModel):
    cleaned_query: str
    tokens: List[str]

@app.post("/process", response_model=QueryResponse)
def process_query(req: QueryRequest):
    # simple example
    cleaned = req.query.strip().lower()
    tokens = cleaned.split()
    return QueryResponse(cleaned_query=cleaned, tokens=tokens)
