package com.starwars.challenge.conexa.mapper;

import com.starwars.challenge.conexa.dto.external.starship.SwapiStarshipDetailResponse;
import com.starwars.challenge.conexa.dto.external.starship.SwapiStarshipListResponse;
import com.starwars.challenge.conexa.dto.external.starship.SwapiStarshipProperties;
import com.starwars.challenge.conexa.dto.external.starship.SwapiStarshipResult;
import com.starwars.challenge.conexa.dto.response.PagedResponse;
import com.starwars.challenge.conexa.dto.response.starship.StarshipResponse;

import java.util.List;
import java.util.stream.Collectors;

public class StarshipMapper {

    public static StarshipResponse toResponse(SwapiStarshipResult result) {
        SwapiStarshipProperties properties = result.getProperties();

        return StarshipResponse.builder()
                .id(result.getUid())
                .name(properties.getName())
                .model(properties.getModel())
                .starshipClass(properties.getStarshipClass())
                .manufacturer(properties.getManufacturer())
                .costInCredits(properties.getCostInCredits())
                .length(properties.getLength())
                .crew(properties.getCrew())
                .passengers(properties.getPassengers())
                .maxAtmospheringSpeed(properties.getMaxAtmospheringSpeed())
                .hyperdriveRating(properties.getHyperdriveRating())
                .mglt(properties.getMglt())
                .cargoCapacity(properties.getCargoCapacity())
                .consumables(properties.getConsumables())
                .films(properties.getFilms())
                .pilots(properties.getPilots())
                .build();
    }

    public static StarshipResponse toResponse(SwapiStarshipDetailResponse detail) {
        return toResponse(detail.getResult());
    }

    public static PagedResponse<StarshipResponse> toPagedResponse(SwapiStarshipListResponse swapiResponse, int page) {
        List<SwapiStarshipResult> results = swapiResponse.getResults() != null
                ? swapiResponse.getResults()
                : List.of();

        List<StarshipResponse> items = results.stream()
                .map(StarshipMapper::toResponse)
                .collect(Collectors.toList());

        return PagedResponse.<StarshipResponse>builder()
                .items(items)
                .page(page)
                .totalPages(swapiResponse.getTotalPages())
                .totalRecords(swapiResponse.getTotalRecords() != null
                        ? swapiResponse.getTotalRecords().longValue()
                        : null)
                .build();
    }
}
