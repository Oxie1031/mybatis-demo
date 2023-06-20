package com.example.mybatisdemo.service;

import com.example.mybatisdemo.controller.MovieResponse;
import com.example.mybatisdemo.entity.Movie;

import java.util.List;
import java.util.Map;

public interface MovieService {
    List<MovieResponse> getAllMovies();
    MovieResponse getMovie(int id);
    List<MovieResponse> getMoviesByPublishedYear(int year);
    MovieResponse addMovie(Movie movie);
    MovieResponse updateMovie(int id, Movie movie);
    MovieResponse deleteMovie(int id);
    Movie patchMovie(int id, Map<String, Object> updates);
}

