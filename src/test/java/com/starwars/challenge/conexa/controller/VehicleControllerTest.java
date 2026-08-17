package com.starwars.challenge.conexa.controller;

import com.starwars.challenge.conexa.dto.response.PagedResponse;
import com.starwars.challenge.conexa.dto.response.vehicle.VehicleResponse;
import com.starwars.challenge.conexa.entity.User;
import com.starwars.challenge.conexa.repository.IUserRepository;
import com.starwars.challenge.conexa.security.JwtService;
import com.starwars.challenge.conexa.service.IVehicleService;
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
public class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private IVehicleService vehicleService;

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
    void getVehicles_shouldReturnUnauthorized_whenNoTokenProvided() throws Exception {
        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getVehicles_shouldReturnOk_whenValidTokenProvided() throws Exception {
        VehicleResponse vehicle = VehicleResponse.builder()
                .id("4")
                .name("Sand Crawler")
                .build();

        PagedResponse<VehicleResponse> pagedResponse = PagedResponse.<VehicleResponse>builder()
                .items(List.of(vehicle))
                .page(1)
                .totalPages(4)
                .totalRecords(39L)
                .build();

        when(vehicleService.findVehicles(any())).thenReturn(pagedResponse);

        mockMvc.perform(get("/api/vehicles")
                        .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].name").value("Sand Crawler"))
                .andExpect(jsonPath("$.totalRecords").value(39));
    }

    @Test
    void getVehicleById_shouldReturnOk_whenValidTokenProvided() throws Exception {
        VehicleResponse vehicle = VehicleResponse.builder()
                .id("4")
                .name("Sand Crawler")
                .model("Digger Crawler")
                .build();

        when(vehicleService.findById("4")).thenReturn(vehicle);

        mockMvc.perform(get("/api/vehicles/4")
                        .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("4"))
                .andExpect(jsonPath("$.name").value("Sand Crawler"))
                .andExpect(jsonPath("$.model").value("Digger Crawler"));
    }

    @Test
    void getVehicles_shouldReturnBadRequest_whenPageIsInvalid() throws Exception {
        mockMvc.perform(get("/api/vehicles")
                        .param("page", "0")
                        .header("Authorization", validToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
