package com.starwars.challenge.conexa.dto.external.film;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwapiFilmResult {

    private String uid;
    private String description;
    private SwapiFilmProperties properties;
}
