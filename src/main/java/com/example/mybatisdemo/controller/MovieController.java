package com.example.mybatisdemo.controller;

import com.example.mybatisdemo.entity.Movie;
import com.example.mybatisdemo.exception.MovieNotFoundException;
import com.example.mybatisdemo.service.MovieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<ApiResponse<MovieResponse>> addMovie(@RequestBody Movie movie) {
        MovieResponse addedMovieResponse = movieService.addMovie(movie);

        ApiResponse<MovieResponse> response = new ApiResponse<>("success", "Movie successfully added.", addedMovieResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResponse>> patchMovie(@PathVariable int id, @RequestBody Map<String, Object> updates) {
        Movie updatedMovie = movieService.patchMovie(id, updates);

        MovieResponse updatedMovieResponse = new MovieResponse(updatedMovie.getId(),updatedMovie.getName(),updatedMovie.getDirector(),updatedMovie.getYear());
        ApiResponse<MovieResponse> response = new ApiResponse<>("success", "Movie successfully updated.", updatedMovieResponse);

        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResponse>> updateMovie(@PathVariable int id, @RequestBody Movie movie) {
        movieService.updateMovie(id, movie);

        MovieResponse updatedMovieResponse = movieService.getMovie(id);

        ApiResponse<MovieResponse> response = new ApiResponse<>("success","Movie successfully updated.", updatedMovieResponse);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResponse>> deleteMovie(@PathVariable int id) {
        MovieResponse deletedMovieResponse = movieService.getMovie(id);

        movieService.deleteMovie(id);
        ApiResponse<MovieResponse> response = new ApiResponse<>("success","Movie successfully deleted.",deletedMovieResponse);

        return ResponseEntity.ok(response);
    }

}
