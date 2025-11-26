import random
from typing import Dict, Any

# Simple in-memory store (for now)  
EXPERIMENTS = {
    "exp_search_ranking": {
        "variants": ["control", "new_ranking_model"],
        "weights": [0.5, 0.5]
    }
}

def assign_variant(experiment_key: str, user_id: str) -> str:
    exp = EXPERIMENTS.get(experiment_key)
    if not exp:
        raise ValueError(f"Unknown experiment {experiment_key}")
    variants = exp["variants"]
    weights = exp["weights"]
    # assign based on weighted random
    return random.choices(variants, weights=weights, k=1)[0]

def record_event(user_id: str, experiment_key: str, variant: str, event_type: str, metadata: Dict[str, Any]):
    # For now, just log
    print(f"LOG EVENT: user={user_id}, exp={experiment_key}, variant={variant}, event={event_type}, metadata={metadata}")
    # later: persist to DB
