package com.starwars.challenge.conexa.dto.external.vehicle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwapiVehicleResult {

    private String uid;
    private String description;
    private SwapiVehicleProperties properties;
}
