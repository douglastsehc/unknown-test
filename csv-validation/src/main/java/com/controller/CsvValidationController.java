package com.controller;

import com.model.ValidateRequest;
import com.model.ValidateResponse;
import com.service.CsvValidationService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping(path = "csv-validation")
public class CsvValidationController {

    private CsvValidationService csvValidationService;

    @PostMapping
    public  ValidateResponse csvValidation(@Valid @RequestBody ValidateRequest request){
        try {
            String filePath = request.getFilepath();
            List<List<String>> fileInfo = csvValidationService.readCsvFileByPath(filePath);
            return csvValidationService.checkValidation(fileInfo, request.getConfig());
        }
        catch (NullPointerException e){
            List<String> error = new ArrayList<>();
            error.add("config cannot be serialised");
            return new ValidateResponse(error);
        }
        catch(Exception e){
            List<String> error = new ArrayList<>();
            error.add(e.getMessage());
            return new ValidateResponse(error);
        }

    }


}
