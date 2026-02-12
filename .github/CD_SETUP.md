# Continuous Deployment Setup for GKE

This guide explains how to set up Continuous Deployment (CD) for MovieWorld to Google Kubernetes Engine.

## Prerequisites

1. **GKE Cluster** - Create a GKE cluster
2. **GCP Project** - With billing enabled
3. **GitHub Repository** - Code pushed to GitHub
4. **Service Account** - With required permissions

## Step 1: Create GCP Service Account

```bash
# Set variables
export PROJECT_ID=your-project-id
export SA_NAME=github-actions-sa
export SA_EMAIL=${SA_NAME}@${PROJECT_ID}.iam.gserviceaccount.com

# Create service account
gcloud iam service-accounts create $SA_NAME \
  --display-name="GitHub Actions Service Account" \
  --project=$PROJECT_ID

# Grant required roles
gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="serviceAccount:${SA_EMAIL}" \
  --role="roles/container.developer"

gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="serviceAccount:${SA_EMAIL}" \
  --role="roles/storage.admin"

gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="serviceAccount:${SA_EMAIL}" \
  --role="roles/artifactregistry.writer"
```

## Step 2: Create and Download Service Account Key

```bash
# Create key
gcloud iam service-accounts keys create key.json \
  --iam-account=$SA_EMAIL \
  --project=$PROJECT_ID

# The key.json file contains the credentials
```

## Step 3: Add GitHub Secrets

Go to your GitHub repository → **Settings** → **Secrets and variables** → **Actions** → **New repository secret**

Add these secrets:

1. **`GCP_PROJECT_ID`** - Your GCP project ID
   - Example: `my-project-12345`

2. **`GKE_CLUSTER_NAME`** - Your GKE cluster name
   - Example: `movieworld-cluster`

3. **`GKE_ZONE`** - Your GKE cluster zone
   - Example: `us-central1-a`

4. **`GCP_SA_KEY`** - Service account key JSON (entire content of key.json)
   - Copy the entire content of `key.json` file

## Step 4: Enable Required APIs

```bash
gcloud services enable \
  container.googleapis.com \
  containerregistry.googleapis.com \
  --project=$PROJECT_ID
```

## Step 5: Update Deployment Image

The CD workflow will automatically update the image in `deployment.yaml`. Make sure your `deployment.yaml` has:

```yaml
image: gcr.io/YOUR_PROJECT_ID/movieworld:latest
```

The workflow will replace `YOUR_PROJECT_ID` with your actual project ID.

## Step 6: Push to Main Branch

Once secrets are configured, push to `main` or `master` branch:

```bash
git add .
git commit -m "Setup CD pipeline"
git push origin main
```

The CD workflow will automatically:
1. Build the application
2. Build Docker image
3. Push to Google Container Registry
4. Deploy to GKE

## Workflow File

- **`cd.yml`** - CD pipeline that builds, pushes Docker image, and deploys to GKE

## Manual Deployment

If you need to deploy manually:

```bash
# Authenticate
gcloud auth login
gcloud auth configure-docker

# Build and push
docker build -t gcr.io/PROJECT_ID/movieworld:latest .
docker push gcr.io/PROJECT_ID/movieworld:latest

# Configure kubectl
gcloud container clusters get-credentials CLUSTER_NAME --zone ZONE

# Deploy
kubectl apply -f k8s/
```

## Troubleshooting

### Build fails
- Check GCP_SA_KEY secret is correctly set
- Verify service account has required permissions

### Push fails
- Ensure Container Registry API is enabled
- Check service account has `storage.admin` role

### Deploy fails
- Verify GKE cluster name and zone are correct
- Check service account has `container.developer` role
- Ensure kubectl can access the cluster

### Image pull fails
- Verify image was pushed successfully
- Check image name matches deployment.yaml
- Ensure GKE nodes can pull from GCR

## Security Best Practices

1. ✅ Use Workload Identity instead of service account keys (advanced)
2. ✅ Rotate service account keys regularly
3. ✅ Use least privilege IAM roles
4. ✅ Enable binary authorization for production
5. ✅ Use private GKE clusters for production
