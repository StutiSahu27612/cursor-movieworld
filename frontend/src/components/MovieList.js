import React from 'react';
import './MovieList.css';
import MovieCard from './MovieCard';

const MovieList = ({ movies, onEdit, onDelete }) => {
  if (!movies || movies.length === 0) {
    return (
      <div className="empty-state">
        <div className="empty-icon">🎬</div>
        <h2>No movies found</h2>
        <p>Start by adding your first movie!</p>
      </div>
    );
  }

  return (
    <div className="movie-list">
      <h2 className="section-title">Your Movie Collection ({movies.length})</h2>
      <div className="movies-grid">
        {movies.map((movie) => (
          <MovieCard
            key={movie.id}
            movie={movie}
            onEdit={onEdit}
            onDelete={onDelete}
          />
        ))}
      </div>
    </div>
  );
};

export default MovieList;
