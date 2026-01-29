-- MovieWorld Database Initial Data
-- This file contains INSERT queries to populate the movie table with sample data
-- Using INSERT IGNORE to prevent errors if data already exists

-- Insert sample movies (will skip if already exist)
INSERT IGNORE INTO movie (title, director, release_year, genre) VALUES
('Inception', 'Christopher Nolan', 2010, 'Sci-Fi'),
('The Shawshank Redemption', 'Frank Darabont', 1994, 'Drama'),
('The Dark Knight', 'Christopher Nolan', 2008, 'Action'),
('Pulp Fiction', 'Quentin Tarantino', 1994, 'Crime'),
('The Godfather', 'Francis Ford Coppola', 1972, 'Crime'),
('Forrest Gump', 'Robert Zemeckis', 1994, 'Drama'),
('The Matrix', 'Lana Wachowski, Lilly Wachowski', 1999, 'Sci-Fi'),
('Goodfellas', 'Martin Scorsese', 1990, 'Crime'),
('Fight Club', 'David Fincher', 1999, 'Drama'),
('Interstellar', 'Christopher Nolan', 2014, 'Sci-Fi');
