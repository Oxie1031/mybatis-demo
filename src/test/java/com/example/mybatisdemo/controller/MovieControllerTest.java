package com.example.mybatisdemo.controller;

import com.example.mybatisdemo.entity.Movie;
import com.example.mybatisdemo.exception.MovieNotFoundException;
import com.example.mybatisdemo.exception.MovieValidationException;
import com.example.mybatisdemo.mapper.MovieMapper;
import com.example.mybatisdemo.service.MovieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MovieController.class)
class MovieControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    MovieService movieService;

    @Mock
    private MovieMapper movieMapper;

    @Autowired
    private ResourceLoader resourceLoader;

    private final String BASE_PATH = "json-datasets/";

    private String getJsonFileData(String fileName) throws IOException {
        var jsonResult = resourceLoader.getResource("classpath:" + BASE_PATH + fileName);
        return StreamUtils.copyToString(jsonResult.getInputStream(), StandardCharsets.UTF_8);
    }

    @Test
    public void 全ての映画を取得できること() throws Exception {
        doReturn(List.of(
                new Movie("test1", "ハリポタ", "ハリポタ・ポッタポッター", 1999, new BigDecimal(8.5), 117),
                new Movie("test2", "マルフォイ", "ルシウス・マルフォイ", 2000, new BigDecimal(1.5), 118),
                new Movie("test3", "アンブリッチ", "ドローレス・アンブリッチ", 2001, new BigDecimal(9.5), 119)
        )).when(movieService).getAllMovies();

        String actualResult = mockMvc
                .perform(get("/movies"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        String expectedResult = objectMapper.readTree(getJsonFileData("movie-all.json")).toString();
        JSONAssert.assertEquals(expectedResult, actualResult, JSONCompareMode.STRICT);
    }



    @Test
    public void 年数指定で映画を検索できること() throws Exception {
        doReturn(List.of(
                new Movie("test3", "アンブリッチ", "ドローレス・アンブリッチ", 2001, new BigDecimal(9.5), 119)
        )).when(movieService).getMoviesByPublishedYear(2001);

        String actualResult = mockMvc
                .perform(get("/movies?published_year=2001"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        String expectedResult = objectMapper.readTree(getJsonFileData("movie-year.json")).toString();
        JSONAssert.assertEquals(expectedResult, actualResult, JSONCompareMode.STRICT);

    }

    @Test
    public void 指定のIDの映画が取得できること() throws Exception {
        doReturn(
                new Movie("test2", "マルフォイ", "ルシウス・マルフォイ", 2000, new BigDecimal(1.5), 118)
        ).when(movieService).getMovie("test2");

        String actualResult = mockMvc
                .perform(get("/movies/test2"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        String expectedResult = objectMapper.readTree(getJsonFileData("movie-id.json")).toString();
        JSONAssert.assertEquals(expectedResult, actualResult, JSONCompareMode.STRICT);


    }



    @Test
    public void 映画データを登録できること() throws Exception {

        Movie newMovie = new Movie("test1", "ハリポタ", "ハリポタ・ポッタポッター", 1999, new BigDecimal(8.5), 117);
        doReturn(
                new Movie("test1", "ハリポタ", "ハリポタ・ポッタポッター", 1999, new BigDecimal(8.5), 117)
        ).when(movieService).addMovie(newMovie);

        String actualResult = mockMvc
                .perform(post("/movies")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new Movie("test1", "ハリポタ", "ハリポタ・ポッタポッター", 1999, new BigDecimal(8.5), 117))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        String expectedResult = objectMapper.readTree(getJsonFileData("movie-create.json")).toString();
        JSONAssert.assertEquals(expectedResult, actualResult, JSONCompareMode.STRICT);
    }


    @Test
    public void 映画データを置き換えできること() throws Exception {
        Movie mockMovie = new Movie("test1", "ハリポタ", "ハリポタ・ポッタポッター", 1999, new BigDecimal(8.5), 117);
        Optional<Movie> optMockMovie = Optional.of(mockMovie);

        Movie updatedMovie = new Movie("test1", "セブルス", "セブルス・スネイプ", 1999, new BigDecimal(8.5), 117);


        doReturn(optMockMovie).when(movieMapper).findOptionalById("test1");
        doReturn(updatedMovie).when(movieService).updateMovie("test1", updatedMovie);

        String actualResult = mockMvc
                .perform(put("/movies/test1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedMovie)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        ApiResponse<MovieResponse> expectedApiResponse = new ApiResponse<>("success", "Movie successfully updated.", new MovieResponse(updatedMovie));
        String expectedResult = objectMapper.writeValueAsString(expectedApiResponse);

        JSONAssert.assertEquals(expectedResult, actualResult, JSONCompareMode.STRICT);
    }

    @Test
    public void 映画データを部分的に更新できること() throws Exception {
        Movie mockMovie = new Movie("test1", "ハリポタ", "ハリポタ・ポッタポッター", 1999, new BigDecimal(8.5), 117);
        Optional<Movie> optMockMovie = Optional.of(mockMovie);

        Movie updatedMovie = new Movie("test1", "セブルス", "セブルス・スネイプ", 1999, new BigDecimal(8.5), 117);

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "セブルス");
        updates.put("director", "セブルス・スネイプ");
        updates.put("year", 1999);

        doReturn(optMockMovie).when(movieMapper).findOptionalById("test1");
        doReturn(updatedMovie).when(movieService).patchMovie("test1", updates);

        String actualResult = mockMvc
                .perform(patch("/movies/test1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        ApiResponse<MovieResponse> expectedApiResponse = new ApiResponse<>("success", "Movie successfully updated.", new MovieResponse(updatedMovie));
        String expectedResult = objectMapper.writeValueAsString(expectedApiResponse);

        JSONAssert.assertEquals(expectedResult, actualResult, JSONCompareMode.STRICT);
    }




    @Test
    public void 映画データを削除できること() throws Exception {
        Movie movie = new Movie("test1", "ハリポタ", "ハリポタ・ポッタポッター", 1999, new BigDecimal(8.5), 117);
        Optional<Movie> deleteMovie = Optional.of(movie);

        Movie deletedMovie = new Movie("test1", "ハリポタ", "ハリポタ・ポッタポッター",1999, new BigDecimal(8.5), 117);


        doReturn(deleteMovie).when(movieMapper).findOptionalById("test1");
        doReturn(deletedMovie).when(movieService).deleteMovie("test1");

        String actualResult = mockMvc
                .perform(delete("/movies/test1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deletedMovie)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        ApiResponse<MovieResponse> expectedApiResponse = new ApiResponse<>("success", "Movie successfully deleted.", new MovieResponse(deletedMovie));
        String expectedResult = objectMapper.writeValueAsString(expectedApiResponse);

        JSONAssert.assertEquals(expectedResult, actualResult, JSONCompareMode.STRICT);
    }

    @Test
    public void 不正な映画データのリクエストを送信した際にエラーを返すこと() throws Exception {
        Movie mockMovie = new Movie("test1", "ハリポタ", "ハリポタ・ポッタポッター", 1999, new BigDecimal(8.5), 117);
        Optional<Movie> optMockMovie = Optional.of(mockMovie);

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "");
        updates.put("director", "");
        updates.put("year", 19999);

        doReturn(optMockMovie).when(movieMapper).findOptionalById("test1");
        doThrow(MovieValidationException.class).when(movieService).patchMovie("test1", updates);

        mockMvc.perform(patch("/movies/test1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void 映画データが存在しないidを指定された場合にエラーを返すこと() throws Exception {
        String nonExistentId = "nonExistentId";

        doReturn(Optional.empty()).when(movieMapper).findOptionalById(nonExistentId);
        doThrow(new MovieNotFoundException("Movie with id " + nonExistentId + " not found.")).when(movieService).deleteMovie(nonExistentId);

        mockMvc.perform(delete("/movies/" + nonExistentId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("404"));
    }

}


