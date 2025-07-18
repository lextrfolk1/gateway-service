#!/bin/bash

# Required substitutions (change as needed)
SERVICE_NAME=$1
REPO_URL="https://github.com/lextrfolk1/${SERVICE_NAME}.git"
GCP_PROJECT_ID="tejalf-project"
REGION="us-central1"
CLUSTER_NAME="tejalf-cluster"
REPO_PATH="tejalf-repo"

# Step 1: Clone or pull the repo
if [ -d "$SERVICE_NAME/.git" ]; then
  echo "Repo exists. Pulling latest changes..."
  cd "$SERVICE_NAME"
  git fetch origin main
  git reset --hard origin/main
  cd ..
else
  echo "Cloning fresh copy of the repo..."
  rm -rf "$SERVICE_NAME"
  git clone "$REPO_URL" "$SERVICE_NAME"
fi

# Step 1.5: Print working directory and contents
echo "Current working directory before Docker build:"
pwd
echo "Files in $SERVICE_NAME:"
ls -la "$SERVICE_NAME"

# Step 2: Build Docker image
docker build -t "${REGION}-docker.pkg.dev/${GCP_PROJECT_ID}/${REPO_PATH}/${SERVICE_NAME}:latest" "$SERVICE_NAME"

# Step 3: Push Docker image to Artifact Registry
docker push "${REGION}-docker.pkg.dev/${GCP_PROJECT_ID}/${REPO_PATH}/${SERVICE_NAME}:latest"

# Step 4: Delete existing deployment (if any)
kubectl delete deployment "$SERVICE_NAME" || true

# Step 5: Delete existing service (if any)
kubectl delete service "$SERVICE_NAME" || true

# Step 6: Apply Kubernetes deployment manifest
kubectl apply -f "$SERVICE_NAME/deployment.yaml"

# Step 7: Wait until the deployment is successfully rolled out
echo "Waiting for deployment $SERVICE_NAME to become ready..."
kubectl rollout status deployment/"$SERVICE_NAME"

# Step 8: Apply Kubernetes service manifest
kubectl apply -f "$SERVICE_NAME/service.yaml"
