package com.starwars.challenge.conexa.mapper;

import com.starwars.challenge.conexa.dto.external.film.SwapiFilmDetailResponse;
import com.starwars.challenge.conexa.dto.external.film.SwapiFilmListResponse;
import com.starwars.challenge.conexa.dto.external.film.SwapiFilmProperties;
import com.starwars.challenge.conexa.dto.external.film.SwapiFilmResult;
import com.starwars.challenge.conexa.dto.response.film.FilmResponse;
import com.starwars.challenge.conexa.dto.response.PagedResponse;

import java.util.List;
import java.util.stream.Collectors;

public class FilmMapper {


    public static FilmResponse toResponse(SwapiFilmDetailResponse detail) {
        return toResponse(detail.getResult());
    }

    public static PagedResponse<FilmResponse> toPagedResponse(SwapiFilmListResponse swapiResponse, int page) {
        List<SwapiFilmResult> results = swapiResponse.getResults() != null
                ? swapiResponse.getResults()
                : List.of();

        List<FilmResponse> items = results.stream()
                .map(FilmMapper::toResponse)
                .collect(Collectors.toList());

        return PagedResponse.<FilmResponse>builder()
                .items(items)
                .page(page)
                .totalPages(swapiResponse.getTotalPages())
                .totalRecords(swapiResponse.getTotalRecords() != null
                        ? swapiResponse.getTotalRecords().longValue()
                        : null)
                .build();
    }

    public static FilmResponse toResponse(SwapiFilmResult result) {
        SwapiFilmProperties properties = result.getProperties();

        return FilmResponse.builder()
                .id(result.getUid())
                .title(properties.getTitle())
                .episodeId(properties.getEpisodeId())
                .openingCrawl(properties.getOpeningCrawl())
                .director(properties.getDirector())
                .producer(properties.getProducer())
                .releaseDate(properties.getReleaseDate())
                .characterIds(properties.getCharacters())
                .starshipIds(properties.getStarships())
                .vehicleIds(properties.getVehicles())
                .build();
    }
}
