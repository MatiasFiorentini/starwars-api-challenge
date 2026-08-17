package com.starwars.challenge.conexa.mapper;

import com.starwars.challenge.conexa.dto.external.person.SwapiPersonDetailResponse;
import com.starwars.challenge.conexa.dto.external.person.SwapiPersonListResponse;
import com.starwars.challenge.conexa.dto.external.person.SwapiPersonProperties;
import com.starwars.challenge.conexa.dto.external.person.SwapiPersonResult;
import com.starwars.challenge.conexa.dto.response.PagedResponse;
import com.starwars.challenge.conexa.dto.response.person.PersonResponse;

import java.util.List;
import java.util.stream.Collectors;

public class PersonMapper {

    public static PersonResponse toResponse(SwapiPersonDetailResponse detail) {
        return buildResponse(detail.getResult().getUid(), detail.getResult().getProperties());
    }

    public static PagedResponse<PersonResponse> toPagedResponse(SwapiPersonListResponse swapiResponse, int page) {
        List<SwapiPersonResult> results = swapiResponse.getResults() != null
                ? swapiResponse.getResults()
                : List.of();

        List<PersonResponse> items = results.stream()
                .map(PersonMapper::toResponse)
                .collect(Collectors.toList());

        return PagedResponse.<PersonResponse>builder()
                .items(items)
                .page(page)
                .totalPages(swapiResponse.getTotalPages())
                .totalRecords(swapiResponse.getTotalRecords() != null
                        ? swapiResponse.getTotalRecords().longValue()
                        : null)
                .build();
    }

    public static PersonResponse toResponse(SwapiPersonResult result) {
        return buildResponse(result.getUid(), result.getProperties());
    }

    private static PersonResponse buildResponse(String id, SwapiPersonProperties properties) {
        return PersonResponse.builder()
                .id(id)
                .name(properties.getName())
                .height(parseIntSafe(properties.getHeight()))
                .mass(parseDoubleSafe(properties.getMass()))
                .hairColor(properties.getHairColor())
                .eyeColor(properties.getEyeColor())
                .birthYear(properties.getBirthYear())
                .gender(properties.getGender())
                .build();
    }

    private static Integer parseIntSafe(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }

    private static Double parseDoubleSafe(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }
}
