package com.example.mybatisdemo.integration;


import com.example.mybatisdemo.entity.Movie;
import com.example.mybatisdemo.entity.PatchMovieForm;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@DBRider
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class MovieRestApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResourceLoader resourceLoader;

    private final String BASE_PATH = "json-datasets/";

    private String getJsonFileData(String fileName) throws IOException {
        var jsonResult = resourceLoader.getResource("classpath:" + BASE_PATH + fileName);
        return StreamUtils.copyToString(jsonResult.getInputStream(), StandardCharsets.UTF_8);
    }

    @Test
    @DataSet(value = "datasets/movieList.yml")
    void 映画が全権取得できること() throws Exception {
        String actualResult = mockMvc
                .perform(MockMvcRequestBuilders.get("/movies"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        String expectedResult = objectMapper.readTree(getJsonFileData("movie-all.json")).toString();
        JSONAssert.assertEquals(expectedResult, actualResult, JSONCompareMode.STRICT);
    }


    @Test
    @DataSet(value = "datasets/movieList.yml")
    void 指定したidの映画が取得できること() throws Exception {
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
    @DataSet(value = "datasets/movieList.yml")
    void 指定した年の映画が取得できること() throws Exception {
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
    @DataSet(value = "datasets/movieList.yml")
    void 指定した年の映画が存在しない場合_空のリストを返すこと() throws Exception {
        String actualResult = mockMvc
                .perform(get("/movies?published_year=2008"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        String expectedResult = "[]";
        JSONAssert.assertEquals(expectedResult, actualResult, JSONCompareMode.STRICT);
    }

    @Test
    void published_yearに不正な値を入力したら400エラーが返されること() throws Exception {
        mockMvc.perform(get("/movies?published_year=aaa"))
                .andExpect(status().isBadRequest());
    }


    @Test
    void 存在しないIDの映画を取得しようとすると404エラーが返されること() throws Exception {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(2023, 6, 28, 13, 0, 0, 0, ZoneId.of("Asia/Tokyo"));
        try(MockedStatic<ZonedDateTime> zonedDateTimeMockedStatic = Mockito.mockStatic(ZonedDateTime.class)){
            zonedDateTimeMockedStatic.when(ZonedDateTime::now).thenReturn(zonedDateTime);

            String actualResult = mockMvc.perform(get("/movies/100"))
                    .andExpect(status().isNotFound())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            String expectedResult = objectMapper.readTree(getJsonFileData("movie-404.json")).toString();
            JSONAssert.assertEquals(expectedResult, actualResult, JSONCompareMode.STRICT);

        }
    }


    @Test
    @Transactional
    @DataSet(value = "datasets/movieList.yml")
    @ExpectedDataSet(value = "datasets/moviePost.yml",  ignoreCols = "id")
    void 映画を登録できること() throws Exception {

        Movie insertData = new Movie("test4", "新作映画", "新作・映画", 2024, new BigDecimal(9.5), 120);


        String actualResult = mockMvc
                .perform(post("/movies")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(insertData)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);


        // actualResultをJsonNodeとして解析
        JsonNode actualJson = objectMapper.readTree(actualResult);

        // dataフィールドが存在することを確認
        assertTrue(actualJson.has("data"), "Expected data field in the response");

        // dataフィールドの下のidフィールドが存在することを確認
        JsonNode dataJson = actualJson.get("data");
        assertTrue(dataJson.has("id"), "Expected id field in the data field");

        // idフィールドの値を"test4"に変更
        ((ObjectNode)dataJson).put("id", "test4");

        String expectedResult = objectMapper.readTree(getJsonFileData("movie-api-insert.json")).toString();
        JSONAssert.assertEquals(expectedResult, actualJson.toString(), JSONCompareMode.STRICT);
    }

    @Test
    public void 不正な内容で映画を新規登録するとエラーが返されること() throws Exception {

        ZonedDateTime zonedDateTime = ZonedDateTime.of(2023, 6, 28, 13, 0, 0, 0, ZoneId.of("Asia/Tokyo"));
        try (MockedStatic<ZonedDateTime> zonedDateTimeMockedStatic = Mockito.mockStatic(ZonedDateTime.class)) {
            zonedDateTimeMockedStatic.when(ZonedDateTime::now).thenReturn(zonedDateTime);

            Map<String, Object> updates = new HashMap<>();
            updates.put("name", "");
            updates.put("director", "");
            updates.put("year", 19999);
            updates.put("rating", 100);
            updates.put("runtime", 0);

            String actualResult = mockMvc
                    .perform(post("/movies")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updates)))
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            JsonNode actualJson = objectMapper.readTree(actualResult);
            JsonNode errorsNode = actualJson.get("errors");

            // 必要なエラーメッセージとフィールドを保持するマップを作成します
            Map<String, String> expectedErrors = new HashMap<>();
            expectedErrors.put("director", "must not be blank");
            expectedErrors.put("rating", "must be less than or equal to 10.0");
            expectedErrors.put("runtime", "must be greater than or equal to 1");
            expectedErrors.put("year", "must be less than or equal to 2100");
            expectedErrors.put("name", "must not be blank");

            // レスポンスから得られたエラーをループ処理
            for (JsonNode errorNode : errorsNode) {
                String field = errorNode.get("field").asText();
                String message = errorNode.get("defaultMessage").asText();

                // 得られたエラーメッセージが期待したエラーと一致するかどうかを確認
                assertTrue(expectedErrors.containsKey(field));
                assertEquals(expectedErrors.get(field), message);

                // 確認したエラーをマップから削除
                expectedErrors.remove(field);
            }

            // すべてのエラーが確認され、マップが空になっていることを確認
            assertTrue(expectedErrors.isEmpty());
        }
    }


    @Test
    @Transactional
    @DataSet(value = "datasets/movieList.yml")
    @ExpectedDataSet(value = "datasets/movieUpdateList.yml", orderBy = "id")
    void 映画データを更新できること() throws Exception {
        String actualResult = mockMvc.perform(put("/movies/test2")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new Movie("test2", "ターミネーター", "ジェームズ・キャメロン", 1984, new BigDecimal(8.5), 108))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);


        // Convert the actual result to a JSON object
        JsonNode actualJson = objectMapper.readTree(actualResult);
        String expectedResult = objectMapper.readTree(getJsonFileData("movie-update.json")).toString();
        JSONAssert.assertEquals(expectedResult, actualJson.toString(), JSONCompareMode.STRICT);
    }



    @Test
    public void 不正な内容で映画を更新するとエラーが返されること() throws Exception {

        ZonedDateTime zonedDateTime = ZonedDateTime.of(2023, 6, 28, 13, 0, 0, 0, ZoneId.of("Asia/Tokyo"));
        try (MockedStatic<ZonedDateTime> zonedDateTimeMockedStatic = Mockito.mockStatic(ZonedDateTime.class)) {
            zonedDateTimeMockedStatic.when(ZonedDateTime::now).thenReturn(zonedDateTime);

            Map<String, Object> updates = new HashMap<>();
            updates.put("name", "");
            updates.put("director", "");
            updates.put("year", 1700);
            updates.put("rating", 20);
            updates.put("runtime", 0);

            String actualResult = mockMvc
                    .perform(put("/movies/test1")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updates)))
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            JsonNode actualJson = objectMapper.readTree(actualResult);
            JsonNode errorsNode = actualJson.get("errors");

            // 必要なエラーメッセージとフィールドを保持するマップを作成します
            Map<String, String> expectedErrors = new HashMap<>();
            expectedErrors.put("director", "must not be blank");
            expectedErrors.put("rating", "must be less than or equal to 10.0");
            expectedErrors.put("runtime", "must be greater than or equal to 1");
            expectedErrors.put("year", "must be greater than or equal to 1800");
            expectedErrors.put("name", "must not be blank");

            // レスポンスから得られたエラーをループ処理
            for (JsonNode errorNode : errorsNode) {
                String field = errorNode.get("field").asText();
                String message = errorNode.get("defaultMessage").asText();

                // 得られたエラーメッセージが期待したエラーと一致するかどうかを確認
                assertTrue(expectedErrors.containsKey(field));
                assertEquals(expectedErrors.get(field), message);

                // 確認したエラーをマップから削除
                expectedErrors.remove(field);
            }

            // すべてのエラーが確認され、マップが空になっていることを確認
            assertTrue(expectedErrors.isEmpty());
        }
    }

    @Test
    @Transactional
    @DataSet(value = "movieList.yml")
    void 存在しないIDの映画を更新した際にエラーが返されること() throws Exception {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(2023, 6, 28, 13, 0, 0, 0, ZoneId.of("Asia/Tokyo"));
        try(MockedStatic<ZonedDateTime> zonedDateTimeMockedStatic = Mockito.mockStatic(ZonedDateTime.class)){
            zonedDateTimeMockedStatic.when(ZonedDateTime::now).thenReturn(zonedDateTime);


            String actualResult = mockMvc.perform(put("/movies/100")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    new Movie("test2", "ターミネーター", "ジェームズ・キャメロン", 1984, new BigDecimal(8.5), 108))))
                    .andExpect(status().isNotFound())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            JsonNode actualJson = objectMapper.readTree(actualResult);
            String expectedResult = objectMapper.readTree(getJsonFileData("movie-404.json")).toString();
            JSONAssert.assertEquals(expectedResult, actualJson.toString(), JSONCompareMode.STRICT);

        }
    }

    @Test
    @Transactional
    @DataSet(value = "datasets/movieList.yml")
    @ExpectedDataSet(value = "datasets/moviePartiallyUpdatedList.yml", orderBy = "id")
    void 映画データの一部部分のみを更新できること() throws Exception {

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("name", "ターミネーター");
        updateData.put("director", "ジェームズ・キャメロン");


        String actualResult = mockMvc.perform(patch("/movies/test2")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);


        // Convert the actual result to a JSON object
        JsonNode actualJson = objectMapper.readTree(actualResult);
        String expectedResult = objectMapper.readTree(getJsonFileData("movie-partially-update.json")).toString();
        JSONAssert.assertEquals(expectedResult, actualJson.toString(), JSONCompareMode.STRICT);
    }


    @Test
    @Transactional
    @DataSet(value = "movieList.yml")
    void 存在しないIDの映画のデータを一部更新した際にエラーが返されること() throws Exception {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(2023, 6, 28, 13, 0, 0, 0, ZoneId.of("Asia/Tokyo"));
        try(MockedStatic<ZonedDateTime> zonedDateTimeMockedStatic = Mockito.mockStatic(ZonedDateTime.class)){
            zonedDateTimeMockedStatic.when(ZonedDateTime::now).thenReturn(zonedDateTime);


            Map<String, Object> updateData = new HashMap<>();
            updateData.put("name", "ターミネーター");
            updateData.put("director", "ジェームズ・キャメロン");


            String actualResult = mockMvc.perform(patch("/movies/100")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateData)))
                    .andExpect(status().isNotFound())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            JsonNode actualJson = objectMapper.readTree(actualResult);
            String expectedResult = objectMapper.readTree(getJsonFileData("movie-404.json")).toString();
            JSONAssert.assertEquals(expectedResult, actualJson.toString(), JSONCompareMode.STRICT);

        }
    }

    @Test
    public void 空の名前で映画のnameデータを更新するとエラーが返されること() throws Exception {

        ZonedDateTime zonedDateTime = ZonedDateTime.of(2023, 6, 28, 13, 0, 0, 0, ZoneId.of("Asia/Tokyo"));
        try (MockedStatic<ZonedDateTime> zonedDateTimeMockedStatic = Mockito.mockStatic(ZonedDateTime.class)) {
            zonedDateTimeMockedStatic.when(ZonedDateTime::now).thenReturn(zonedDateTime);

            PatchMovieForm updates = new PatchMovieForm();
            updates.setName("");
            updates.setDirector("test");
            updates.setYear(2000);


            String actualResult = mockMvc
                    .perform(patch("/movies/test1")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updates)))
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            String expectedResult = objectMapper.readTree(getJsonFileData("movie-name-blank.json")).toString();
            JSONAssert.assertEquals(expectedResult, actualResult, JSONCompareMode.STRICT);

        }
    }

    @Test
    public void 空のディレクター名で映画のdirectorデータを更新するとエラーが返されること() throws Exception {

        ZonedDateTime zonedDateTime = ZonedDateTime.of(2023, 6, 28, 13, 0, 0, 0, ZoneId.of("Asia/Tokyo"));
        try (MockedStatic<ZonedDateTime> zonedDateTimeMockedStatic = Mockito.mockStatic(ZonedDateTime.class)) {
            zonedDateTimeMockedStatic.when(ZonedDateTime::now).thenReturn(zonedDateTime);

            PatchMovieForm updates = new PatchMovieForm();
            updates.setName("test");
            updates.setDirector("");
            updates.setYear(2000);


            String actualResult = mockMvc
                    .perform(patch("/movies/test1")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updates)))
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            String expectedResult = objectMapper.readTree(getJsonFileData("movie-director-blank.json")).toString();
            JSONAssert.assertEquals(expectedResult, actualResult, JSONCompareMode.STRICT);

        }
    }

    @Test
    public void 不正な年で映画のyearデータを更新するとエラーが返されること() throws Exception {

        ZonedDateTime zonedDateTime = ZonedDateTime.of(2023, 6, 28, 13, 0, 0, 0, ZoneId.of("Asia/Tokyo"));
        try (MockedStatic<ZonedDateTime> zonedDateTimeMockedStatic = Mockito.mockStatic(ZonedDateTime.class)) {
            zonedDateTimeMockedStatic.when(ZonedDateTime::now).thenReturn(zonedDateTime);

            PatchMovieForm updates = new PatchMovieForm();
            updates.setName("test");
            updates.setDirector("test");
            updates.setYear(20000);


            String actualResult = mockMvc
                    .perform(patch("/movies/test1")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updates)))
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            String expectedResult = objectMapper.readTree(getJsonFileData("movie-invalid-year.json")).toString();
            JSONAssert.assertEquals(expectedResult, actualResult, JSONCompareMode.STRICT);

        }
    }

    @Test
    public void 不正なレートで映画のratingデータを更新するとエラーが返されること() throws Exception {

        ZonedDateTime zonedDateTime = ZonedDateTime.of(2023, 6, 28, 13, 0, 0, 0, ZoneId.of("Asia/Tokyo"));
        try (MockedStatic<ZonedDateTime> zonedDateTimeMockedStatic = Mockito.mockStatic(ZonedDateTime.class)) {
            zonedDateTimeMockedStatic.when(ZonedDateTime::now).thenReturn(zonedDateTime);

            PatchMovieForm updates = new PatchMovieForm();
            updates.setName("test");
            updates.setDirector("test");
            updates.setYear(2000);
            updates.setRating(new BigDecimal(20));

            String actualResult = mockMvc
                    .perform(patch("/movies/test1")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updates)))
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            String expectedResult = objectMapper.readTree(getJsonFileData("movie-invalid-rating.json")).toString();
            JSONAssert.assertEquals(expectedResult, actualResult, JSONCompareMode.STRICT);
        }
    }

    @Test
    public void 不正な上映時間で映画のruntimeデータを更新するとエラーが返されること() throws Exception {

        ZonedDateTime zonedDateTime = ZonedDateTime.of(2023, 6, 28, 13, 0, 0, 0, ZoneId.of("Asia/Tokyo"));
        try (MockedStatic<ZonedDateTime> zonedDateTimeMockedStatic = Mockito.mockStatic(ZonedDateTime.class)) {
            zonedDateTimeMockedStatic.when(ZonedDateTime::now).thenReturn(zonedDateTime);

            PatchMovieForm updates = new PatchMovieForm();
            updates.setName("test");
            updates.setDirector("test");
            updates.setYear(2000);
            updates.setRating(new BigDecimal(10));
            updates.setRuntime(0);

            String actualResult = mockMvc
                    .perform(patch("/movies/test1")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updates)))
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            String expectedResult = objectMapper.readTree(getJsonFileData("movie-invalid-runtime.json")).toString();
            JSONAssert.assertEquals(expectedResult, actualResult, JSONCompareMode.STRICT);
        }
    }



    @Test
    @Transactional
    @DataSet(value = "movieList.yml")
    @ExpectedDataSet(value = "movieDeleteList.yml")
    void 指定したIDの映画が削除できること() throws Exception {
        String actualResult = mockMvc.perform(delete("/movies/test1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        JsonNode actualJson = objectMapper.readTree(actualResult);
        String expectedResult = objectMapper.readTree(getJsonFileData("movie-delete.json")).toString();
        JSONAssert.assertEquals(expectedResult, actualJson.toString(), JSONCompareMode.STRICT);

    }


    @Test
    @Transactional
    @DataSet(value = "movieList.yml")
    void 存在しないIDの映画を削除した際にエラーが返されること() throws Exception {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(2023, 6, 28, 13, 0, 0, 0, ZoneId.of("Asia/Tokyo"));
        try(MockedStatic<ZonedDateTime> zonedDateTimeMockedStatic = Mockito.mockStatic(ZonedDateTime.class)){
            zonedDateTimeMockedStatic.when(ZonedDateTime::now).thenReturn(zonedDateTime);

            String actualResult = mockMvc.perform(delete("/movies/100")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

            JsonNode actualJson = objectMapper.readTree(actualResult);
            String expectedResult = objectMapper.readTree(getJsonFileData("movie-404.json")).toString();
            JSONAssert.assertEquals(expectedResult, actualJson.toString(), JSONCompareMode.STRICT);

        }
    }
}

