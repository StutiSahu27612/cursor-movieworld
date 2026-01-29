package com.example.movieworld.config;

import com.example.movieworld.model.Movie;
import com.example.movieworld.repository.MovieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private DataInitializer dataInitializer;

    @Test
    void testRun_WhenDatabaseIsEmpty_ShouldInitializeMovies() {
        // Given
        when(movieRepository.count()).thenReturn(0L, 2L); // First call returns 0, second call returns 2
        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        dataInitializer.run();

        // Then
        verify(movieRepository, times(2)).count(); // Called once to check, once to log
        verify(movieRepository, times(2)).save(any(Movie.class));
    }

    @Test
    void testRun_WhenDatabaseHasMovies_ShouldSkipInitialization() {
        // Given
        when(movieRepository.count()).thenReturn(5L);

        // When
        dataInitializer.run();

        // Then
        verify(movieRepository, times(1)).count();
        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void testRun_ShouldCreateCorrectMovies() {
        // Given
        when(movieRepository.count()).thenReturn(0L, 2L); // First call returns 0, second call returns 2
        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        dataInitializer.run();

        // Then
        verify(movieRepository).save(argThat(movie -> "Inception".equals(movie.getTitle()) &&
                "Christopher Nolan".equals(movie.getDirector()) &&
                movie.getYear() == 2010 &&
                "Sci-Fi".equals(movie.getGenre())));

        verify(movieRepository).save(argThat(movie -> "The Shawshank Redemption".equals(movie.getTitle()) &&
                "Frank Darabont".equals(movie.getDirector()) &&
                movie.getYear() == 1994 &&
                "Drama".equals(movie.getGenre())));
    }
}
