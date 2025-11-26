FROM python:3.10-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

# Train model at build time so ltr_model.pkl is present in the image
RUN python train_ltr_model.py

EXPOSE 7005

CMD ["uvicorn", "app:app", "--host", "0.0.0.0", "--port", "7005"]
