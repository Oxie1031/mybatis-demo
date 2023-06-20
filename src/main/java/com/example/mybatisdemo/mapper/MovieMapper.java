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
    Optional<Movie> findById(int id);

    @Select("SELECT * FROM movies WHERE year = #{year}")
    List<Movie> findByPublishedYear(int year);


    @Insert("INSERT INTO movies(name, director, year, rating, runtime) VALUES(#{name}, #{director}, #{year}, #{rating}, #{runtime})")
    void insert(Movie movie);

    @Update("UPDATE movies SET name = #{movie.name}, director = #{movie.director}, year = #{movie.year}, rating = #{movie.rating}, runtime = #{movie.runtime} WHERE id = #{id}")
    void update(int id, Movie movie);

    @Delete("DELETE FROM movies WHERE id = #{id}")
    void delete(int id);
}
