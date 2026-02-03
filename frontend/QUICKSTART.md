# Quick Start Guide

## Step 1: Install Dependencies

Navigate to the frontend directory and install npm packages:

```bash
cd frontend
npm install
```

## Step 2: Start the Backend

Make sure your Spring Boot backend is running on `http://localhost:8081`.

You can start it by running:
```bash
mvn spring-boot:run
```

## Step 3: Start the Frontend

In the frontend directory, run:

```bash
npm start
```

The React app will open automatically at `http://localhost:3000`.

## Features

✅ **View All Movies** - See all movies in a beautiful grid layout  
✅ **Add Movies** - Click "Add New Movie" to create a new entry  
✅ **Edit Movies** - Click the edit icon (✏️) on any movie card  
✅ **Delete Movies** - Click the delete icon (🗑️) on any movie card  
✅ **Health Status** - See real-time API connection status  
✅ **Responsive Design** - Works on desktop, tablet, and mobile  

## Troubleshooting

### Frontend can't connect to backend

1. Make sure the backend is running on port 8081
2. Check the browser console for CORS errors
3. Verify the API URL in `src/services/api.js` matches your backend URL

### Port 3000 is already in use

React will automatically try the next available port (3001, 3002, etc.)

### npm install fails

Make sure you have Node.js v14 or higher installed:
```bash
node --version
```
