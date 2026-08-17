package com.starwars.challenge.conexa.service;

import com.starwars.challenge.conexa.dto.request.film.FilmFilterRequest;
import com.starwars.challenge.conexa.dto.response.film.FilmResponse;
import com.starwars.challenge.conexa.dto.response.PagedResponse;

public interface IFilmService {

    PagedResponse<FilmResponse> findFilms(FilmFilterRequest filter);

    FilmResponse findById(String id);
}
