from fastapi import FastAPI
from pydantic import BaseModel
from processor import process_query

app = FastAPI()

class QueryRequest(BaseModel):
    query: str

@app.post("/process")
def process(request: QueryRequest):
    return process_query(request.query)
