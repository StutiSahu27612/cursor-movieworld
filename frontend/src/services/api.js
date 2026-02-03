import axios from 'axios';

const API_BASE_URL = 'http://localhost:8081/api/movies';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const movieService = {
  // Health check
  checkHealth: async () => {
    try {
      const response = await api.get('/health');
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Get all movies
  getAllMovies: async () => {
    try {
      const response = await api.get('/');
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Get movie by ID
  getMovieById: async (id) => {
    try {
      const response = await api.get(`/${id}`);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Create movie
  createMovie: async (movie) => {
    try {
      const response = await api.post('/', movie);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Update movie
  updateMovie: async (id, movie) => {
    try {
      const response = await api.put(`/${id}`, movie);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Delete movie
  deleteMovie: async (id) => {
    try {
      await api.delete(`/${id}`);
    } catch (error) {
      throw error;
    }
  },
};

export default api;
