package com.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
public class ValidateResponse implements Serializable {

    @JsonProperty("status")
    private String status;

    @JsonProperty("errors")
    private List<String> error;

    public ValidateResponse(){
        this.status = ResponseStatus.SUCCESS.getStatus();
        error = new ArrayList<>();
    }
    public ValidateResponse(List<String>  error){
        this.status = ResponseStatus.ERROR.getStatus();
        this.error = error;
    }
}
