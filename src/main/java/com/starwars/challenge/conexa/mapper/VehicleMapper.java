package com.starwars.challenge.conexa.mapper;

import com.starwars.challenge.conexa.dto.external.vehicle.SwapiVehicleDetailResponse;
import com.starwars.challenge.conexa.dto.external.vehicle.SwapiVehicleListResponse;
import com.starwars.challenge.conexa.dto.external.vehicle.SwapiVehicleProperties;
import com.starwars.challenge.conexa.dto.external.vehicle.SwapiVehicleResult;
import com.starwars.challenge.conexa.dto.response.PagedResponse;
import com.starwars.challenge.conexa.dto.response.vehicle.VehicleResponse;

import java.util.List;
import java.util.stream.Collectors;

public class VehicleMapper {

    public static VehicleResponse toResponse(SwapiVehicleResult result) {
        SwapiVehicleProperties properties = result.getProperties();

        return VehicleResponse.builder()
                .id(result.getUid())
                .name(properties.getName())
                .model(properties.getModel())
                .vehicleClass(properties.getVehicleClass())
                .manufacturer(properties.getManufacturer())
                .length(properties.getLength())
                .costInCredits(properties.getCostInCredits())
                .crew(properties.getCrew())
                .passengers(properties.getPassengers())
                .maxAtmospheringSpeed(properties.getMaxAtmospheringSpeed())
                .cargoCapacity(properties.getCargoCapacity())
                .consumables(properties.getConsumables())
                .films(properties.getFilms())
                .pilots(properties.getPilots())
                .build();
    }

    public static VehicleResponse toResponse(SwapiVehicleDetailResponse detail) {
        return toResponse(detail.getResult());
    }

    public static PagedResponse<VehicleResponse> toPagedResponse(SwapiVehicleListResponse swapiResponse, int page) {
        List<SwapiVehicleResult> results = swapiResponse.getResults() != null
                ? swapiResponse.getResults()
                : List.of();

        List<VehicleResponse> items = results.stream()
                .map(VehicleMapper::toResponse)
                .collect(Collectors.toList());

        return PagedResponse.<VehicleResponse>builder()
                .items(items)
                .page(page)
                .totalPages(swapiResponse.getTotalPages())
                .totalRecords(swapiResponse.getTotalRecords() != null
                        ? swapiResponse.getTotalRecords().longValue()
                        : null)
                .build();
    }
}
