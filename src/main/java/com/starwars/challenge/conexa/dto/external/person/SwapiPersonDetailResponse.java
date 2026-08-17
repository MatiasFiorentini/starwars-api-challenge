package com.starwars.challenge.conexa.dto.external.person;

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
public class SwapiPersonDetailResponse {

    private String message;
    private SwapiPersonResult result;
}
