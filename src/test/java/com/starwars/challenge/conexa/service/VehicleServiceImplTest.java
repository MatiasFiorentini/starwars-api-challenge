package com.starwars.challenge.conexa.service;

import com.starwars.challenge.conexa.client.SwapiClient;
import com.starwars.challenge.conexa.dto.external.vehicle.SwapiVehicleDetailResponse;
import com.starwars.challenge.conexa.dto.external.vehicle.SwapiVehicleListResponse;
import com.starwars.challenge.conexa.dto.external.vehicle.SwapiVehicleProperties;
import com.starwars.challenge.conexa.dto.external.vehicle.SwapiVehicleResult;
import com.starwars.challenge.conexa.dto.request.vehicle.VehicleFilterRequest;
import com.starwars.challenge.conexa.dto.response.PagedResponse;
import com.starwars.challenge.conexa.dto.response.vehicle.VehicleResponse;
import com.starwars.challenge.conexa.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VehicleServiceImplTest {

    @Mock
    private SwapiClient swapiClient;

    @InjectMocks
    private VehicleService vehicleService;

    @Test
    void findVehicles_shouldCallSwapiClientWithCorrectParamsAndMapResponse() {
        VehicleFilterRequest filter = VehicleFilterRequest.builder()
                .name("speeder")
                .page(1)
                .size(10)
                .build();

        SwapiVehicleResult vehicle = SwapiVehicleResult.builder()
                .uid("6")
                .properties(SwapiVehicleProperties.builder().name("Sand Crawler").build())
                .build();

        SwapiVehicleListResponse swapiResponse = SwapiVehicleListResponse.builder()
                .totalRecords(1)
                .totalPages(1)
                .results(List.of(vehicle))
                .build();

        when(swapiClient.getPaged(
                eq("/vehicles"), eq("name"), eq("speeder"), eq(1), eq(10), eq(SwapiVehicleListResponse.class)
        )).thenReturn(swapiResponse);

        PagedResponse<VehicleResponse> result = vehicleService.findVehicles(filter);

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().getFirst().getName()).isEqualTo("Sand Crawler");
        verify(swapiClient).getPaged("/vehicles", "name", "speeder", 1, 10, SwapiVehicleListResponse.class);
    }

    @Test
    void findById_shouldCallSwapiClientWithCorrectPathAndMapResponse() {
        SwapiVehicleResult vehicle = SwapiVehicleResult.builder()
                .uid("4")
                .properties(SwapiVehicleProperties.builder().name("Sand Crawler").build())
                .build();

        SwapiVehicleDetailResponse swapiResponse = SwapiVehicleDetailResponse.builder()
                .result(vehicle)
                .build();

        when(swapiClient.get("/vehicles/4", SwapiVehicleDetailResponse.class))
                .thenReturn(swapiResponse);

        VehicleResponse result = vehicleService.findById("4");

        assertThat(result.getId()).isEqualTo("4");
        assertThat(result.getName()).isEqualTo("Sand Crawler");
    }

    @Test
    void findById_shouldPropagateException_whenResourceNotFound() {
        when(swapiClient.get("/vehicles/999", SwapiVehicleDetailResponse.class))
                .thenThrow(new ResourceNotFoundException("Resource not found in SWAPI: /vehicles/999"));

        // Act & Assert
        assertThatThrownBy(() -> vehicleService.findById("999"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("/vehicles/999");
    }
}
