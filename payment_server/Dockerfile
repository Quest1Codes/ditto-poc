FROM python:3.9-slim

# Set the working directory in the container
WORKDIR /usr/src/app

# Install uv for faster package installation (similar to auth-service)
RUN pip install uv

# Copy the requirements file into the container
COPY requirements.txt ./

# Install dependencies using uv for better performance
RUN uv pip install --system --no-cache -r requirements.txt

# Copy the rest of the application source code into the container
COPY . .

# Make port 5002 available to the world outside this container
EXPOSE 5002

# Use gunicorn to run the application, pointing to the 'app' object in the 'run.py' file.
# This is a production-ready web server.
CMD ["gunicorn", "--bind", "0.0.0.0:5002", "run:app"]