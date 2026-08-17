package com.starwars.challenge.conexa.service;

import com.starwars.challenge.conexa.client.SwapiClient;
import com.starwars.challenge.conexa.dto.external.vehicle.SwapiVehicleDetailResponse;
import com.starwars.challenge.conexa.dto.external.vehicle.SwapiVehicleListResponse;
import com.starwars.challenge.conexa.dto.request.vehicle.VehicleFilterRequest;
import com.starwars.challenge.conexa.dto.response.PagedResponse;
import com.starwars.challenge.conexa.dto.response.vehicle.VehicleResponse;
import com.starwars.challenge.conexa.mapper.VehicleMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class VehicleService implements IVehicleService {

    private final SwapiClient swapiClient;

    public VehicleService(SwapiClient swapiClient) {
        this.swapiClient = swapiClient;
    }

    @Override
    @Cacheable(value="vehicles", key="#filter.name + '-' + #filter.page + '-' + #filter.size")
    public PagedResponse<VehicleResponse> findVehicles(VehicleFilterRequest filter) {
        SwapiVehicleListResponse swapiVehicleListResponse = swapiClient.getPaged(
                "/vehicles",
                "name",
                filter.getName(),
                filter.getPage(),
                filter.getSize(),
                SwapiVehicleListResponse.class
        );
        return VehicleMapper.toPagedResponse(swapiVehicleListResponse, filter.getPage());
    }

    @Override
    @Cacheable(value = "vehicles", key = "#id")
    public VehicleResponse findById(String id) {
        SwapiVehicleDetailResponse swapiResponse = swapiClient.get(
                "/vehicles/" + id,
                SwapiVehicleDetailResponse.class
        );
        return VehicleMapper.toResponse(swapiResponse);
    }
}
