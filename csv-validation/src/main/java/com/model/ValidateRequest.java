package com.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
public class ValidateRequest {

    @JsonProperty(value = "filepath", required = true)
    @NotEmpty(message = "filepath must not be empty")
    private String filepath;

    @JsonProperty(value = "config", required = true)
    private Config config;

}
