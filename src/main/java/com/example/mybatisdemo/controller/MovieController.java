package com.example.mybatisdemo.controller;

import com.example.mybatisdemo.entity.Movie;
import com.example.mybatisdemo.entity.PatchMovieForm;
import com.example.mybatisdemo.service.MovieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
                .map(MovieResponse::new)
                .collect(Collectors.toList());
    }


    @GetMapping("/{id}")
    public MovieResponse getMovie(@PathVariable String id) {
       return new MovieResponse(movieService.getMovie(id));
    }

    @GetMapping(params = "published_year")
    public List<MovieResponse> getMoviesByPublishedYear(@RequestParam("published_year") int year) {
        return movieService.getMoviesByPublishedYear(year)
                .stream().map(MovieResponse::new)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MovieResponse>> addMovie(@Validated @RequestBody Movie movie) {

        ApiResponse<MovieResponse> response = new ApiResponse<>("success", "Movie successfully added.", new MovieResponse(movieService.addMovie(movie)));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResponse>> patchMovie(@PathVariable String id, @Validated @RequestBody PatchMovieForm updates) {
        ApiResponse<MovieResponse> response = new ApiResponse<>("success", "Movie successfully updated.", new MovieResponse(movieService.patchMovie(id, updates)));

        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResponse>> updateMovie(@PathVariable String id, @Validated @RequestBody Movie movie) {

        ApiResponse<MovieResponse> response = new ApiResponse<>("success","Movie successfully updated.", new MovieResponse(movieService.updateMovie(id, movie)));

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResponse>> deleteMovie(@PathVariable String id) {

        ApiResponse<MovieResponse> response = new ApiResponse<>("success","Movie successfully deleted.",new MovieResponse(movieService.deleteMovie(id)));
        return ResponseEntity.ok(response);
    }

}

