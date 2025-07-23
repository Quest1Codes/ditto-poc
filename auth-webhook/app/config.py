# app/config.py
import os

class Config:
    """Configuration class to load secrets from environment variables."""
    JWT_SECRET_KEY = os.getenv("JWT_SECRET_KEY")