package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    private MpaDbStorage mpaDbStorage;

    private Mpa firstMpa;

    private Mpa secondMpa;

    @BeforeEach
    void setUp() {

        mpaDbStorage = new MpaDbStorage(jdbcTemplate);

        firstMpa = Mpa.builder()
                .id(1)
                .name("G")
                .build();

        secondMpa = Mpa.builder()
                .id(2)
                .name("PG")
                .build();
    }

    @Test
    void getById() {
       Mpa savedGenre = mpaDbStorage.getById(firstMpa.getId());

        assertThat(savedGenre)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(firstMpa);
    }

    @Test
    void getAll() {
        List<Mpa> mpas = mpaDbStorage.getAll();

        assertThat(mpas)
                .isNotNull()
                .isNotEmpty()
                .size().isEqualTo(5);

        assertThat(mpas.get(0))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(firstMpa);

        assertThat(mpas.get(1))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(secondMpa);
    }
}