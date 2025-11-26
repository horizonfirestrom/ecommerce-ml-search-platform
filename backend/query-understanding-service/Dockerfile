FROM python:3.10-slim

WORKDIR /app

# Install system deps (if you had them, e.g. build-essential) – optional
# RUN apt-get update && apt-get install -y build-essential && rm -rf /var/lib/apt/lists/*

# Copy requirements and install them
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Install spaCy model explicitly
RUN pip install "https://github.com/explosion/spacy-models/releases/download/en_core_web_sm-3.7.1/en_core_web_sm-3.7.1-py3-none-any.whl"

# Copy app code
COPY . .

# Expose port
EXPOSE 7003

# Run service
CMD ["uvicorn", "app:app", "--host", "0.0.0.0", "--port", "7003"]
