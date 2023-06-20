package com.example.mybatisdemo.service;

import com.example.mybatisdemo.controller.MovieResponse;
import com.example.mybatisdemo.entity.Movie;

import java.util.List;
import java.util.Optional;

public interface MovieService {
    List<MovieResponse> getAllMovies();
    MovieResponse getMovie(int id);
    List<MovieResponse> getMoviesByPublishedYear(int year);
    void addMovie(Movie movie);
    void updateMovie(int id, Movie movie);
    void deleteMovie(int id);
}

