package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class Film extends BaseUnit {

    @NotEmpty
    private String name;

    @Size(max = 200)
    private String description;

    @JsonIgnore
    @Builder.Default
    private int rate = 0;

    @NotNull
    private LocalDate releaseDate;

    @Builder.Default
    private List<Integer> likes = new ArrayList<>();

    @NotNull
    private Mpa mpa;

    @Positive
    @Min(1)
    private int duration;

    @Builder.Default
    private Set<Genre> genres = new HashSet<>();

      public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("film_id", getId());
        values.put("film_name", name);
        values.put("description", description);
        values.put("release_date", releaseDate);
        values.put("duration", duration);
        values.put("mpa_id", mpa.getId());

        return values;
    }
}
