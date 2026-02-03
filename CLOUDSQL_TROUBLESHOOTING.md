# Cloud SQL Connection Troubleshooting

## Error: Access denied for user 'user1'@'34.27.10.199'

This error occurs when the application cannot authenticate with Cloud SQL. Here's how to fix it:

## Step 1: Verify User Exists in Cloud SQL

1. **Check if user 'user1' exists**:
   ```bash
   gcloud sql users list --instance=YOUR_INSTANCE_NAME
   ```

2. **If user doesn't exist, create it**:
   ```bash
   gcloud sql users create user1 \
       --instance=YOUR_INSTANCE_NAME \
       --password=YOUR_SECURE_PASSWORD
   ```

3. **If user exists but password is wrong, reset it**:
   ```bash
   gcloud sql users set-password user1 \
       --instance=YOUR_INSTANCE_NAME \
       --password=NEW_PASSWORD
   ```

## Step 2: Configure Network Access

### Option A: Authorize VM IP Address (Public IP)

1. Go to Cloud Console → SQL → Your Instance → **Connections**
2. Under **Authorized networks**, click **Add Network**
3. Add your VM's IP: `34.27.10.199/32`
4. Click **Save**

### Option B: Use Private IP (Recommended for GCP VMs)

1. Enable Private IP on your Cloud SQL instance
2. Ensure your VM is in the same VPC network
3. Use Private IP in connection string

## Step 3: Update Application Configuration

### Method 1: Using Environment Variables (Recommended for JAR)

When running the JAR file, pass connection details as environment variables:

```bash
export SPRING_DATASOURCE_URL="jdbc:mysql:///movieworld?cloudSqlInstance=PROJECT_ID:REGION:INSTANCE_NAME&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false&serverTimezone=UTC"
export SPRING_DATASOURCE_USERNAME="user1"
export SPRING_DATASOURCE_PASSWORD="your_password"
export SPRING_PROFILES_ACTIVE="cloudsql"

java -jar movieworld-0.0.1-SNAPSHOT.jar
```

### Method 2: Update application-cloudsql.properties

1. Edit `src/main/resources/application-cloudsql.properties`
2. Replace placeholders:
   - `YOUR_PROJECT_ID` → Your GCP project ID
   - `YOUR_REGION` → Your Cloud SQL region (e.g., `us-central1`)
   - `YOUR_INSTANCE_NAME` → Your Cloud SQL instance name
   - `YOUR_PASSWORD_HERE` → User 'user1' password
3. Rebuild the JAR:
   ```bash
   mvn clean package -DskipTests
   ```
4. Run with profile:
   ```bash
   java -jar -Dspring.profiles.active=cloudsql movieworld-0.0.1-SNAPSHOT.jar
   ```

## Step 4: Verify Connection String Format

The connection string must follow this exact format:

```
jdbc:mysql:///<DATABASE_NAME>?cloudSqlInstance=<PROJECT_ID>:<REGION>:<INSTANCE_NAME>&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false&serverTimezone=UTC
```

**Important Notes:**
- Use `///` (three slashes) after `mysql:`
- Database name comes immediately after `///`
- `cloudSqlInstance` parameter format: `PROJECT_ID:REGION:INSTANCE_NAME`
- No spaces in the connection string

**Example:**
```
jdbc:mysql:///movieworld?cloudSqlInstance=my-project-123:us-central1:movieworld-db&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false&serverTimezone=UTC
```

## Step 5: Set Up Application Default Credentials

The Cloud SQL Socket Factory needs credentials to authenticate:

```bash
# On your GCP VM
gcloud auth application-default login

# Or use a service account
export GOOGLE_APPLICATION_CREDENTIALS="/path/to/service-account-key.json"
```

## Step 6: Verify Database Exists

Ensure the database `movieworld` exists:

```bash
gcloud sql databases list --instance=YOUR_INSTANCE_NAME
```

If it doesn't exist:
```bash
gcloud sql databases create movieworld --instance=YOUR_INSTANCE_NAME
```

## Step 7: Grant Permissions to User

Ensure user 'user1' has proper permissions:

```sql
-- Connect to Cloud SQL and run:
GRANT ALL PRIVILEGES ON movieworld.* TO 'user1'@'%';
FLUSH PRIVILEGES;
```

## Common Issues and Solutions

### Issue 1: "Access denied" but password is correct
- **Solution**: Check if user host is set to `%` (allows from anywhere)
- **Solution**: Verify IP is authorized in Cloud SQL authorized networks

### Issue 2: "Unknown database"
- **Solution**: Create the database: `gcloud sql databases create movieworld --instance=INSTANCE_NAME`

### Issue 3: "SocketFactory not found"
- **Solution**: Ensure `mysql-socket-factory-connector-j-8` is in `pom.xml` and JAR includes it
- **Solution**: Run `mvn clean package` to rebuild with dependencies

### Issue 4: Connection works from VM but not from JAR
- **Solution**: Check if environment variables are set correctly
- **Solution**: Verify the JAR was built with latest configuration
- **Solution**: Check if profile is activated: `-Dspring.profiles.active=cloudsql`

## Testing Connection

Test the connection from your VM:

```bash
# Test with mysql client
mysql -h YOUR_INSTANCE_IP -u user1 -p

# Or test with gcloud
gcloud sql connect YOUR_INSTANCE_NAME --user=user1
```

## Quick Fix Checklist

- [ ] User 'user1' exists in Cloud SQL
- [ ] User password is correct
- [ ] Database 'movieworld' exists
- [ ] VM IP (34.27.10.199) is authorized OR using Private IP
- [ ] Connection string format is correct
- [ ] Application Default Credentials are set
- [ ] JAR file includes Cloud SQL Socket Factory dependency
- [ ] Profile is set to 'cloudsql' when running JAR

## Running the Fixed JAR

```bash
# Set environment variables
export SPRING_DATASOURCE_URL="jdbc:mysql:///movieworld?cloudSqlInstance=PROJECT:REGION:INSTANCE&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false&serverTimezone=UTC"
export SPRING_DATASOURCE_USERNAME="user1"
export SPRING_DATASOURCE_PASSWORD="your_password"

# Run with profile
java -jar -Dspring.profiles.active=cloudsql movieworld-0.0.1-SNAPSHOT.jar
```
