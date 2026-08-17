package com.starwars.challenge.conexa.controller;

import com.starwars.challenge.conexa.dto.request.person.PersonFilterRequest;
import com.starwars.challenge.conexa.dto.response.PagedResponse;
import com.starwars.challenge.conexa.dto.response.person.PersonResponse;
import com.starwars.challenge.conexa.exception.ErrorResponse;
import com.starwars.challenge.conexa.service.IPersonService;
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
@RequestMapping("/api/people")
@Tag(name = "People", description = "Operations on Star Wars characters")
public class PersonController {

    private final IPersonService personService;

    public PersonController(IPersonService personService) {
        this.personService = personService;
    }

    @Operation(
            summary = "List people with pagination",
            description = "Returns a paginated list of Star Wars characters, "
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
    public ResponseEntity<PagedResponse<PersonResponse>> getPeople(
            @Valid PersonFilterRequest request
    ) {
        PagedResponse<PersonResponse> response = personService.findPeople(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get a person by ID",
            description = "Returns the full detail of a single character, looked up by their SWAPI ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Person found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "No person found with that ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<PersonResponse> getPersonById(
            @Parameter(description = "Person ID in SWAPI", example = "1")
            @PathVariable String id
    ) {
        PersonResponse response = personService.findById(id);
        return ResponseEntity.ok(response);
    }
}
