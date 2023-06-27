package com.example.mybatisdemo.service;

import com.example.mybatisdemo.entity.Movie;

import java.util.List;
import java.util.Map;

public interface MovieService {
    List<Movie> getAllMovies();
    Movie getMovie(String  id);
    List<Movie> getMoviesByPublishedYear(int year);
    Movie addMovie(Movie movie);
    Movie updateMovie(String  id, Movie movie);
    Movie deleteMovie(String  id);


    /**
     * PUTとPATCH処理の練習のため、機能は重複するがサンプルとして作成
     */
    Movie patchMovie(String  id, Map<String, Object> updates);

}

