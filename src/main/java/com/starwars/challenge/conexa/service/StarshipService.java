package com.starwars.challenge.conexa.service;

import com.starwars.challenge.conexa.client.SwapiClient;
import com.starwars.challenge.conexa.dto.external.starship.SwapiStarshipDetailResponse;
import com.starwars.challenge.conexa.dto.external.starship.SwapiStarshipListResponse;
import com.starwars.challenge.conexa.dto.request.starship.StarshipFilterRequest;
import com.starwars.challenge.conexa.dto.response.PagedResponse;
import com.starwars.challenge.conexa.dto.response.starship.StarshipResponse;
import com.starwars.challenge.conexa.mapper.StarshipMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class StarshipService implements IStarshipService {

    private final SwapiClient swapiClient;

    public StarshipService(SwapiClient swapiClient) {
        this.swapiClient = swapiClient;
    }

    @Override
    @Cacheable(value="starships", key="#filter.name + '-' + #filter.page + '-' + #filter.size")
    public PagedResponse<StarshipResponse> findStarships(StarshipFilterRequest filter) {
        SwapiStarshipListResponse swapiResponse = swapiClient.getPaged(
                "/starships",
                "name",
                filter.getName(),
                filter.getPage(),
                filter.getSize(),
                SwapiStarshipListResponse.class
        );

        return StarshipMapper.toPagedResponse(swapiResponse, filter.getPage());
    }

    @Override
    @Cacheable(value = "starships", key = "#id")
    public StarshipResponse findById(String id) {
        SwapiStarshipDetailResponse swapiResponse = swapiClient.get(
                "/starships/" + id,
                SwapiStarshipDetailResponse.class
        );

        return StarshipMapper.toResponse(swapiResponse);
    }
}
