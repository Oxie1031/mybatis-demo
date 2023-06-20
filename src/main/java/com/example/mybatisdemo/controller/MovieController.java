package com.example.mybatisdemo.controller;

import com.example.mybatisdemo.entity.Movie;
import com.example.mybatisdemo.exception.MovieNotFoundException;
import com.example.mybatisdemo.service.MovieService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public List<MovieResponse> getAllMovies() {
        return movieService.getAllMovies();
    }

    @GetMapping("/{id}")
    public MovieResponse getMovie(@PathVariable int id) {
        return movieService.getMovie(id);
    }

    @GetMapping(params = "published_year")
    public List<MovieResponse> getMoviesByPublishedYear(@RequestParam("published_year") int year) {
        return movieService.getMoviesByPublishedYear(year);
    }

    @PostMapping
    public void addMovie(@RequestBody Movie movie) {
        movieService.addMovie(movie);
    }

    @PutMapping("/{id}")
    public void updateMovie(@PathVariable int id, @RequestBody Movie movie) {
        movieService.updateMovie(id, movie);
    }

    @DeleteMapping("/{id}")
    public void deleteMovie(@PathVariable int id) {
        movieService.deleteMovie(id);
    }


}
