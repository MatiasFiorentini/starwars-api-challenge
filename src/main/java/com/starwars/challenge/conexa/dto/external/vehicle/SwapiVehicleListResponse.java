package com.starwars.challenge.conexa.dto.external.vehicle;

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
public class SwapiVehicleListResponse {

    private String message;

    @JsonProperty("total_records")
    private Integer totalRecords;

    @JsonProperty("total_pages")
    private Integer totalPages;

    @JsonAlias("result")
    private List<SwapiVehicleResult> results;
}
