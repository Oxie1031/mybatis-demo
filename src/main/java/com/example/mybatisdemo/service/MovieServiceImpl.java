package com.example.mybatisdemo.service;

import com.example.mybatisdemo.controller.MovieResponse;
import com.example.mybatisdemo.entity.Movie;
import com.example.mybatisdemo.mapper.MovieMapper;
import com.example.mybatisdemo.exception.MovieNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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
        Movie movie = movieMapper.findOptionalById(id)
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
    public MovieResponse addMovie(Movie movie) {
        movieMapper.insert(movie);

        int newMovieId = movieMapper.getLatestMovieId();

        Movie newMovie = movieMapper.findById(newMovieId);
        return convertToResponse(newMovie);
    }


    @Override
    public MovieResponse updateMovie(int id, Movie movie) {
        movieMapper.update(id, movie);

        Movie updatedMovie = movieMapper.findById(id);
        return convertToResponse(updatedMovie);
    }

    @Override
    public MovieResponse deleteMovie(int id) {
        Movie deletedMovie = movieMapper.findById(id);

        movieMapper.delete(id);

        return convertToResponse(deletedMovie);
    }

    @Override
    public Movie patchMovie(int id, Map<String, Object> updates) {
        Movie movieToUpdate = movieMapper.findOptionalById(id)
                .orElseThrow(() -> new MovieNotFoundException("Movie with id " + id + " not found."));

        if(updates.containsKey("name")){
            movieToUpdate.setName((String) updates.get("name"));
        }
        if(updates.containsKey("director")){
            movieToUpdate.setDirector((String) updates.get("director"));
        }
        if(updates.containsKey("year")){
            movieToUpdate.setYear((int) updates.get("year"));
        }


        movieMapper.update(id, movieToUpdate);
        return  movieToUpdate;
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
