package com.starwars.challenge.conexa.controller;

import com.starwars.challenge.conexa.dto.request.starship.StarshipFilterRequest;
import com.starwars.challenge.conexa.dto.response.PagedResponse;
import com.starwars.challenge.conexa.dto.response.starship.StarshipResponse;
import com.starwars.challenge.conexa.exception.ErrorResponse;
import com.starwars.challenge.conexa.service.IStarshipService;
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
@RequestMapping("/api/starships")
@Tag(name = "Starships", description = "Operations on Star Wars starships")
public class StarshipController {

    private final IStarshipService starshipService;

    public StarshipController(IStarshipService starshipService) {
        this.starshipService = starshipService;
    }

    @Operation(
            summary = "List starships with pagination",
            description = "Returns a paginated list of Star Wars starships, "
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
    public ResponseEntity<PagedResponse<StarshipResponse>> getStarships(
            @Valid StarshipFilterRequest request
    ) {
        PagedResponse<StarshipResponse> response = starshipService.findStarships(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get a starship by ID",
            description = "Returns the full detail of a single starship, looked up by its SWAPI ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Starship found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "No starship found with that ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<StarshipResponse> getStarshipById(
            @Parameter(description = "Starship ID in SWAPI", example = "9")
            @PathVariable String id
    ) {
        StarshipResponse response = starshipService.findById(id);
        return ResponseEntity.ok(response);
    }
}
