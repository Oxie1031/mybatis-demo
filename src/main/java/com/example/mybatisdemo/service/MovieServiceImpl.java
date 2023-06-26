package com.example.mybatisdemo.service;

import com.example.mybatisdemo.entity.Movie;
import com.example.mybatisdemo.exception.MovieNotFoundException;
import com.example.mybatisdemo.mapper.MovieMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
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

        return movies.stream().collect(Collectors.toList());
    }



    @Override
    public Movie addMovie(Movie movie) {
        String uuidString = generateUuid();
        movie.setId(uuidString);

        movieMapper.insert(movie);

        Movie newMovie = movieMapper.findById(uuidString);
        return newMovie;
    }



    @Override
    public Movie updateMovie(String id, Movie movie) {
        Movie existingMovie = movieMapper.findOptionalById(id)
                .orElseThrow(() -> new MovieNotFoundException("Movie with id " + id + " not found."));

        movieMapper.update(id, movie);
        Movie updatedMovie = movieMapper.findOptionalById(id).orElseThrow(() -> new MovieNotFoundException("Movie with id " + id + " not found."));

        return updatedMovie;
    }


    @Override
    public Movie deleteMovie(String id) {
        Movie movieToDelete = movieMapper.findOptionalById(id)
                .orElseThrow(() -> new MovieNotFoundException("Movie with id " + id + " not found."));

        movieMapper.delete(id);

        return movieToDelete;
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
        if(updates.containsKey("rating")){
            movieToUpdate.setRating((BigDecimal) updates.get("rating"));
        }
        if(updates.containsKey("runtime")){
            movieToUpdate.setRuntime((int) updates.get("runtime"));
        }


        movieMapper.update(id, movieToUpdate);
        return  movieToUpdate;
    }

    public String generateUuid() {
        return UUID.randomUUID().toString();
    }


}
