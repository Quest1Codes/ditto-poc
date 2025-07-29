# app/routes.py
import jwt
from flask import Blueprint, request, jsonify, current_app
from datetime import datetime

bp = Blueprint("api", __name__)


def get_permissions_for_role(role: str, user_id: str) -> dict:
    """
    Returns a permissions dictionary based on the user's role.
    Permissions are structured according to Ditto's requirements.
    """
    return {
        "read": {"everything": True, "queriesByCollection": {}},
        "write": {"everything": True, "queriesByCollection": {}},
    }


@bp.route("/auth", methods=["POST"])
def auth_webhook():
    """
    Handles the authentication request from Ditto Server.
    Validates the token and returns user permissions.
    """
    data = request.get_json()
    current_app.logger.info(f"Received auth request: {data}")
    token = data.get("token")

    if not token:
        return (
            jsonify({"authenticated": False, "clientInfo": "Token not provided"}),
            400,
        )

    try:
        key = current_app.config["JWT_SECRET_KEY"]
        if not key:
            current_app.logger.error(
                "JWT_SECRET_KEY is not set in the webhook environment!"
            )
            return (
                jsonify(
                    {"authenticated": False, "clientInfo": "Server configuration error"}
                ),
                500,
            )

        payload = jwt.decode(token, key, algorithms="HS256")

        user_id = payload.get("sub")
        role = payload.get("role")

        if not user_id or not role:
            return (
                jsonify(
                    {
                        "authenticated": False,
                        "clientInfo": "Token is missing user_id or role",
                    }
                ),
                400,
            )

        permissions = get_permissions_for_role(role, user_id)

        response = {
            "authenticated": True,
            "userID": "6880674ccd221c74c058ab73",
            "expirationSeconds": 8 * 60 * 60,
            "permissions": permissions,
            "identityServiceMetadata": {"userRole": role},
        }
        return jsonify(response), 200

    except jwt.ExpiredSignatureError:
        return jsonify({"authenticated": False, "clientInfo": "Token has expired"}), 401
    except jwt.InvalidTokenError as e:
        current_app.logger.error(f"Signature verification failed: {e}")
        return (
            jsonify(
                {"authenticated": False, "clientInfo": "Signature verification failed"}
            ),
            401,
        )
    except Exception as e:
        print(f"An unexpected error occurred: {e}")
        return (
            jsonify(
                {"authenticated": False, "clientInfo": "An internal error occurred"}
            ),
            500,
        )
