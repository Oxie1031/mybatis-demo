package com.example.mybatisdemo.controller;

import com.example.mybatisdemo.entity.Movie;

public class MovieResponse {
    private String id;
    private String name;
    private String director;
    private int year;


    public MovieResponse(Movie movie) {
        this.id = movie.getId();
        this.name = movie.getName();
        this.director = movie.getDirector();
        this.year = movie.getYear();
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}


