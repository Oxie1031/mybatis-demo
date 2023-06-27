package com.example.mybatisdemo.entity;


import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Objects;


public class Movie {
    private String id; //後日UUIDに変更
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
    @DecimalMin("0.0")
    @DecimalMax("10.0")
    private BigDecimal rating;

    @NotNull
    @Min(1)
    private int runtime;


    public Movie(String id, String name, String director, int year, BigDecimal rating, int runtime) {
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

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Movie other = (Movie) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(name, other.name)
                && Objects.equals(director, other.director)
                && year == other.year
                && Objects.equals(rating, other.rating)
                && runtime == other.runtime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, director, year, rating, runtime);
    }

}

