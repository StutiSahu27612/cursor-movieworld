import React from 'react';
import './MovieCard.css';

const MovieCard = ({ movie, onEdit, onDelete }) => {
  return (
    <div className="movie-card">
      <div className="movie-header">
        <h3 className="movie-title">{movie.title}</h3>
        <div className="movie-actions">
          <button
            className="btn-icon btn-edit"
            onClick={() => onEdit(movie)}
            title="Edit movie"
          >
            ✏️
          </button>
          <button
            className="btn-icon btn-delete"
            onClick={() => onDelete(movie.id)}
            title="Delete movie"
          >
            🗑️
          </button>
        </div>
      </div>
      <div className="movie-details">
        <div className="movie-detail-item">
          <span className="detail-label">Director:</span>
          <span className="detail-value">{movie.director}</span>
        </div>
        <div className="movie-detail-item">
          <span className="detail-label">Year:</span>
          <span className="detail-value">{movie.year}</span>
        </div>
        <div className="movie-detail-item">
          <span className="detail-label">Genre:</span>
          <span className="detail-value genre-badge">{movie.genre}</span>
        </div>
      </div>
    </div>
  );
};

export default MovieCard;
