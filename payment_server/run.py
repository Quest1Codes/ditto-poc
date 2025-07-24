from payment_server import app

# This block allows running the app directly with `python run.py` for local development.
# It will not be executed when running with Gunicorn in Docker.
if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5002, debug=True)
