package com.starwars.challenge.conexa.controller;

import com.starwars.challenge.conexa.dto.response.PagedResponse;
import com.starwars.challenge.conexa.dto.response.starship.StarshipResponse;
import com.starwars.challenge.conexa.entity.User;
import com.starwars.challenge.conexa.repository.IUserRepository;
import com.starwars.challenge.conexa.security.JwtService;
import com.starwars.challenge.conexa.service.IStarshipService;
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
public class StarshipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private IStarshipService starshipService;

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
    void getStarships_shouldReturnUnauthorized_whenNoTokenProvided() throws Exception {
        mockMvc.perform(get("/api/starships"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getStarships_shouldReturnOk_whenValidTokenProvided() throws Exception {
        StarshipResponse starship = StarshipResponse.builder()
                .id("10")
                .name("Millennium Falcon")
                .build();

        PagedResponse<StarshipResponse> pagedResponse = PagedResponse.<StarshipResponse>builder()
                .items(List.of(starship))
                .page(1)
                .totalPages(4)
                .totalRecords(36L)
                .build();

        when(starshipService.findStarships(any())).thenReturn(pagedResponse);

        mockMvc.perform(get("/api/starships")
                        .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].name").value("Millennium Falcon"))
                .andExpect(jsonPath("$.totalRecords").value(36));
    }

    @Test
    void getStarshipById_shouldReturnOk_whenValidTokenProvided() throws Exception {
        StarshipResponse starship = StarshipResponse.builder()
                .id("10")
                .name("Millennium Falcon")
                .model("YT-1300 light freighter")
                .build();

        when(starshipService.findById("10")).thenReturn(starship);

        mockMvc.perform(get("/api/starships/10")
                        .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("10"))
                .andExpect(jsonPath("$.name").value("Millennium Falcon"))
                .andExpect(jsonPath("$.model").value("YT-1300 light freighter"));
    }

    @Test
    void getStarships_shouldReturnBadRequest_whenPageIsInvalid() throws Exception {
        mockMvc.perform(get("/api/starships")
                        .param("page", "0")
                        .header("Authorization", validToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
