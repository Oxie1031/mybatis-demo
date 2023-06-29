package com.example.mybatisdemo.entity;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;


public class PatchMovieForm {

    public PatchMovieForm() {
    }

    @NotBlank
    private String name;

    @NotBlank
    private String director;

    @Min(1800)
    @Max(2100)
    private Integer year;

    @DecimalMin("0.0")
    @DecimalMax("10.0")
    private BigDecimal rating;

    @Min(1)
    private Integer runtime;

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public Optional<String> getDirector() {
        return Optional.ofNullable(director);
    }

    public Optional<Integer> getYear() {
        return Optional.ofNullable(year);
    }

    public Optional<BigDecimal> getRating() {
        return Optional.ofNullable(rating);
    }

    public Optional<Integer> getRuntime() {
        return Optional.ofNullable(runtime);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatchMovieForm that = (PatchMovieForm) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(director, that.director) &&
                Objects.equals(year, that.year) &&
                Objects.equals(rating, that.rating) &&
                Objects.equals(runtime, that.runtime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, director, year, rating, runtime);
    }
}
