import React, { useState, useEffect } from 'react';
import './MovieForm.css';

const MovieForm = ({ movie, onSubmit, onCancel }) => {
  const [formData, setFormData] = useState({
    title: '',
    director: '',
    year: '',
    genre: '',
  });

  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (movie) {
      setFormData({
        title: movie.title || '',
        director: movie.director || '',
        year: movie.year || '',
        genre: movie.genre || '',
      });
    }
  }, [movie]);

  const validate = () => {
    const newErrors = {};

    if (!formData.title.trim()) {
      newErrors.title = 'Title is required';
    }

    if (!formData.director.trim()) {
      newErrors.director = 'Director is required';
    }

    if (!formData.year) {
      newErrors.year = 'Year is required';
    } else {
      const yearNum = parseInt(formData.year);
      if (isNaN(yearNum) || yearNum < 1888 || yearNum > new Date().getFullYear() + 5) {
        newErrors.year = `Year must be between 1888 and ${new Date().getFullYear() + 5}`;
      }
    }

    if (!formData.genre.trim()) {
      newErrors.genre = 'Genre is required';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
    // Clear error when user starts typing
    if (errors[name]) {
      setErrors((prev) => ({
        ...prev,
        [name]: '',
      }));
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (validate()) {
      onSubmit({
        title: formData.title.trim(),
        director: formData.director.trim(),
        year: parseInt(formData.year),
        genre: formData.genre.trim(),
      });
    }
  };

  return (
    <div className="movie-form-container">
      <h2 className="form-title">{movie ? 'Edit Movie' : 'Add New Movie'}</h2>
      <form className="movie-form" onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="title">
            Title <span className="required">*</span>
          </label>
          <input
            type="text"
            id="title"
            name="title"
            value={formData.title}
            onChange={handleChange}
            className={errors.title ? 'error' : ''}
            placeholder="Enter movie title"
          />
          {errors.title && <span className="error-message">{errors.title}</span>}
        </div>

        <div className="form-group">
          <label htmlFor="director">
            Director <span className="required">*</span>
          </label>
          <input
            type="text"
            id="director"
            name="director"
            value={formData.director}
            onChange={handleChange}
            className={errors.director ? 'error' : ''}
            placeholder="Enter director name"
          />
          {errors.director && <span className="error-message">{errors.director}</span>}
        </div>

        <div className="form-row">
          <div className="form-group">
            <label htmlFor="year">
              Release Year <span className="required">*</span>
            </label>
            <input
              type="number"
              id="year"
              name="year"
              value={formData.year}
              onChange={handleChange}
              className={errors.year ? 'error' : ''}
              placeholder="YYYY"
              min="1888"
              max={new Date().getFullYear() + 5}
            />
            {errors.year && <span className="error-message">{errors.year}</span>}
          </div>

          <div className="form-group">
            <label htmlFor="genre">
              Genre <span className="required">*</span>
            </label>
            <input
              type="text"
              id="genre"
              name="genre"
              value={formData.genre}
              onChange={handleChange}
              className={errors.genre ? 'error' : ''}
              placeholder="e.g., Action, Drama, Comedy"
            />
            {errors.genre && <span className="error-message">{errors.genre}</span>}
          </div>
        </div>

        <div className="form-actions">
          <button type="button" className="btn btn-cancel" onClick={onCancel}>
            Cancel
          </button>
          <button type="submit" className="btn btn-submit">
            {movie ? 'Update Movie' : 'Add Movie'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default MovieForm;
