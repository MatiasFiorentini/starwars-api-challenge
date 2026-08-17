package com.starwars.challenge.conexa.service;

import com.starwars.challenge.conexa.dto.request.person.PersonFilterRequest;
import com.starwars.challenge.conexa.dto.response.PagedResponse;
import com.starwars.challenge.conexa.dto.response.person.PersonResponse;

public interface IPersonService {

    PagedResponse<PersonResponse> findPeople(PersonFilterRequest filter);

    PersonResponse findById(String id);
}
