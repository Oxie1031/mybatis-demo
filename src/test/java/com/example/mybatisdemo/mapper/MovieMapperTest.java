package com.example.mybatisdemo.mapper;

import com.example.mybatisdemo.entity.Movie;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.junit5.api.DBRider;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DBRider
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MovieMapperTest {
    @Autowired
    MovieMapper movieMapper;

    @Test
    @DataSet(value = "movieList.yml")
    @Transactional
    public void すべての映画が取得できること() {
        List<Movie> movieList = movieMapper.findAll();
        assertThat(movieList)
                .hasSize(3)
                .containsExactly(
                        new Movie("test1", "ハリポタ", "ハリポタ・ポッタポッター", 1999, new BigDecimal(8.5), 117),
                        new Movie("test2", "マルフォイ", "ルシウス・マルフォイ", 2000, new BigDecimal(1.5), 118),
                        new Movie("test3", "アンブリッチ", "ドローレス・アンブリッチ", 2001, new BigDecimal(9.5), 119)
                );
    }

    @Test
    @DataSet(value = "movieEmpty.yml")
    @Transactional
    public void 映画が存在しない時は空のListを返すこと() {
        List<Movie> movieList = movieMapper.findAll();
        assertThat(movieList).isEmpty();
    }

    @Test
    @DataSet(value = "movieList.yml")
    @Transactional
    public void 指定の年の映画が取得できること() {
        List<Movie> movieList = movieMapper.findByPublishedYear(2000);
        assertThat(movieList)
                .hasSize(1)
                .containsExactly(
                        new Movie("test2", "マルフォイ", "ルシウス・マルフォイ", 2000, new BigDecimal(1.5), 118)
                );
    }

    @Test
    @DataSet(value = "movieList.yml")
    @Transactional
    public void 指定のIDの映画が取得できること() {
        Optional<Movie> movie = movieMapper.findOptionalById("test1");
        assertThat(movie).contains(
                new Movie("test1", "ハリポタ", "ハリポタ・ポッタポッター", 1999, new BigDecimal(8.5), 117)
        );
    }

    @Test
    @DataSet(value = "movieList.yml")
    @Transactional
    public void 指定したidの映画が存在しない時空のOptionalを返すこと() {
        Optional<Movie> movie = movieMapper.findOptionalById("test100");
        assertThat(movie).isEmpty();
    }


    @Test
    @Transactional
    @DataSet(value = "movieList.yml")
    @ExpectedDataSet(value = "movieUpdateList.yml")
    public void 映画情報を上書きできること() {
        Movie updateMovie = new Movie("test2", "ターミネーター", "ジェームズ・キャメロン", 1984, new BigDecimal(8.4), 108);
        movieMapper.update("test2", updateMovie);
    }
    @Test
    @Transactional
    @DataSet(value = "movieList.yml")
    @ExpectedDataSet(value = "movieDeleteList.yml")
    public void 映画を削除できること() {
        movieMapper.delete("test1");
    }

}

