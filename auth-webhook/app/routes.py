# app/routes.py
import jwt
from flask import Blueprint, request, jsonify, current_app

bp = Blueprint("api", __name__)

def get_permissions_for_role(role: str, user_id: str) -> dict:
    """
    Returns a permissions dictionary based on the user's role.
    Permissions are structured according to Ditto's requirements.
    """
    if role == "manager":
        return {
            "read": {"everything": True, "queriesByCollection": {}},
            "write": {"everything": True, "queriesByCollection": {}}
        }
    elif role == "cashier":
        return {
            "read": {
                "everything": False,
                "queriesByCollection": {
                    "items": ["true"],
                    "orders": ["true"]
                }
            },
            "write": {
                "everything": False,
                "queriesByCollection": {
                    "orders": [f"_id.createdBy == '{user_id}'"]
                }
            }
        }
    else:
        return {
            "read": {"everything": False, "queriesByCollection": {}},
            "write": {"everything": False, "queriesByCollection": {}}
        }

@bp.route("/auth", methods=["POST"])
def auth_webhook():
    """
    Handles the authentication request from Ditto Server.
    Validates the token and returns user permissions.
    """
    data = request.get_json()
    token = data.get("token")
    
    if not token:
        return jsonify({"authenticated": False, "clientInfo": "Token not provided"}), 400

    try:
        payload = jwt.decode(
            token,
            current_app.config["JWT_SECRET_KEY"],
            algorithms=["HS256"]
        )
        
        user_id = payload.get("sub")
        role = payload.get("role")

        if not user_id or not role:
            return jsonify({"authenticated": False, "clientInfo": "Token is missing user_id or role"}), 400

        permissions = get_permissions_for_role(role, user_id)
        
        response = {
            "authenticated": True,
            "userID": user_id,
            "expirationSeconds": 8 * 60 * 60,  
            "permissions": permissions,
            "identityServiceMetadata": {
                "userRole": role
            }
        }
        return jsonify(response), 200

    except (jwt.ExpiredSignatureError, jwt.InvalidTokenError) as e:
        return jsonify({"authenticated": False, "clientInfo": str(e)}), 401