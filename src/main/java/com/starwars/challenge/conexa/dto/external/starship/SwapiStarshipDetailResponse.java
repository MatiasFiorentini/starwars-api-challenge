package com.starwars.challenge.conexa.dto.external.starship;

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
public class SwapiStarshipDetailResponse {

    private String message;
    private SwapiStarshipResult result;
}
