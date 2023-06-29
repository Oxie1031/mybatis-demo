package com.example.mybatisdemo.service;

import com.example.mybatisdemo.entity.Movie;
import com.example.mybatisdemo.exception.MovieNotFoundException;
import com.example.mybatisdemo.exception.MovieValidationException;
import com.example.mybatisdemo.mapper.MovieMapper;
import io.micrometer.common.util.StringUtils;
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
        String uuidString = UUID.randomUUID().toString();
        movie.setId(uuidString);

        movieMapper.insert(movie);

        Movie newMovie = movieMapper.findById(uuidString);
        return newMovie;
    }


    @Override
    public Movie updateMovie(String id, Movie movie) {

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


//TODO:後日更新処理をMovieに移し、引数として利用するPatchMovieInputクラスを作成

    @Override
    public Movie patchMovie(String id, Map<String, Object> updates) {
        Movie movieToUpdate = movieMapper.findOptionalById(id)
                .orElseThrow(() -> new MovieNotFoundException("Movie with id " + id + " not found."));

        if(updates.containsKey("name")){
            String name = (String) updates.get("name");
            if(StringUtils.isBlank(name)){
                throw new MovieValidationException("Name cannot be blank");
            }
            movieToUpdate.setName(name);
        }
        if(updates.containsKey("director")){
            String director = (String) updates.get("director");
            if(StringUtils.isBlank(director)){
                throw new MovieValidationException("Director cannot be blank");
            }
            movieToUpdate.setDirector(director);
        }
        if(updates.containsKey("year")){
            int year = (int) updates.get("year");
            if(year < 1800 || year > 2100){
                throw new MovieValidationException("Year should be between 1800 and 2100");
            }
            movieToUpdate.setYear(year);
        }
        if(updates.containsKey("rating")){
            int rating = (int) updates.get("rating");
            if(rating < 0 || rating > 0){
                throw new MovieValidationException("Rating should be between 0.0 and 10.0");
            }
            BigDecimal BigDecimalRating = BigDecimal.valueOf(rating);
            movieToUpdate.setRating(BigDecimalRating);
        }
        if(updates.containsKey("runtime")){
            int runtime = (int) updates.get("runtime");
            if(runtime < 1){
                throw new MovieValidationException("Runtime should be greater than 0");
            }
            movieToUpdate.setRuntime(runtime);
        }

        movieMapper.update(id, movieToUpdate);
        return  movieToUpdate;
    }

}

