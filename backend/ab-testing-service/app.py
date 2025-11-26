from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import Dict, Any
from service_logic import assign_variant, record_event

app = FastAPI()

class AssignRequest(BaseModel):
    userId: str
    experimentKey: str

class AssignResponse(BaseModel):
    variant: str

class LogRequest(BaseModel):
    userId: str
    experimentKey: str
    variant: str
    eventType: str
    metadata: Dict[str, Any] = {}

@app.post("/assign", response_model=AssignResponse)
def assign(req: AssignRequest):
    try:
        variant = assign_variant(req.experimentKey, req.userId)
        return AssignResponse(variant=variant)
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))

@app.post("/log")
def log_event(req: LogRequest):
    record_event(req.userId, req.experimentKey, req.variant, req.eventType, req.metadata)
    return {"status": "ok"}
