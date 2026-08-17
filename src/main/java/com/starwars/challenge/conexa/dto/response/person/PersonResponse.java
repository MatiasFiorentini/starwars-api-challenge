package com.starwars.challenge.conexa.dto.response.person;

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
public class PersonResponse {

    private String id;
    private String name;
    private Integer height;
    private Double mass;
    private String hairColor;
    private String eyeColor;
    private String birthYear;
    private String gender;
}
