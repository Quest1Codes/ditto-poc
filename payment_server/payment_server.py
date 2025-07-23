from flask import Flask, request, jsonify
import time
import random

# Create a Flask application
app = Flask(__name__)

# A list of potential failure reasons for a failed transaction.
failure_reasons = [
    "Insufficient funds",
    "Card declined by issuer",
    "Expired card",
    "Invalid CVC",
    "Transaction blocked by fraud filter",
    "Gateway timeout"
]

@app.route('/<acquirer_id>/pay', methods=['POST'])
def process_payment(acquirer_id):
    """
    This is the actual endpoint that the Android app will hit.
    The payment logic is implemented here for each specific acquirer.
    """
    print(f"--- New Request on Payment Server ---")
    print(f"Received payment request for acquirer: {acquirer_id}")

    payment_request = request.json
    print(f"Request details: {payment_request}")

    # 1. Simulate network latency to the bank
    time.sleep(random.uniform(2.0, 9.0))

    # 2. Randomly decide if the payment succeeds or fails (80% success rate)
    is_success = random.randint(1, 10) > 2

    # 3. Create the response
    if is_success:
        response_data = {
            "transactionId": f"txn_{random.randint(10000, 99999)}",
            "status": "SUCCESS",
            "acquirerId": acquirer_id,
            "acquirerName": acquirer_id.capitalize(),
            "orderId": payment_request.get("orderId"),
            "totalAmount": payment_request.get("amount"),
            "failureReason": None
        }
        print(f"Payment successful for order {payment_request.get('orderId')}.")
        return jsonify(response_data), 200
    else:
        response_data = {
            "transactionId": f"txn_{random.randint(10000, 99999)}",
            "status": "FAILED",
            "acquirerId": acquirer_id,
            "acquirerName": acquirer_id.capitalize(),
            "orderId": payment_request.get("orderId"),
            "totalAmount": payment_request.get("amount"),
            "failureReason": random.choice(failure_reasons)
        }
        print(f"Payment failed for order {payment_request.get('orderId')}. Reason: {response_data['failureReason']}")
        return jsonify(response_data), 400

if __name__ == '__main__':
    # Running on port 5002 to avoid conflict with the auth service on 5001
    print("Starting payment server on port 5002...")
    app.run(host='0.0.0.0', port=5002, debug=True)