package com.starwars.challenge.conexa.service;

import com.starwars.challenge.conexa.dto.request.starship.StarshipFilterRequest;
import com.starwars.challenge.conexa.dto.response.PagedResponse;
import com.starwars.challenge.conexa.dto.response.starship.StarshipResponse;

public interface IStarshipService {

    PagedResponse<StarshipResponse> findStarships(StarshipFilterRequest filter);

    StarshipResponse findById(String id);
}
