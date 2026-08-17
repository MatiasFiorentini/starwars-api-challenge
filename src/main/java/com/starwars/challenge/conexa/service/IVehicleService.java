package com.starwars.challenge.conexa.service;

import com.starwars.challenge.conexa.dto.request.vehicle.VehicleFilterRequest;
import com.starwars.challenge.conexa.dto.response.PagedResponse;
import com.starwars.challenge.conexa.dto.response.vehicle.VehicleResponse;

public interface IVehicleService {

    PagedResponse<VehicleResponse> findVehicles(VehicleFilterRequest filter);

    VehicleResponse findById(String id);
}
