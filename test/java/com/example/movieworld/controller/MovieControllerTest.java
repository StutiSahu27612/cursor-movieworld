package com.example.movieworld.controller;

import com.example.movieworld.model.Movie;
import com.example.movieworld.repository.MovieRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MovieControllerTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieController movieController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private Movie movie1;
    private Movie movie2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(movieController).build();
        objectMapper = new ObjectMapper();

        movie1 = new Movie("Inception", "Christopher Nolan", 2010, "Sci-Fi");
        movie1.setId(1L);

        movie2 = new Movie("The Shawshank Redemption", "Frank Darabont", 1994, "Drama");
        movie2.setId(2L);
    }

    @Test
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/api/movies/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.message").value("MovieWorld API is running"));
    }

    @Test
    void testGetAllMovies() throws Exception {
        List<Movie> movies = Arrays.asList(movie1, movie2);
        when(movieRepository.findAll()).thenReturn(movies);

        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Inception"))
                .andExpect(jsonPath("$[0].director").value("Christopher Nolan"))
                .andExpect(jsonPath("$[0].year").value(2010))
                .andExpect(jsonPath("$[0].genre").value("Sci-Fi"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].title").value("The Shawshank Redemption"));

        verify(movieRepository, times(1)).findAll();
    }

    @Test
    void testGetMovieById_Success() throws Exception {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie1));

        mockMvc.perform(get("/api/movies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Inception"))
                .andExpect(jsonPath("$.director").value("Christopher Nolan"))
                .andExpect(jsonPath("$.year").value(2010))
                .andExpect(jsonPath("$.genre").value("Sci-Fi"));

        verify(movieRepository, times(1)).findById(1L);
    }

    @Test
    void testGetMovieById_NotFound() throws Exception {
        when(movieRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/movies/999"))
                .andExpect(status().isNotFound());

        verify(movieRepository, times(1)).findById(999L);
    }

    @Test
    void testCreateMovie() throws Exception {
        Movie newMovie = new Movie("Interstellar", "Christopher Nolan", 2014, "Sci-Fi");
        Movie savedMovie = new Movie("Interstellar", "Christopher Nolan", 2014, "Sci-Fi");
        savedMovie.setId(3L);

        when(movieRepository.save(any(Movie.class))).thenReturn(savedMovie);

        mockMvc.perform(post("/api/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newMovie)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.title").value("Interstellar"))
                .andExpect(jsonPath("$.director").value("Christopher Nolan"))
                .andExpect(jsonPath("$.year").value(2014))
                .andExpect(jsonPath("$.genre").value("Sci-Fi"))
                .andExpect(header().string("Location", "/api/movies/3"));

        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    void testUpdateMovie_Success() throws Exception {
        Movie updatedMovie = new Movie("Inception Updated", "Christopher Nolan", 2010, "Thriller");
        updatedMovie.setId(1L);

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie1));
        when(movieRepository.save(any(Movie.class))).thenReturn(updatedMovie);

        mockMvc.perform(put("/api/movies/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedMovie)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Inception Updated"))
                .andExpect(jsonPath("$.genre").value("Thriller"));

        verify(movieRepository, times(1)).findById(1L);
        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    void testUpdateMovie_NotFound() throws Exception {
        Movie updatedMovie = new Movie("Non-existent Movie", "Director", 2020, "Genre");

        when(movieRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/movies/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedMovie)))
                .andExpect(status().isNotFound());

        verify(movieRepository, times(1)).findById(999L);
        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void testDeleteMovie_Success() throws Exception {
        when(movieRepository.existsById(1L)).thenReturn(true);
        doNothing().when(movieRepository).deleteById(1L);

        mockMvc.perform(delete("/api/movies/1"))
                .andExpect(status().isNoContent());

        verify(movieRepository, times(1)).existsById(1L);
        verify(movieRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteMovie_NotFound() throws Exception {
        when(movieRepository.existsById(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/movies/999"))
                .andExpect(status().isNotFound());

        verify(movieRepository, times(1)).existsById(999L);
        verify(movieRepository, never()).deleteById(anyLong());
    }
}
