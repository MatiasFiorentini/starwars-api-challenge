package com.starwars.challenge.conexa.controller;

import com.starwars.challenge.conexa.dto.response.PagedResponse;
import com.starwars.challenge.conexa.dto.response.person.PersonResponse;
import com.starwars.challenge.conexa.entity.User;
import com.starwars.challenge.conexa.repository.IUserRepository;
import com.starwars.challenge.conexa.security.JwtService;
import com.starwars.challenge.conexa.service.IPersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private IPersonService personService;

    private String validToken;

    @BeforeEach
    void setUp() {
        if (!userRepository.existsByUsername("testuser")) {
            User user = User.builder()
                    .username("testuser")
                    .password(passwordEncoder.encode("testpassword"))
                    .build();
            userRepository.save(user);
        }

        validToken = "Bearer " + jwtService.generateToken("testuser");
    }

    @Test
    void getPeople_shouldReturnUnauthorized_whenNoTokenProvided() throws Exception {
        mockMvc.perform(get("/api/people"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getPeople_shouldReturnOk_whenValidTokenProvided() throws Exception {
        PersonResponse person = PersonResponse.builder()
                .id("1")
                .name("Luke Skywalker")
                .build();

        PagedResponse<PersonResponse> pagedResponse = PagedResponse.<PersonResponse>builder()
                .items(List.of(person))
                .page(1)
                .totalPages(9)
                .totalRecords(82L)
                .build();

        when(personService.findPeople(any())).thenReturn(pagedResponse);

        mockMvc.perform(get("/api/people")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].name").value("Luke Skywalker"))
                .andExpect(jsonPath("$.totalRecords").value(82));
    }

    @Test
    void getPersonById_shouldReturnOk_whenValidTokenProvided() throws Exception {
        PersonResponse person = PersonResponse.builder()
                .id("1")
                .name("Luke Skywalker")
                .height(172)
                .build();

        when(personService.findById("1")).thenReturn(person);

        mockMvc.perform(get("/api/people/1")
                        .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Luke Skywalker"))
                .andExpect(jsonPath("$.height").value(172));
    }

    @Test
    void getPeople_shouldReturnBadRequest_whenPageIsInvalid() throws Exception {
        mockMvc.perform(get("/api/people")
                        .param("page", "0")
                        .header("Authorization", validToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
