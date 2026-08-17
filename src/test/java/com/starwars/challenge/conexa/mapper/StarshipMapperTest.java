package com.starwars.challenge.conexa.mapper;

import com.starwars.challenge.conexa.dto.external.starship.SwapiStarshipListResponse;
import com.starwars.challenge.conexa.dto.external.starship.SwapiStarshipProperties;
import com.starwars.challenge.conexa.dto.external.starship.SwapiStarshipResult;
import com.starwars.challenge.conexa.dto.response.PagedResponse;
import com.starwars.challenge.conexa.dto.response.starship.StarshipResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class StarshipMapperTest {

    @Test
    void toResponse_shouldMapAllFieldsCorrectly() {
        SwapiStarshipProperties properties = SwapiStarshipProperties.builder()
                .name("Millennium Falcon")
                .model("YT-1300 light freighter")
                .starshipClass("Light freighter")
                .manufacturer("Corellian Engineering Corporation")
                .crew("4")
                .passengers("6")
                .hyperdriveRating("0.5")
                .films(List.of("https://www.swapi.tech/api/films/1"))
                .pilots(List.of("https://www.swapi.tech/api/people/13"))
                .build();

        SwapiStarshipResult result = SwapiStarshipResult.builder()
                .uid("10")
                .properties(properties)
                .build();

        StarshipResponse response = StarshipMapper.toResponse(result);

        assertThat(response.getId()).isEqualTo("10");
        assertThat(response.getName()).isEqualTo("Millennium Falcon");
        assertThat(response.getModel()).isEqualTo("YT-1300 light freighter");
        assertThat(response.getStarshipClass()).isEqualTo("Light freighter");
        assertThat(response.getCrew()).isEqualTo("4");
        assertThat(response.getPassengers()).isEqualTo("6");
        assertThat(response.getHyperdriveRating()).isEqualTo("0.5");
        assertThat(response.getFilms()).containsExactly("https://www.swapi.tech/api/films/1");
        assertThat(response.getPilots()).containsExactly("https://www.swapi.tech/api/people/13");
    }

    @Test
    void toPagedResponse_shouldMapFullListWithPagination() {
        SwapiStarshipResult starship1 = SwapiStarshipResult.builder()
                .uid("9")
                .properties(SwapiStarshipProperties.builder().name("Death Star").build())
                .build();

        SwapiStarshipListResponse swapiResponse = SwapiStarshipListResponse.builder()
                .totalRecords(36)
                .totalPages(4)
                .results(List.of(starship1))
                .build();

        PagedResponse<StarshipResponse> result = StarshipMapper.toPagedResponse(swapiResponse, 1);

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().getFirst().getName()).isEqualTo("Death Star");
        assertThat(result.getTotalRecords()).isEqualTo(36L);
    }

    @Test
    void toPagedResponse_shouldNotFail_whenResultsIsNull() {
        SwapiStarshipListResponse swapiResponse = SwapiStarshipListResponse.builder()
                .results(null)
                .build();

        PagedResponse<StarshipResponse> result = StarshipMapper.toPagedResponse(swapiResponse, 1);

        assertThat(result.getItems()).isEmpty();
    }
}
