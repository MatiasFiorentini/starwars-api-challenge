package com.starwars.challenge.conexa.dto.external.person;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwapiPersonProperties {

    private String name;

    private String height;

    private String mass;

    @JsonProperty("hair_color")
    private String hairColor;

    @JsonProperty("eye_color")
    private String eyeColor;

    @JsonProperty("birth_year")
    private String birthYear;

    private String gender;

    @JsonProperty("skin_color")
    private String skinColor;

    private String homeworld;

    private List<String>  films;

    private List<String> species;

    private List<String> vehicles;

    private List<String> starships;

    private String url;

    private String created;

    private String edited;
}
