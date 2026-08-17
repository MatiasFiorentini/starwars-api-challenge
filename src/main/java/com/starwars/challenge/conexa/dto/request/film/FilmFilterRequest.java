package com.starwars.challenge.conexa.dto.request.film;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class FilmFilterRequest {

    private String title;

    @Builder.Default
    @Min(value = 1, message = "page must be greater than or equal to 1")
    private Integer page = 1;

    @Builder.Default
    @Min(value = 1, message = "page must be greater than or equal to 1")
    @Max(value = 100, message = "size can not be greater than 100")
    private Integer size = 10;
}

