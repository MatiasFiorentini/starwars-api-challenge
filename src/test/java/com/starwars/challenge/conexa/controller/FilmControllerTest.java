package com.starwars.challenge.conexa.controller;

import com.starwars.challenge.conexa.dto.response.PagedResponse;
import com.starwars.challenge.conexa.dto.response.film.FilmResponse;
import com.starwars.challenge.conexa.entity.User;
import com.starwars.challenge.conexa.repository.IUserRepository;
import com.starwars.challenge.conexa.security.JwtService;
import com.starwars.challenge.conexa.service.IFilmService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
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
public class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private IFilmService filmService;

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
    void getFilms_shouldReturnUnauthorized_whenNoTokenProvided() throws Exception {
        mockMvc.perform(get("/api/films"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getFilms_shouldReturnOk_whenValidTokenProvided() throws Exception {
        FilmResponse film = FilmResponse.builder()
                .id("1")
                .title("A New Hope")
                .build();

        PagedResponse<FilmResponse> pagedResponse = PagedResponse.<FilmResponse>builder()
                .items(List.of(film))
                .page(1)
                .totalPages(1)
                .totalRecords(6L)
                .build();

        when(filmService.findFilms(any())).thenReturn(pagedResponse);

        mockMvc.perform(get("/api/films")
                        .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].title").value("A New Hope"))
                .andExpect(jsonPath("$.totalRecords").value(6));
    }

    @Test
    void getFilmById_shouldReturnOk_whenValidTokenProvided() throws Exception {
        FilmResponse film = FilmResponse.builder()
                .id("1")
                .title("A New Hope")
                .director("George Lucas")
                .build();

        when(filmService.findById("1")).thenReturn(film);

        mockMvc.perform(get("/api/films/1")
                        .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("A New Hope"))
                .andExpect(jsonPath("$.director").value("George Lucas"));
    }

    @Test
    void getFilms_shouldReturnBadRequest_whenPageIsInvalid() throws Exception {
        mockMvc.perform(get("/api/films")
                        .param("page", "0")
                        .header("Authorization", validToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
