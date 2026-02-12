# Workload Identity Federation (WIF) Setup Guide

This guide explains how to set up Workload Identity Federation for GitHub Actions to authenticate with GCP without using service account keys.

## Prerequisites

1. **GCP Project** with billing enabled
2. **GitHub Repository** 
3. **GKE Cluster** created

## Step 1: Enable Required APIs

```bash
export PROJECT_ID=your-project-id

gcloud services enable \
  iamcredentials.googleapis.com \
  sts.googleapis.com \
  container.googleapis.com \
  containerregistry.googleapis.com \
  --project=$PROJECT_ID
```

## Step 2: Create Workload Identity Pool

```bash
export PROJECT_ID=your-project-id
export POOL_ID=github-actions-pool
export PROVIDER_ID=github-provider

# Create workload identity pool
gcloud iam workload-identity-pools create $POOL_ID \
  --project=$PROJECT_ID \
  --location="global" \
  --display-name="GitHub Actions Pool"

# Create workload identity provider
gcloud iam workload-identity-pools providers create-oidc $PROVIDER_ID \
  --project=$PROJECT_ID \
  --location="global" \
  --workload-identity-pool=$POOL_ID \
  --display-name="GitHub Provider" \
  --attribute-mapping="google.subject=assertion.sub,attribute.actor=assertion.actor,attribute.repository=assertion.repository" \
  --attribute-condition="assertion.repository_owner=='YOUR_GITHUB_ORG' || assertion.repository_owner=='YOUR_GITHUB_USERNAME'" \
  --issuer-uri="https://token.actions.githubusercontent.com"
```

**Note**: Replace `YOUR_GITHUB_ORG` or `YOUR_GITHUB_USERNAME` with your actual GitHub organization or username.

## Step 3: Create Service Account

```bash
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
```

## Step 4: Allow GitHub to Impersonate Service Account

```bash
export PROJECT_NUMBER=$(gcloud projects describe $PROJECT_ID --format="value(projectNumber)")
export POOL_ID=github-actions-pool
export PROVIDER_ID=github-provider
export SA_EMAIL=github-actions-sa@${PROJECT_ID}.iam.gserviceaccount.com
export REPO=YOUR_GITHUB_USERNAME/YOUR_REPO_NAME

# Allow GitHub Actions to impersonate the service account
gcloud iam service-accounts add-iam-policy-binding $SA_EMAIL \
  --project=$PROJECT_ID \
  --role="roles/iam.workloadIdentityUser" \
  --member="principalSet://iam.googleapis.com/projects/${PROJECT_NUMBER}/locations/global/workloadIdentityPools/${POOL_ID}/attribute.repository/${REPO}"
```

**Note**: Replace `YOUR_GITHUB_USERNAME/YOUR_REPO_NAME` with your actual GitHub repository (e.g., `stutis/movieworld`).

## Step 5: Get Workload Identity Provider Resource Name

```bash
export PROJECT_NUMBER=$(gcloud projects describe $PROJECT_ID --format="value(projectNumber)")
export POOL_ID=github-actions-pool
export PROVIDER_ID=github-provider

echo "projects/${PROJECT_NUMBER}/locations/global/workloadIdentityPools/${POOL_ID}/providers/${PROVIDER_ID}"
```

This will output something like:
```
projects/123456789/locations/global/workloadIdentityPools/github-actions-pool/providers/github-provider
```

## Step 6: Add GitHub Secrets

Go to your GitHub repository → **Settings** → **Secrets and variables** → **Actions** → **New repository secret**

Add these secrets:

1. **`GCP_PROJECT_ID`** - Your GCP project ID
   - Example: `my-project-12345`

2. **`GKE_CLUSTER_NAME`** - Your GKE cluster name
   - Example: `movieworld-cluster`

3. **`GKE_ZONE`** - Your GKE cluster zone
   - Example: `us-central1-a`

