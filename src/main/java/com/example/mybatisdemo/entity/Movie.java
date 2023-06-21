package com.example.mybatisdemo.entity;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;



public class Movie {
    private String id;
    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @NotBlank
    private String director;

    @NotNull
    @Min(1800)
    @Max(2100)
    private int year;

    @NotNull
    @Min(0)
    @Max(10)
    private double rating;

    @NotNull
    @Min(1)
    private int runtime;


    public Movie(String id, String name, String director, int year, double rating, int runtime) {
        this.id = id;
        this.name = name;
        this.director = director;
        this.year = year;
        this.rating = rating;
        this.runtime = runtime;
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

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", director='" + director + '\'' +
                ", year=" + year +
                ", rating=" + rating +
                ", runtime=" + runtime +
                '}';
    }
}
