package com.starwars.challenge.conexa.service;

import com.starwars.challenge.conexa.client.SwapiClient;
import com.starwars.challenge.conexa.dto.external.person.SwapiPersonDetailResponse;
import com.starwars.challenge.conexa.dto.external.person.SwapiPersonListResponse;
import com.starwars.challenge.conexa.dto.external.person.SwapiPersonProperties;
import com.starwars.challenge.conexa.dto.external.person.SwapiPersonResult;
import com.starwars.challenge.conexa.dto.request.person.PersonFilterRequest;
import com.starwars.challenge.conexa.dto.response.PagedResponse;
import com.starwars.challenge.conexa.dto.response.person.PersonResponse;
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
public class PersonServiceImplTest {

    @Mock
    private SwapiClient swapiClient;

    @InjectMocks
    private PersonService personService;

    @Test
    void findPeople_shouldCallSwapiClientWithCorrectParamsAndMapResponse() {
        PersonFilterRequest filter = PersonFilterRequest.builder()
                .name("luke")
                .page(1)
                .size(10)
                .build();

        SwapiPersonResult person = SwapiPersonResult.builder()
                .uid("1")
                .properties(SwapiPersonProperties.builder().name("Luke Skywalker").build())
                .build();

        SwapiPersonListResponse swapiResponse = SwapiPersonListResponse.builder()
                .totalRecords(1)
                .totalPages(1)
                .results(List.of(person))
                .build();

        when(swapiClient.getPaged(
                eq("/people"), eq("name"), eq("luke"), eq(1), eq(10), eq(SwapiPersonListResponse.class)
        )).thenReturn(swapiResponse);

        PagedResponse<PersonResponse> result = personService.findPeople(filter);

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().getFirst().getName()).isEqualTo("Luke Skywalker");
        verify(swapiClient).getPaged("/people", "name", "luke", 1, 10, SwapiPersonListResponse.class);
    }

    @Test
    void findById_shouldCallSwapiClientWithCorrectPathAndMapResponse() {
        SwapiPersonResult person = SwapiPersonResult.builder()
                .uid("1")
                .properties(SwapiPersonProperties.builder().name("Luke Skywalker").build())
                .build();

        SwapiPersonDetailResponse swapiResponse = SwapiPersonDetailResponse.builder()
                .result(person)
                .build();

        when(swapiClient.get("/people/1", SwapiPersonDetailResponse.class))
                .thenReturn(swapiResponse);

        PersonResponse result = personService.findById("1");

        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getName()).isEqualTo("Luke Skywalker");
    }

    @Test
    void findById_shouldPropagateException_whenResourceNotFound() {
        when(swapiClient.get("/people/999", SwapiPersonDetailResponse.class))
                .thenThrow(new ResourceNotFoundException("Resource not found in SWAPI: /people/999"));

        assertThatThrownBy(() -> personService.findById("999"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("/people/999");
    }
}
