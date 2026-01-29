package com.example.movieworld.config;

import com.example.movieworld.model.Movie;
import com.example.movieworld.repository.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private final MovieRepository movieRepository;

    public DataInitializer(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public void run(String... args) {
        // Check if movies already exist to avoid duplicates
        if (movieRepository.count() == 0) {
            logger.info("Initializing dummy movie data...");

            Movie movie1 = new Movie("Inception", "Christopher Nolan", 2010, "Sci-Fi");
            Movie movie2 = new Movie("The Shawshank Redemption", "Frank Darabont", 1994, "Drama");

            movieRepository.save(movie1);
            movieRepository.save(movie2);

            logger.info("Successfully initialized {} dummy movies", movieRepository.count());
        } else {
            logger.info("Movies already exist in database. Skipping initialization.");
        }
    }
}
