# MovieWorld Frontend

A modern React frontend application for the MovieWorld backend API.

## Features

- 🎬 View all movies in a beautiful grid layout
- ➕ Add new movies
- ✏️ Edit existing movies
- 🗑️ Delete movies
- 🟢 Real-time API health status
- 📱 Responsive design for mobile and desktop

## Prerequisites

- Node.js (v14 or higher)
- npm or yarn
- Backend API running on http://localhost:8081

## Installation

1. Navigate to the frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

## Running the Application

1. Make sure the backend is running on http://localhost:8081

2. Start the React development server:
```bash
npm start
```

3. Open [http://localhost:3000](http://localhost:3000) in your browser

## Building for Production

To create a production build:

```bash
npm run build
```

This creates an optimized build in the `build` folder.

## API Configuration

The frontend is configured to connect to the backend API at `http://localhost:8081/api/movies`. 

If your backend runs on a different URL or port, update the `API_BASE_URL` in `src/services/api.js`.

## Project Structure

```
frontend/
├── public/
│   └── index.html
├── src/
│   ├── components/
│   │   ├── MovieCard.js
│   │   ├── MovieCard.css
│   │   ├── MovieForm.js
│   │   ├── MovieForm.css
│   │   ├── MovieList.js
│   │   └── MovieList.css
│   ├── services/
│   │   └── api.js
│   ├── App.js
│   ├── App.css
│   ├── index.js
│   └── index.css
├── package.json
└── README.md
```

## Technologies Used

- React 18
- Axios for API calls
- CSS3 for styling
- Modern ES6+ JavaScript
