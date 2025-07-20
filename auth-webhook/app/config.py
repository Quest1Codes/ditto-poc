# app/config.py
import os
from dotenv import load_dotenv

load_dotenv()

class Config:
    """Configuration class to load secrets from environment variables."""
    JWT_SECRET_KEY = os.getenv("JWT_SECRET_KEY")