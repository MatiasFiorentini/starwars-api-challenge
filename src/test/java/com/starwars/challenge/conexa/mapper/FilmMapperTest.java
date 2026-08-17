package com.starwars.challenge.conexa.mapper;

import com.starwars.challenge.conexa.dto.external.film.SwapiFilmListResponse;
import com.starwars.challenge.conexa.dto.external.film.SwapiFilmProperties;
import com.starwars.challenge.conexa.dto.external.film.SwapiFilmResult;
import com.starwars.challenge.conexa.dto.response.PagedResponse;
import com.starwars.challenge.conexa.dto.response.film.FilmResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FilmMapperTest {

    @Test
    void toResponse_shouldMapAllFieldsCorrectly() {
        SwapiFilmProperties properties = SwapiFilmProperties.builder()
                .title("A New Hope")
                .episodeId(4)
                .openingCrawl("It is a period of civil war...")
                .director("George Lucas")
                .producer("Gary Kurtz, Rick McCallum")
                .releaseDate("1977-05-25")
                .characters(List.of("https://www.swapi.tech/api/people/1"))
                .starships(List.of("https://www.swapi.tech/api/starships/2"))
                .vehicles(List.of("https://www.swapi.tech/api/vehicles/4"))
                .build();

        SwapiFilmResult result = SwapiFilmResult.builder()
                .uid("1")
                .properties(properties)
                .build();

        FilmResponse response = FilmMapper.toResponse(result);

        assertThat(response.getId()).isEqualTo("1");
        assertThat(response.getTitle()).isEqualTo("A New Hope");
        assertThat(response.getEpisodeId()).isEqualTo(4);
        assertThat(response.getDirector()).isEqualTo("George Lucas");
        assertThat(response.getProducer()).isEqualTo("Gary Kurtz, Rick McCallum");
        assertThat(response.getReleaseDate()).isEqualTo("1977-05-25");
        assertThat(response.getCharacterIds()).containsExactly("https://www.swapi.tech/api/people/1");
        assertThat(response.getStarshipIds()).containsExactly("https://www.swapi.tech/api/starships/2");
        assertThat(response.getVehicleIds()).containsExactly("https://www.swapi.tech/api/vehicles/4");
    }

    @Test
    void toPagedResponse_shouldMapFullListWithPagination() {
        SwapiFilmResult film1 = SwapiFilmResult.builder()
                .uid("1")
                .properties(SwapiFilmProperties.builder().title("A New Hope").build())
                .build();

        SwapiFilmListResponse swapiResponse = SwapiFilmListResponse.builder()
                .totalRecords(6)
                .totalPages(1)
                .results(List.of(film1))
                .build();

        PagedResponse<FilmResponse> result = FilmMapper.toPagedResponse(swapiResponse, 1);

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getTitle()).isEqualTo("A New Hope");
        assertThat(result.getPage()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getTotalRecords()).isEqualTo(6L);
    }

    @Test
    void toPagedResponse_shouldNotFail_whenResultsIsNull() {
        SwapiFilmListResponse swapiResponse = SwapiFilmListResponse.builder()
                .results(null)
                .build();

        PagedResponse<FilmResponse> result = FilmMapper.toPagedResponse(swapiResponse, 1);

        assertThat(result.getItems()).isEmpty();
    }
}
