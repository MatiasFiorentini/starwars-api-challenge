package com.starwars.challenge.conexa.service;

import com.starwars.challenge.conexa.client.SwapiClient;
import com.starwars.challenge.conexa.dto.external.person.SwapiPersonDetailResponse;
import com.starwars.challenge.conexa.dto.external.person.SwapiPersonListResponse;
import com.starwars.challenge.conexa.dto.request.person.PersonFilterRequest;
import com.starwars.challenge.conexa.dto.response.PagedResponse;
import com.starwars.challenge.conexa.dto.response.person.PersonResponse;
import com.starwars.challenge.conexa.mapper.PersonMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class PersonService implements IPersonService {

    private final SwapiClient swapiClient;

    public PersonService(SwapiClient swapiClient) {
        this.swapiClient = swapiClient;
    }

    @Override
    @Cacheable(value="people", key="#filter.name + '-' + #filter.page + '-' + #filter.size")
    public PagedResponse<PersonResponse> findPeople(PersonFilterRequest filter) {
        SwapiPersonListResponse swapiPersonListResponse = swapiClient.getPaged(
                "/people",
                "name",
                filter.getName(),
                filter.getPage(),
                filter.getSize(),
                SwapiPersonListResponse.class
        );
        return PersonMapper.toPagedResponse(swapiPersonListResponse, filter.getPage());
    }

    @Override
    @Cacheable(value = "people", key = "#id")
    public PersonResponse findById(String id) {
        SwapiPersonDetailResponse swapiPersonDetailResponse = swapiClient.get(
                "/people/" + id,
                SwapiPersonDetailResponse.class
        );
        return PersonMapper.toResponse(swapiPersonDetailResponse);
    }
}
