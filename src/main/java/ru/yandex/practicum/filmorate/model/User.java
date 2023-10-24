package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class User extends BaseUnit {
    @Email
    @NotEmpty
    @NotBlank
    private String email;

    @NotNull
    @NotBlank
    private String login;

    private String name;

    @JsonIgnore
    private List<User> friends = new ArrayList<>();

    @PastOrPresent
    private LocalDate birthday;
}
