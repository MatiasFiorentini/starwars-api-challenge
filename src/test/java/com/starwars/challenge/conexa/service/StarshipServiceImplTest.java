package com.starwars.challenge.conexa.service;

import com.starwars.challenge.conexa.client.SwapiClient;
import com.starwars.challenge.conexa.dto.external.starship.SwapiStarshipDetailResponse;
import com.starwars.challenge.conexa.dto.external.starship.SwapiStarshipListResponse;
import com.starwars.challenge.conexa.dto.external.starship.SwapiStarshipProperties;
import com.starwars.challenge.conexa.dto.external.starship.SwapiStarshipResult;
import com.starwars.challenge.conexa.dto.request.starship.StarshipFilterRequest;
import com.starwars.challenge.conexa.dto.response.PagedResponse;
import com.starwars.challenge.conexa.dto.response.starship.StarshipResponse;
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
public class StarshipServiceImplTest {

    @Mock
    private SwapiClient swapiClient;

    @InjectMocks
    private StarshipService starshipService;

    @Test
    void findStarships_shouldCallSwapiClientWithCorrectParamsAndMapResponse() {
        StarshipFilterRequest filter = StarshipFilterRequest.builder()
                .name("falcon")
                .page(1)
                .size(10)
                .build();

        SwapiStarshipResult starship = SwapiStarshipResult.builder()
                .uid("10")
                .properties(SwapiStarshipProperties.builder().name("Millennium Falcon").build())
                .build();

        SwapiStarshipListResponse swapiResponse = SwapiStarshipListResponse.builder()
                .totalRecords(1)
                .totalPages(1)
                .results(List.of(starship))
                .build();

        when(swapiClient.getPaged(
                eq("/starships"), eq("name"), eq("falcon"), eq(1), eq(10), eq(SwapiStarshipListResponse.class)
        )).thenReturn(swapiResponse);

        PagedResponse<StarshipResponse> result = starshipService.findStarships(filter);

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getName()).isEqualTo("Millennium Falcon");
        verify(swapiClient).getPaged("/starships", "name", "falcon", 1, 10, SwapiStarshipListResponse.class);
    }

    @Test
    void findById_shouldCallSwapiClientWithCorrectPathAndMapResponse() {
        SwapiStarshipResult starship = SwapiStarshipResult.builder()
                .uid("10")
                .properties(SwapiStarshipProperties.builder().name("Millennium Falcon").build())
                .build();

        SwapiStarshipDetailResponse swapiResponse = SwapiStarshipDetailResponse.builder()
                .result(starship)
                .build();

        when(swapiClient.get("/starships/10", SwapiStarshipDetailResponse.class))
                .thenReturn(swapiResponse);

        StarshipResponse result = starshipService.findById("10");

        assertThat(result.getId()).isEqualTo("10");
        assertThat(result.getName()).isEqualTo("Millennium Falcon");
    }

    @Test
    void findById_shouldPropagateException_whenResourceNotFound() {
        when(swapiClient.get("/starships/999", SwapiStarshipDetailResponse.class))
                .thenThrow(new ResourceNotFoundException("Resource not found in SWAPI: /starships/999"));

        assertThatThrownBy(() -> starshipService.findById("999"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("/starships/999");
    }
}
