package com.starwars.challenge.conexa.controller;

import com.starwars.challenge.conexa.dto.request.vehicle.VehicleFilterRequest;
import com.starwars.challenge.conexa.dto.response.PagedResponse;
import com.starwars.challenge.conexa.dto.response.vehicle.VehicleResponse;
import com.starwars.challenge.conexa.exception.ErrorResponse;
import com.starwars.challenge.conexa.service.IVehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vehicles")
@Tag(name = "Vehicles", description = "Operations on Star Wars vehicles")
public class VehicleController {

    private final IVehicleService vehicleService;

    public VehicleController(IVehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @Operation(
            summary = "List vehicles with pagination",
            description = "Returns a paginated list of Star Wars vehicles, "
                    + "with an optional filter by name. Requires JWT authentication."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<PagedResponse<VehicleResponse>> getVehicles(
            @Valid VehicleFilterRequest request
    ) {
        PagedResponse<VehicleResponse> response = vehicleService.findVehicles(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get a vehicle by ID",
            description = "Returns the full detail of a single vehicle, looked up by its SWAPI ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "No vehicle found with that ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getVehicleById(
            @Parameter(description = "Vehicle ID in SWAPI", example = "4")
            @PathVariable String id
    ) {
        VehicleResponse response = vehicleService.findById(id);
        return ResponseEntity.ok(response);
    }
}
