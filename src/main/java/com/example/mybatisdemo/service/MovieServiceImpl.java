package com.example.mybatisdemo.service;

import com.example.mybatisdemo.controller.MovieResponse;
import com.example.mybatisdemo.entity.Movie;
import com.example.mybatisdemo.mapper.MovieMapper;
import com.example.mybatisdemo.exception.MovieNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService {


    private final MovieMapper movieMapper;

    public MovieServiceImpl(MovieMapper movieMapper) {
        this.movieMapper = movieMapper;
    }

    @Override
    public List<Movie> getAllMovies() {
        return movieMapper.findAll().stream().collect(Collectors.toList());
    }

    @Override
    public Movie getMovie(String id) {
        Movie movie = movieMapper.findOptionalById(id)
                .orElseThrow(() -> new MovieNotFoundException("Movie with id " + id + " not found."));
        return movie;
    }



    @Override
    public List<Movie> getMoviesByPublishedYear(int year) {
        List<Movie> movies = movieMapper.findByPublishedYear(year);
        if (movies == null || movies.isEmpty()) {
            throw new MovieNotFoundException("No movies were found for the year: " + year);
        }

        return movies.stream().collect(Collectors.toList());
    }



    @Override
    public Movie addMovie(Movie movie) {
        String uuidString = UUID.randomUUID().toString();
        movie.setId(uuidString);

        movieMapper.insert(movie);

        Movie newMovie = movieMapper.findById(uuidString);
        return newMovie;
    }



    @Override
    public Movie updateMovie(String  id, Movie movie) {
        movieMapper.update(id, movie);

        Movie updatedMovie = movieMapper.findById(id);
        return updatedMovie;
    }

    @Override
    public Movie deleteMovie(String  id) {
        Movie deletedMovie = movieMapper.findById(id);

        movieMapper.delete(id);

        return deletedMovie;
    }

    @Override
    public Movie patchMovie(String  id, Map<String, Object> updates) {
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

}
