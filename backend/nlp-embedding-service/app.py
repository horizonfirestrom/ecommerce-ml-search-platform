from fastapi import FastAPI
from pydantic import BaseModel
import numpy as np
from model_loader import load_model

app = FastAPI()

model = load_model()

class TextRequest(BaseModel):
    text: str

@app.get("/")
def root():
    return {"message": "NLP Embedding Service Running"}

@app.post("/embed")
def embed_text(request: TextRequest):
    embedding = model.encode(request.text).tolist()
    return {"embedding": embedding}
