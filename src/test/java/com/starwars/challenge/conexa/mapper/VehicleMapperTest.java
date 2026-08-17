package com.starwars.challenge.conexa.mapper;

import com.starwars.challenge.conexa.dto.external.vehicle.SwapiVehicleListResponse;
import com.starwars.challenge.conexa.dto.external.vehicle.SwapiVehicleProperties;
import com.starwars.challenge.conexa.dto.external.vehicle.SwapiVehicleResult;
import com.starwars.challenge.conexa.dto.response.PagedResponse;
import com.starwars.challenge.conexa.dto.response.vehicle.VehicleResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class VehicleMapperTest {

    @Test
    void toResponse_shouldMapAllFieldsCorrectly() {
        SwapiVehicleProperties properties = SwapiVehicleProperties.builder()
                .name("Sand Crawler")
                .model("Digger Crawler")
                .vehicleClass("wheeled")
                .manufacturer("Corellia Mining Corporation")
                .crew("46")
                .passengers("30")
                .films(List.of("https://www.swapi.tech/api/films/1"))
                .pilots(List.of())
                .build();

        SwapiVehicleResult result = SwapiVehicleResult.builder()
                .uid("4")
                .properties(properties)
                .build();

        VehicleResponse response = VehicleMapper.toResponse(result);

        assertThat(response.getId()).isEqualTo("4");
        assertThat(response.getName()).isEqualTo("Sand Crawler");
        assertThat(response.getModel()).isEqualTo("Digger Crawler");
        assertThat(response.getVehicleClass()).isEqualTo("wheeled");
        assertThat(response.getCrew()).isEqualTo("46");
        assertThat(response.getPassengers()).isEqualTo("30");
        assertThat(response.getFilms()).containsExactly("https://www.swapi.tech/api/films/1");
        assertThat(response.getPilots()).isEmpty();
    }

    @Test
    void toPagedResponse_shouldMapFullListWithPagination() {
        SwapiVehicleResult vehicle1 = SwapiVehicleResult.builder()
                .uid("4")
                .properties(SwapiVehicleProperties.builder().name("Sand Crawler").build())
                .build();

        SwapiVehicleListResponse swapiResponse = SwapiVehicleListResponse.builder()
                .totalRecords(39)
                .totalPages(4)
                .results(List.of(vehicle1))
                .build();

        PagedResponse<VehicleResponse> result = VehicleMapper.toPagedResponse(swapiResponse, 1);

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getName()).isEqualTo("Sand Crawler");
        assertThat(result.getTotalRecords()).isEqualTo(39L);
    }

    @Test
    void toPagedResponse_shouldNotFail_whenResultsIsNull() {
        SwapiVehicleListResponse swapiResponse = SwapiVehicleListResponse.builder()
                .results(null)
                .build();

        PagedResponse<VehicleResponse> result = VehicleMapper.toPagedResponse(swapiResponse, 1);

        assertThat(result.getItems()).isEmpty();
    }
}
