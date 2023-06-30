package com.example.mybatisdemo.service;

import com.example.mybatisdemo.entity.Movie;
import com.example.mybatisdemo.entity.PatchMovieForm;
import com.example.mybatisdemo.exception.MovieNotFoundException;
import com.example.mybatisdemo.mapper.MovieMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class MovieServiceImplTest {


    @InjectMocks
    private MovieServiceImpl movieService;


    @Mock
    private MovieMapper movieMapper;


    @Test
    void 全ての映画が取得できること() {
        Movie movie1 = new Movie("id1","TestMovie1", "TestDirector1", 1, new BigDecimal(1), 1);
        Movie movie2 = new Movie("id2","TestMovie2", "TestDirector2", 2, new BigDecimal(2), 2);
        List<Movie> expectedMovies = Arrays.asList(movie1, movie2);
        doReturn(expectedMovies).when(movieMapper).findAll();

        List<Movie> actualMovies = movieService.getAllMovies();

        assertThat(actualMovies).isEqualTo(expectedMovies);
    }

    @Test
    void 存在する映画のIDを指定した時に正常に映画が返されること() {
        Movie expectedMovie = new Movie("id1","TestMovie1", "TestDirector1", 1, new BigDecimal(1), 1);
        doReturn(Optional.of(expectedMovie)).when(movieMapper).findOptionalById(expectedMovie.getId());

        Movie actualMovie = movieService.getMovie(expectedMovie.getId());


        assertThat(actualMovie).isEqualTo(expectedMovie);
    }


    @Test
    public void 指定のIDの映画が存在しないときに例外をThrowすること() {
        doReturn(Optional.empty()).when(movieMapper).findOptionalById("test");
        assertThatThrownBy(() -> movieService.getMovie("test"))
                .isInstanceOf(MovieNotFoundException.class)
                .hasMessage("Movie with id " + "test" + " not found.");
    }

    @Test
    void 存在する映画の年数を指定した時に正常に映画が返されること() {
        Movie movie1 = new Movie("id1","TestMovie1", "TestDirector1", 2000, new BigDecimal(1), 1);
        Movie movie2 = new Movie("id2","TestMovie2", "TestDirector2", 2000, new BigDecimal(2), 2);
        List<Movie> expectedMovies = Arrays.asList(movie1, movie2);
        doReturn(expectedMovies).when(movieMapper).findByPublishedYear(2000);

        List<Movie> actualMovies = movieService.getMoviesByPublishedYear(2000);

        assertThat(actualMovies).isEqualTo(expectedMovies);
    }


    @Test
    public void DBに存在しない年の映画を指定した時に空のリストが返されること() {
        int nonExistentYear = 9999;
        doReturn(Collections.emptyList()).when(movieMapper).findByPublishedYear(nonExistentYear);

        List<Movie> result = movieService.getMoviesByPublishedYear(nonExistentYear);

        assertThat(result.isEmpty()).isEqualTo(true);
    }



    @Test
    void 一意のIDが割り振られた上で映画を登録できること() {
        // Prepare a movie without ID
        Movie movieToInsert = new Movie(null,"TestMovie", "TestDirector", 1, new BigDecimal(1), 1);

        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            String id = (String) args[0];
            return new Movie(id, movieToInsert.getName(), movieToInsert.getDirector(), movieToInsert.getYear(), movieToInsert.getRating(), movieToInsert.getRuntime());
        }).when(movieMapper).findById(any(String.class));

        Movie actualMovie = movieService.addMovie(movieToInsert);

        assertNotNull(actualMovie.getId());

        assertEquals(movieToInsert.getName(), actualMovie.getName());
        assertEquals(movieToInsert.getDirector(), actualMovie.getDirector());
        assertEquals(movieToInsert.getYear(), actualMovie.getYear());
        assertEquals(movieToInsert.getRating(), actualMovie.getRating());
        assertEquals(movieToInsert.getRuntime(), actualMovie.getRuntime());
    }


    @Test
    void 存在するidの映画を更新できること() {
        Movie movie = new Movie("id1","TestMovie", "TestDirector", 1, new BigDecimal(1), 1);
        Movie expectedMovie = new Movie("id1","UpdatedMovie", "UpdatedDirector", 2, new BigDecimal(2), 2);
        doReturn(Optional.of(expectedMovie)).when(movieMapper).findOptionalById("id1");

        Movie actualMovie = movieService.updateMovie("id1", movie);

        assertEquals(expectedMovie, actualMovie);
    }

    @Test
    public void 更新対象の映画が存在しないときに例外をthrowすること() {
        Movie movie = new Movie("test", "TestMovie", "TestDirector", 1, new BigDecimal(1), 1);

        doReturn(Optional.empty()).when(movieMapper).findOptionalById(anyString());
        assertThatThrownBy(() -> movieService.updateMovie("test", movie))
                .isInstanceOf(MovieNotFoundException.class)
                .hasMessage("Movie with id " + "test" + " not found.");
    }

    @Test
    void 存在するidの映画を削除できること() {
        Movie expectedMovie = new Movie("id1","TestMovie", "TestDirector", 1, new BigDecimal(1), 1);
        doReturn(Optional.of(expectedMovie)).when(movieMapper).findOptionalById("id1");

        Movie actualMovie = movieService.deleteMovie("id1");

        assertEquals(expectedMovie, actualMovie);
    }

    @Test
    public void 削除対象の映画が存在しないときに例外をthrowすること() {
        Movie movie = new Movie("test", "TestMovie", "TestDirector", 1, new BigDecimal(1), 1);

        doReturn(Optional.empty()).when(movieMapper).findOptionalById("test");
        assertThatThrownBy(() -> movieService.deleteMovie("test"))
                .isInstanceOf(MovieNotFoundException.class)
                .hasMessage("Movie with id " + "test" + " not found.");
    }

    @Test
    void 存在するidの映画のデータの一部を更新できること() {
        Movie movie = new Movie("id1","TestMovie", "TestDirector", 1, new BigDecimal(1), 1);
        Movie expectedMovie = new Movie("id1","UpdatedMovie", "UpdatedDirector", 2, new BigDecimal(2), 2);
        doReturn(Optional.of(movie)).when(movieMapper).findOptionalById("id1");

        PatchMovieForm updates = new PatchMovieForm();

        updates.setName("UpdatedMovie");
        updates.setDirector("UpdatedDirector");
        updates.setYear(2);
        updates.setRating( new BigDecimal(2));
        updates.setRuntime(2);


        Movie actualMovie = movieService.patchMovie("id1", updates);

        assertEquals(expectedMovie, actualMovie);
    }



}

