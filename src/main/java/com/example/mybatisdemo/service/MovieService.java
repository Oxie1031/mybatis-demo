package com.example.mybatisdemo.service;

import com.example.mybatisdemo.controller.MovieResponse;
import com.example.mybatisdemo.entity.Movie;

import java.util.List;
import java.util.Map;

public interface MovieService {
    List<Movie> getAllMovies();
    Movie getMovie(int id);
    List<Movie> getMoviesByPublishedYear(int year);
    Movie addMovie(Movie movie);
    Movie updateMovie(int id, Movie movie);
    Movie deleteMovie(int id);
    Movie patchMovie(int id, Map<String, Object> updates);
}

