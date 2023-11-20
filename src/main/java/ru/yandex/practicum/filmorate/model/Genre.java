package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Genre extends BaseUnit implements Comparable<Genre> {
    @NotBlank
    private String name;

    @Override
    public int compareTo(Genre o) {
        return getId() - o.getId();
    }
}
