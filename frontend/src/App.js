import React, { useState, useEffect } from 'react';
import './App.css';
import MovieList from './components/MovieList';
import MovieForm from './components/MovieForm';
import { movieService } from './services/api';

function App() {
  const [movies, setMovies] = useState([]);
  const [editingMovie, setEditingMovie] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [healthStatus, setHealthStatus] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Check health status
  useEffect(() => {
    const checkHealth = async () => {
      try {
        const health = await movieService.checkHealth();
        setHealthStatus(health);
      } catch (err) {
        setHealthStatus({ status: 'DOWN', message: 'API is not available' });
      }
    };
    checkHealth();
    const interval = setInterval(checkHealth, 30000); // Check every 30 seconds
    return () => clearInterval(interval);
  }, []);

  // Load movies
  useEffect(() => {
    loadMovies();
  }, []);

  const loadMovies = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await movieService.getAllMovies();
      setMovies(data);
    } catch (err) {
      setError('Failed to load movies. Make sure the backend is running on http://localhost:8081');
      console.error('Error loading movies:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateMovie = async (movieData) => {
    try {
      await movieService.createMovie(movieData);
      await loadMovies();
      setShowForm(false);
      setEditingMovie(null);
    } catch (err) {
      alert('Failed to create movie. Please try again.');
      console.error('Error creating movie:', err);
    }
  };

  const handleUpdateMovie = async (id, movieData) => {
    try {
      await movieService.updateMovie(id, movieData);
      await loadMovies();
      setShowForm(false);
      setEditingMovie(null);
    } catch (err) {
      alert('Failed to update movie. Please try again.');
      console.error('Error updating movie:', err);
    }
  };

  const handleDeleteMovie = async (id) => {
    if (window.confirm('Are you sure you want to delete this movie?')) {
      try {
        await movieService.deleteMovie(id);
        await loadMovies();
      } catch (err) {
        alert('Failed to delete movie. Please try again.');
        console.error('Error deleting movie:', err);
      }
    }
  };

  const handleEditMovie = (movie) => {
    setEditingMovie(movie);
    setShowForm(true);
  };

  const handleCancelEdit = () => {
    setEditingMovie(null);
    setShowForm(false);
  };

  const handleAddNew = () => {
    setEditingMovie(null);
    setShowForm(true);
  };

  return (
    <div className="App">
      <header className="app-header">
        <h1>🎬 MovieWorld</h1>
        <div className="health-status">
          {healthStatus && (
            <span className={`health-indicator ${healthStatus.status === 'UP' ? 'healthy' : 'unhealthy'}`}>
              {healthStatus.status === 'UP' ? '🟢' : '🔴'} {healthStatus.message}
            </span>
          )}
        </div>
      </header>

      <main className="app-main">
        {error && (
          <div className="error-banner">
            {error}
          </div>
        )}

        {showForm ? (
          <MovieForm
            movie={editingMovie}
            onSubmit={editingMovie ? (data) => handleUpdateMovie(editingMovie.id, data) : handleCreateMovie}
            onCancel={handleCancelEdit}
          />
        ) : (
          <>
            <div className="actions-bar">
              <button className="btn btn-primary" onClick={handleAddNew}>
                + Add New Movie
              </button>
              <button className="btn btn-secondary" onClick={loadMovies} disabled={loading}>
                {loading ? 'Loading...' : '🔄 Refresh'}
              </button>
            </div>

            {loading && !movies.length ? (
              <div className="loading">Loading movies...</div>
            ) : (
              <MovieList
                movies={movies}
                onEdit={handleEditMovie}
                onDelete={handleDeleteMovie}
              />
            )}
          </>
        )}
      </main>
    </div>
  );
}

export default App;
