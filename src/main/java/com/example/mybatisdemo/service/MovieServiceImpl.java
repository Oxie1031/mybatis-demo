package com.example.mybatisdemo.service;

import com.example.mybatisdemo.controller.MovieResponse;
import com.example.mybatisdemo.entity.Movie;
import com.example.mybatisdemo.mapper.MovieMapper;
import com.example.mybatisdemo.exception.MovieNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService {


    private final MovieMapper movieMapper;

    public MovieServiceImpl(MovieMapper movieMapper) {
        this.movieMapper = movieMapper;
    }

    @Override
    public List<MovieResponse> getAllMovies() {
        return movieMapper.findAll().stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    public MovieResponse getMovie(int id) {
        Movie movie = movieMapper.findById(id)
                .orElseThrow(() -> new MovieNotFoundException("Movie with id " + id + " not found."));
        return convertToResponse(movie);
    }



    @Override
    public List<MovieResponse> getMoviesByPublishedYear(int year) {
        List<Movie> movies = movieMapper.findByPublishedYear(year);
        if (movies == null || movies.isEmpty()) {
            throw new MovieNotFoundException("No movies were found for the year: " + year);
        }

        return movies.stream().map(this::convertToResponse).collect(Collectors.toList());
    }



    @Override
    public void addMovie(Movie movie) {
        movieMapper.insert(movie);
    }

    @Override
    public void updateMovie(int id, Movie movie) {
        movieMapper.update(id, movie);
    }

    @Override
    public void deleteMovie(int id) {
        movieMapper.delete(id);
    }

    private MovieResponse convertToResponse(Movie movie) {
        MovieResponse response = new MovieResponse();
        response.setId(movie.getId());
        response.setName(movie.getName());
        response.setDirector(movie.getDirector());
        response.setYear(movie.getYear());
        return response;
    }

}