4. **`WIF_PROVIDER`** - Workload Identity Provider resource name
   - Format: `projects/PROJECT_NUMBER/locations/global/workloadIdentityPools/POOL_ID/providers/PROVIDER_ID`
   - Example: `projects/123456789/locations/global/workloadIdentityPools/github-actions-pool/providers/github-provider`

5. **`WIF_SERVICE_ACCOUNT`** - Service account email
   - Format: `SERVICE_ACCOUNT_NAME@PROJECT_ID.iam.gserviceaccount.com`
   - Example: `github-actions-sa@my-project-12345.iam.gserviceaccount.com`

## Complete Setup Script

```bash
#!/bin/bash
set -e

export PROJECT_ID=your-project-id
export POOL_ID=github-actions-pool
export PROVIDER_ID=github-provider
export SA_NAME=github-actions-sa
export REPO=YOUR_GITHUB_USERNAME/YOUR_REPO_NAME

# Enable APIs
gcloud services enable \
  iamcredentials.googleapis.com \
  sts.googleapis.com \
  container.googleapis.com \
  containerregistry.googleapis.com \
  --project=$PROJECT_ID

# Create workload identity pool
gcloud iam workload-identity-pools create $POOL_ID \
  --project=$PROJECT_ID \
  --location="global" \
  --display-name="GitHub Actions Pool"

# Create provider
gcloud iam workload-identity-pools providers create-oidc $PROVIDER_ID \
  --project=$PROJECT_ID \
  --location="global" \
  --workload-identity-pool=$POOL_ID \
  --display-name="GitHub Provider" \
  --attribute-mapping="google.subject=assertion.sub,attribute.actor=assertion.actor,attribute.repository=assertion.repository" \
  --attribute-condition="assertion.repository_owner=='YOUR_GITHUB_ORG'" \
  --issuer-uri="https://token.actions.githubusercontent.com"

# Create service account
export SA_EMAIL=${SA_NAME}@${PROJECT_ID}.iam.gserviceaccount.com
gcloud iam service-accounts create $SA_NAME \
  --display-name="GitHub Actions Service Account" \
  --project=$PROJECT_ID

# Grant roles
gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="serviceAccount:${SA_EMAIL}" \
  --role="roles/container.developer"

gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="serviceAccount:${SA_EMAIL}" \
  --role="roles/storage.admin"

# Allow GitHub to impersonate
export PROJECT_NUMBER=$(gcloud projects describe $PROJECT_ID --format="value(projectNumber)")
gcloud iam service-accounts add-iam-policy-binding $SA_EMAIL \
  --project=$PROJECT_ID \
  --role="roles/iam.workloadIdentityUser" \
  --member="principalSet://iam.googleapis.com/projects/${PROJECT_NUMBER}/locations/global/workloadIdentityPools/${POOL_ID}/attribute.repository/${REPO}"

# Output values for GitHub secrets
echo ""
echo "=== Add these to GitHub Secrets ==="
echo "GCP_PROJECT_ID: $PROJECT_ID"
echo "WIF_PROVIDER: projects/${PROJECT_NUMBER}/locations/global/workloadIdentityPools/${POOL_ID}/providers/${PROVIDER_ID}"
echo "WIF_SERVICE_ACCOUNT: $SA_EMAIL"
```

## Advantages of WIF

✅ **No service account keys** - More secure, no keys to manage  
✅ **Automatic rotation** - Tokens are short-lived  
✅ **Repository-specific** - Can restrict to specific repositories  
✅ **Audit trail** - Better logging and monitoring  
✅ **No key storage** - No need to store keys in GitHub secrets  

## Troubleshooting

### Authentication fails
- Verify WIF_PROVIDER format is correct
- Check repository name matches the attribute condition
- Ensure service account has required IAM bindings

### Permission denied
- Verify service account has `container.developer` role
- Check service account has `storage.admin` role for GCR
- Ensure workload identity binding is correct

### Provider not found
- Verify pool and provider names are correct
- Check project number is correct
- Ensure APIs are enabled
