import jwt
from datetime import datetime, timedelta, timezone
from flask import Blueprint, request, jsonify, current_app
from app import mongo, bcrypt

bp = Blueprint("api", __name__)


@bp.route("/register", methods=["POST"])
def register():
    data = request.get_json()
    username = data.get("username")
    password = data.get("password")
    role = data.get("role")
    if not all([username, password, role]):
        return jsonify({"error": "Missing required fields"}), 400
    if mongo.db.users.find_one({"username": username}):
        return jsonify({"error": "Username already exists"}), 409
    hashed_password = bcrypt.generate_password_hash(password).decode("utf-8")
    mongo.db.users.insert_one(
        {"username": username, "password_hash": hashed_password, "role": role}
    )
    return jsonify({"message": f"User {username} registered successfully"}), 201


@bp.route("/login", methods=["POST"])
def login():
    data = request.get_json()
    username = data.get("username")
    password = data.get("password")
    if not username or not password:
        return jsonify({"error": "Missing username or password"}), 400
    user = mongo.db.users.find_one({"username": username})
    if user is None or not bcrypt.check_password_hash(user["password_hash"], password):
        return jsonify({"error": "Invalid username or password"}), 401
    payload = {
        "sub": str(user["_id"]),
        "role": user["role"],
        "iat": datetime.now(timezone.utc),
        "exp": datetime.now(timezone.utc) + timedelta(hours=8),
    }
    token = jwt.encode(payload, current_app.config["JWT_SECRET_KEY"], algorithm="HS256")

    return jsonify({"accessToken": token}), 200
