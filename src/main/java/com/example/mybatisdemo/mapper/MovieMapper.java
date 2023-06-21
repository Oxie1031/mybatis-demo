package com.example.mybatisdemo.mapper;

import com.example.mybatisdemo.entity.Movie;
import com.example.mybatisdemo.exception.MovieNotFoundException;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.*;
import  java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.*;



@Mapper
public interface MovieMapper {
    @Select("SELECT * FROM movies")
    List<Movie> findAll();

    @Select("SELECT * FROM movies WHERE id = #{id}")
    Optional<Movie> findOptionalById(String id);

    @Select("SELECT * FROM movies WHERE id = #{id}")
    Movie findById(String id);

    @Select("SELECT id FROM movies ORDER BY id DESC LIMIT 1")
    int getLatestMovieId();

    @Select("SELECT * FROM movies WHERE year = #{year}")
    List<Movie> findByPublishedYear(int year);

    @Insert("INSERT INTO movies(id, name, director, year, rating, runtime) VALUES(#{id}, #{name}, #{director}, #{year}, #{rating}, #{runtime})")
    void insert(Movie movie);

    @Update("UPDATE movies SET name = #{movie.name}, director = #{movie.director}, year = #{movie.year}, rating = #{movie.rating}, runtime = #{movie.runtime} WHERE id = #{id}")
    void update(String id, Movie movie);

    @Delete("DELETE FROM movies WHERE id = #{id}")
    void delete(String  id);
}
