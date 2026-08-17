package com.starwars.challenge.conexa.service;

import com.starwars.challenge.conexa.client.SwapiClient;
import com.starwars.challenge.conexa.dto.external.film.SwapiFilmDetailResponse;
import com.starwars.challenge.conexa.dto.external.film.SwapiFilmListResponse;
import com.starwars.challenge.conexa.dto.external.film.SwapiFilmProperties;
import com.starwars.challenge.conexa.dto.external.film.SwapiFilmResult;
import com.starwars.challenge.conexa.dto.request.film.FilmFilterRequest;
import com.starwars.challenge.conexa.dto.response.PagedResponse;
import com.starwars.challenge.conexa.dto.response.film.FilmResponse;
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
public class FilmServiceImplTest {

    @Mock
    private SwapiClient swapiClient;

    @InjectMocks
    private FilmService filmService;

    @Test
    void findFilms_shouldCallSwapiClientWithCorrectParamsAndMapResponse() {
        FilmFilterRequest filter = FilmFilterRequest.builder()
                .title("hope")
                .page(1)
                .size(10)
                .build();

        SwapiFilmResult film = SwapiFilmResult.builder()
                .uid("1")
                .properties(SwapiFilmProperties.builder().title("A New Hope").build())
                .build();

        SwapiFilmListResponse swapiResponse = SwapiFilmListResponse.builder()
                .totalRecords(1)
                .totalPages(1)
                .results(List.of(film))
                .build();

        when(swapiClient.getPaged(
                eq("/films"), eq("title"), eq("hope"), eq(1), eq(10), eq(SwapiFilmListResponse.class)
        )).thenReturn(swapiResponse);

        PagedResponse<FilmResponse> result = filmService.findFilms(filter);

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getTitle()).isEqualTo("A New Hope");
        verify(swapiClient).getPaged("/films", "title", "hope", 1, 10, SwapiFilmListResponse.class);
    }

    @Test
    void findById_shouldCallSwapiClientWithCorrectPathAndMapResponse() {
        SwapiFilmResult film = SwapiFilmResult.builder()
                .uid("1")
                .properties(SwapiFilmProperties.builder().title("A New Hope").build())
                .build();

        SwapiFilmDetailResponse swapiResponse = SwapiFilmDetailResponse.builder()
                .result(film)
                .build();

        when(swapiClient.get("/films/1", SwapiFilmDetailResponse.class))
                .thenReturn(swapiResponse);

        FilmResponse result = filmService.findById("1");

        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getTitle()).isEqualTo("A New Hope");
    }

    @Test
    void findById_shouldPropagateException_whenResourceNotFound() {
        when(swapiClient.get("/films/999", SwapiFilmDetailResponse.class))
                .thenThrow(new ResourceNotFoundException("Resource not found in SWAPI: /films/999"));

        assertThatThrownBy(() -> filmService.findById("999"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("/films/999");
    }
}
