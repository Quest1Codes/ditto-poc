FROM python:3.9-slim

WORKDIR /usr/src/app

RUN pip install uv

COPY requirements.txt ./
RUN uv pip install --system --no-cache -r requirements.txt

COPY . .

EXPOSE 5001

CMD ["gunicorn", "--bind", "0.0.0.0:5001", "run:app"]