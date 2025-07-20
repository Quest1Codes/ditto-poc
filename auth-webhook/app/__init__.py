# app/__init__.py
from flask import Flask
from app.config import Config


def create_app(config_class=Config):
    """Creates and configures the Flask application."""
    app = Flask(__name__)
    app.config.from_object(config_class)

    from app import routes

    app.register_blueprint(routes.bp)

    return app
