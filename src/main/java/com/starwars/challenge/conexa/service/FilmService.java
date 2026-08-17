package com.starwars.challenge.conexa.service;

import com.starwars.challenge.conexa.client.SwapiClient;
import com.starwars.challenge.conexa.dto.external.film.SwapiFilmDetailResponse;
import com.starwars.challenge.conexa.dto.external.film.SwapiFilmListResponse;
import com.starwars.challenge.conexa.dto.request.film.FilmFilterRequest;
import com.starwars.challenge.conexa.dto.response.film.FilmResponse;
import com.starwars.challenge.conexa.dto.response.PagedResponse;
import com.starwars.challenge.conexa.mapper.FilmMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class FilmService implements IFilmService {

    private final SwapiClient swapiClient;

    public FilmService(SwapiClient swapiClient) {
        this.swapiClient = swapiClient;
    }

    @Override
    @Cacheable(value="films", key="#filter.title + '-' + #filter.page + '-' + #filter.size")
    public PagedResponse<FilmResponse> findFilms(FilmFilterRequest filter) {
        SwapiFilmListResponse swapiFilmListResponse = swapiClient.getPaged(
                "/films",
                "title",
                filter.getTitle(),
                filter.getPage(),
                filter.getSize(),
                SwapiFilmListResponse.class
        );
        return FilmMapper.toPagedResponse(swapiFilmListResponse, filter.getPage());
    }

    @Override
    @Cacheable(value = "films", key = "#id")
    public FilmResponse findById(String id) {
        SwapiFilmDetailResponse swapiFilmDetailResponse = swapiClient.get(
                "/films/" + id,
                SwapiFilmDetailResponse.class
        );
        return FilmMapper.toResponse(swapiFilmDetailResponse);
    }
}
