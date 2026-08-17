package com.starwars.challenge.conexa.service;

import com.starwars.challenge.conexa.dto.request.login.LoginRequest;
import com.starwars.challenge.conexa.dto.request.register.RegisterRequest;
import com.starwars.challenge.conexa.dto.response.auth.AuthResponse;

public interface IAuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
