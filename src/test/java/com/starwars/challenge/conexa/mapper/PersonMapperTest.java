package com.starwars.challenge.conexa.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.starwars.challenge.conexa.dto.external.person.SwapiPersonListResponse;
import com.starwars.challenge.conexa.dto.external.person.SwapiPersonProperties;
import com.starwars.challenge.conexa.dto.external.person.SwapiPersonResult;
import com.starwars.challenge.conexa.dto.response.PagedResponse;
import com.starwars.challenge.conexa.dto.response.person.PersonResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

public class PersonMapperTest {

    @Test
    void toResponse_shouldMapAllFieldsCorrectly() {
        SwapiPersonProperties properties = SwapiPersonProperties.builder()
                .name("Luke Skywalker")
                .height("172")
                .mass("77")
                .hairColor("blond")
                .eyeColor("blue")
                .birthYear("19BBY")
                .gender("male")
                .build();

        SwapiPersonResult result = SwapiPersonResult.builder()
                .uid("1")
                .description("A person within the Star Wars universe")
                .properties(properties)
                .build();

        PersonResponse response = PersonMapper.toResponse(result);

        assertThat(response.getId()).isEqualTo("1");
        assertThat(response.getName()).isEqualTo("Luke Skywalker");
        assertThat(response.getHeight()).isEqualTo(172);
        assertThat(response.getMass()).isEqualTo(77.0);
        assertThat(response.getHairColor()).isEqualTo("blond");
        assertThat(response.getEyeColor()).isEqualTo("blue");
        assertThat(response.getBirthYear()).isEqualTo("19BBY");
        assertThat(response.getGender()).isEqualTo("male");
    }

    @Test
    void toPagedResponse_shouldMapFullListWithPagination() {
        SwapiPersonResult person1 = SwapiPersonResult.builder()
                .uid("1")
                .properties(SwapiPersonProperties.builder().name("Luke Skywalker").build())
                .build();

        SwapiPersonResult person2 = SwapiPersonResult.builder()
                .uid("2")
                .properties(SwapiPersonProperties.builder().name("C-3PO").build())
                .build();

        SwapiPersonListResponse swapiResponse = SwapiPersonListResponse.builder()
                .totalRecords(82)
                .totalPages(9)
                .results(List.of(person1, person2))
                .build();

        PagedResponse<PersonResponse> result = PersonMapper.toPagedResponse(swapiResponse, 1);

        assertThat(result.getItems()).hasSize(2);
        assertThat(result.getItems().get(0).getName()).isEqualTo("Luke Skywalker");
        assertThat(result.getItems().get(1).getName()).isEqualTo("C-3PO");
        assertThat(result.getPage()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(9);
        assertThat(result.getTotalRecords()).isEqualTo(82L);
    }

    @Test
    void toPagedResponse_shouldNotFail_whenResultsIsNull() {
        SwapiPersonListResponse swapiResponse = SwapiPersonListResponse.builder()
                .totalRecords(null)
                .totalPages(null)
                .results(null)
                .build();

        PagedResponse<PersonResponse> result = PersonMapper.toPagedResponse(swapiResponse, 1);

        assertThat(result.getItems()).isEmpty();
        assertThat(result.getTotalRecords()).isNull();
    }
}
