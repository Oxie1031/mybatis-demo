package com.example.mybatisdemo.controller;

import com.example.mybatisdemo.entity.Movie;
import com.example.mybatisdemo.exception.MovieNotFoundException;
import com.example.mybatisdemo.service.MovieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public List<MovieResponse> getAllMovies() {
        return movieService.getAllMovies().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }


    @GetMapping("/{id}")
    public MovieResponse getMovie(@PathVariable int id) {
       return convertToResponse(movieService.getMovie(id));
    }

    @GetMapping(params = "published_year")
    public List<MovieResponse> getMoviesByPublishedYear(@RequestParam("published_year") int year) {
        return movieService.getMoviesByPublishedYear(year)
                .stream().map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MovieResponse>> addMovie(@RequestBody Movie movie) {

        ApiResponse<MovieResponse> response = new ApiResponse<>("success", "Movie successfully added.", convertToResponse(movieService.addMovie(movie)));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResponse>> patchMovie(@PathVariable int id, @RequestBody Map<String, Object> updates) {
        ApiResponse<MovieResponse> response = new ApiResponse<>("success", "Movie successfully updated.", convertToResponse(movieService.patchMovie(id, updates)));

        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResponse>> updateMovie(@PathVariable int id, @RequestBody Movie movie) {

        ApiResponse<MovieResponse> response = new ApiResponse<>("success","Movie successfully updated.", convertToResponse(movieService.updateMovie(id, movie)));

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResponse>> deleteMovie(@PathVariable int id) {

        ApiResponse<MovieResponse> response = new ApiResponse<>("success","Movie successfully deleted.",convertToResponse(movieService.deleteMovie(id)));
        return ResponseEntity.ok(response);
    }

    private MovieResponse convertToResponse(Movie movie) {
        MovieResponse response = new MovieResponse((movie.getId()),movie.getName(),movie.getDirector(),movie.getYear());
        return response;
    }

}

