FROM python:3.12-slim

WORKDIR /app

# Install dependencies
RUN pip install kafka-python-ng

# Copy application code
COPY send_mail.py .

# Unbuffered output for logs
ENV PYTHONUNBUFFERED=1

CMD ["python", "send_mail.py"]
