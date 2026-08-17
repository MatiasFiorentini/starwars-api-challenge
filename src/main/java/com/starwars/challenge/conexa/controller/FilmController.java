package com.starwars.challenge.conexa.controller;

import com.starwars.challenge.conexa.dto.request.film.FilmFilterRequest;
import com.starwars.challenge.conexa.dto.response.PagedResponse;
import com.starwars.challenge.conexa.dto.response.film.FilmResponse;
import com.starwars.challenge.conexa.exception.ErrorResponse;
import com.starwars.challenge.conexa.service.IFilmService;
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
@RequestMapping("/api/films")
@Tag(name = "Films", description = "Operations on Star Wars films")
public class FilmController {

    private final IFilmService filmService;

    public FilmController(IFilmService filmService) {
        this.filmService = filmService;
    }

    @Operation(
            summary = "List films with pagination",
            description = "Returns a paginated list of Star Wars films, "
                    + "with an optional filter by title. Requires JWT authentication."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<PagedResponse<FilmResponse>> getFilms(
            @Valid FilmFilterRequest request
    ) {
        PagedResponse<FilmResponse> response = filmService.findFilms(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get a film by ID",
            description = "Returns the full detail of a single film, looked up by its SWAPI ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Film found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "No film found with that ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<FilmResponse> getFilmById(
            @Parameter(description = "Film ID in SWAPI", example = "1")
            @PathVariable String id
    ) {
        FilmResponse response = filmService.findById(id);
        return ResponseEntity.ok(response);
    }
}
