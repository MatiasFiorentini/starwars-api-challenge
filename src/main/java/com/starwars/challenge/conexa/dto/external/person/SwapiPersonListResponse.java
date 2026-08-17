package com.starwars.challenge.conexa.dto.external.person;

import com.fasterxml.jackson.annotation.JsonAlias;
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
public class SwapiPersonListResponse {

    private String message;

    @JsonProperty("total_records")
    private Integer totalRecords;

    @JsonProperty("total_pages")
    private Integer totalPages;

    private String previous;
    private String next;

    @JsonAlias("result")
    private List<SwapiPersonResult> results;

}
