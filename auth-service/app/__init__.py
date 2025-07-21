import logging
from flask import Flask
from flask_pymongo import PyMongo
from flask_bcrypt import Bcrypt
from app.config import Config

mongo = PyMongo()
bcrypt = Bcrypt()

def create_app(config_class=Config):
    app = Flask(__name__)
    app.config.from_object(config_class)

    # Configure logging
    gunicorn_logger = logging.getLogger('gunicorn.error')
    app.logger.handlers = gunicorn_logger.handlers
    app.logger.setLevel(gunicorn_logger.level)

    mongo.init_app(app)
    bcrypt.init_app(app)

    from app import routes
    app.register_blueprint(routes.bp)

    return app