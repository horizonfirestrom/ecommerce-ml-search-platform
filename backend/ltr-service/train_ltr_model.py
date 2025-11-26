import numpy as np
import pickle
from sklearn.ensemble import RandomForestRegressor

# Generate synthetic data just for demo
n_samples = 1000
n_features = 5

X = np.random.rand(n_samples, n_features)
y = np.random.rand(n_samples)

model = RandomForestRegressor(n_estimators=50, random_state=42)
model.fit(X, y)

with open("ltr_model.pkl", "wb") as f:
    pickle.dump(model, f)

print("Model trained and saved: ltr_model.pkl")
