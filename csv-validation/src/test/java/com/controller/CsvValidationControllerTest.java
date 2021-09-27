package com.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.model.*;
import com.service.CsvValidationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(CsvValidationController.class)
public class CsvValidationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CsvValidationService csvValidationServiceMock;

    @Test
    @DisplayName("should pass validation")
    void should_pass_validation() throws Exception{

        Config config = new Config(this.initializeFieldList());
        ValidateRequest request =ValidateRequest.builder()
                .filepath("justRandomInput")
                .config(config)
                .build();
        Mockito.when(csvValidationServiceMock.readCsvFileByPath(request.getFilepath()))
                .thenReturn(this.initializeCSFileInfo());
        Mockito.when(csvValidationServiceMock.checkValidation(this.initializeCSFileInfo(), request.getConfig()))
                .thenCallRealMethod();
        ValidateResponse validateResponse = new ValidateResponse();
        MvcResult resultResponse = mockMvc.perform(MockMvcRequestBuilders
                        .post("/csv-validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                 .andExpect(jsonPath("$.status").value(validateResponse.getStatus()))
                .andExpect(jsonPath("$.errors").value(validateResponse.getError()))
                .andReturn();
    }

    @Test
    @DisplayName("should pass validation with random field order")
    void should_pass_validation_with_random_field_order() throws Exception{

        Config config = new Config(this.initializeFieldListRandomOrder());
        ValidateRequest request =ValidateRequest.builder()
                .filepath("justRandomInput")
                .config(config)
                .build();
        Mockito.when(csvValidationServiceMock.readCsvFileByPath(request.getFilepath()))
                .thenReturn(this.initializeCSFileInfo());
        Mockito.when(csvValidationServiceMock.checkValidation(this.initializeCSFileInfo(), request.getConfig()))
                .thenCallRealMethod();
        ValidateResponse validateResponse = new ValidateResponse();
        MvcResult resultResponse = mockMvc.perform(MockMvcRequestBuilders
                        .post("/csv-validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(jsonPath("$.status").value(validateResponse.getStatus()))
                .andExpect(jsonPath("$.errors").value(validateResponse.getError()))
                .andReturn();
    }

    @Test
    @DisplayName("should pass validation with price canBeEmpty and empty value price")
    void should_pass_validation_with_price_can_be_empty_and_empty_price() throws Exception{

        Config config = new Config(this.initializeFieldListEmptyPrice());
        ValidateRequest request =ValidateRequest.builder()
                .filepath("justRandomInput")
                .config(config)
                .build();
        Mockito.when(csvValidationServiceMock.readCsvFileByPath(request.getFilepath()))
                .thenReturn(this.initializeCSFileInfoEmptyValue());
        Mockito.when(csvValidationServiceMock.checkValidation(this.initializeCSFileInfoEmptyValue(), request.getConfig()))
                .thenCallRealMethod();
        ValidateResponse validateResponse = new ValidateResponse();
        MvcResult resultResponse = mockMvc.perform(MockMvcRequestBuilders
                        .post("/csv-validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(jsonPath("$.status").value(validateResponse.getStatus()))
                .andExpect(jsonPath("$.errors").value(validateResponse.getError()))
                .andReturn();
    }

    @Test
    @DisplayName("should fail validation with empty value price")
    void should_fail_validation_with_empty_price() throws Exception{

        Config config = new Config(this.initializeFieldList());
        ValidateRequest request =ValidateRequest.builder()
                .filepath("justRandomInput")
                .config(config)
                .build();
        Mockito.when(csvValidationServiceMock.readCsvFileByPath(request.getFilepath()))
                .thenReturn(this.initializeCSFileInfoEmptyValue());
        Mockito.when(csvValidationServiceMock.checkValidation(this.initializeCSFileInfoEmptyValue(), request.getConfig()))
                .thenCallRealMethod();
        List<String> errorRes = new ArrayList<>();
        errorRes.add("Row 0 column 0: value number that cannot be empty");
        ValidateResponse validateResponse = new ValidateResponse(errorRes);
        MvcResult resultResponse = mockMvc.perform(MockMvcRequestBuilders
                        .post("/csv-validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(jsonPath("$.status").value(validateResponse.getStatus()))
                .andExpect(jsonPath("$.errors").value(validateResponse.getError()))
                .andReturn();
    }

    @Test
    @DisplayName("empty filepath will fail validation")
    void should_fail_csv_validation_because_filepath_is_empty() throws Exception{

        Config config = new Config(this.initializeFieldList());
        ValidateRequest request =ValidateRequest.builder()
                .filepath("")
                .config(config)
                .build();
        List<String> errorRes = new ArrayList<>();
        errorRes.add("filepath must not be empty");
        ValidateResponse validateResponse = new ValidateResponse(errorRes);
        MvcResult resultResponse = mockMvc.perform(MockMvcRequestBuilders
                        .post("/csv-validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(jsonPath("$.status").value(validateResponse.getStatus()))
                .andExpect(jsonPath("$.errors").value(validateResponse.getError()))
                .andReturn();
        String responseBody = resultResponse.getResponse().getContentAsString();

    }

    @Test
    @DisplayName("non csv file will fail validation")
    void should_fail_csv_validation_because_not_csv_file() throws Exception{

        Config config = new Config(this.initializeFieldList());
        ValidateRequest request =ValidateRequest.builder()
                .filepath("C:\\book.cssv")
                .config(config)
                .build();
        Mockito.when(csvValidationServiceMock.readCsvFileByPath(request.getFilepath()))
                .thenCallRealMethod();
        List<String> errorRes = new ArrayList<>();
        errorRes.add("The file is not a csv format file");
        ValidateResponse validateResponse = new ValidateResponse(errorRes);
        MvcResult resultResponse = mockMvc.perform(MockMvcRequestBuilders
                        .post("/csv-validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(jsonPath("$.status").value(validateResponse.getStatus()))
                .andExpect(jsonPath("$.errors").value(validateResponse.getError()))
                .andReturn();

    }

    @Test
    @DisplayName("wrong file path will fail validation")
    void should_fail_csv_validation_because_wrong_filepath() throws Exception{

        Config config = new Config(this.initializeFieldList());
        ValidateRequest request =ValidateRequest.builder()
                .filepath("C:\\book.csv")
                .config(config)
                .build();
        Mockito.when(csvValidationServiceMock.readCsvFileByPath(request.getFilepath()))
                .thenCallRealMethod();
        List<String> errorRes = new ArrayList<>();
        errorRes.add("The file path is wrong, not able to find the file " + request.getFilepath());
        ValidateResponse validateResponse = new ValidateResponse(errorRes);
        MvcResult resultResponse = mockMvc.perform(MockMvcRequestBuilders
                        .post("/csv-validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(jsonPath("$.status").value(validateResponse.getStatus()))
                .andExpect(jsonPath("$.errors").value(validateResponse.getError()))
                .andReturn();

    }

    @Test
    @DisplayName("should fail validation with fewer fields, post body is not well formatted")
    void should_fail_validation_with_fewer_field() throws Exception{

        Config config = new Config(this.initializeFieldListFewerFieldList());
        ValidateRequest request =ValidateRequest.builder()
                .filepath("justRandomInput")
                .config(config)
                .build();
        Mockito.when(csvValidationServiceMock.readCsvFileByPath(request.getFilepath()))
                .thenReturn(this.initializeCSFileInfo());
        Mockito.when(csvValidationServiceMock.checkValidation(this.initializeCSFileInfo(), config))
                .thenCallRealMethod();
        List<String> errorRes = new ArrayList<>();
        errorRes.add("POST body is not well formatted");
        ValidateResponse validateResponse = new ValidateResponse(errorRes);
        MvcResult resultResponse = mockMvc.perform(MockMvcRequestBuilders
                        .post("/csv-validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(jsonPath("$.status").value(validateResponse.getStatus()))
                .andExpect(jsonPath("$.errors").value(validateResponse.getError()))
                .andReturn();
    }

    @Test
    @DisplayName("should fail validation with wrong field type")
    void should_fail_validation_with_wrong_field_type() throws Exception{

        Config config = new Config(this.initializeFieldListWrongType());
        ValidateRequest request =ValidateRequest.builder()
                .filepath("justRandomInput")
                .config(config)
                .build();
        Mockito.when(csvValidationServiceMock.readCsvFileByPath(request.getFilepath()))
                .thenReturn(this.initializeCSFileInfo());
        Mockito.when(csvValidationServiceMock.checkValidation(this.initializeCSFileInfo(), config))
                .thenCallRealMethod();
        List<String> errorRes = new ArrayList<>();
        errorRes.add("config cannot be serialize the type AAA does not exist");
        ValidateResponse validateResponse = new ValidateResponse(errorRes);
        MvcResult resultResponse = mockMvc.perform(MockMvcRequestBuilders
                        .post("/csv-validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(jsonPath("$.status").value(validateResponse.getStatus()))
                .andExpect(jsonPath("$.errors").value(validateResponse.getError()))
                .andReturn();
    }

//    @Test
//    @DisplayName("should fail validation with duplicated field type")
//    void should_fail_validation_with_duplicated_field_type() throws Exception{
//
//        Config config = new Config(this.initializeFieldListDuplicatedId());
//        ValidateRequest request =ValidateRequest.builder()
//                .filepath("justRandomInput")
//                .config(config)
//                .build();
//        Mockito.when(csvValidationServiceMock.readCsvFileByPath(request.getFilepath()))
//                .thenReturn(this.initializeCSFileInfo());
//        List<String> errorRes = new ArrayList<>();
//        errorRes.add("POST body is not well formatted");
//        ValidateResponse validateResponse = new ValidateResponse(errorRes);
//        MvcResult resultResponse = mockMvc.perform(MockMvcRequestBuilders
//                        .post("/csv-validation")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(request)))
//                .andExpect(jsonPath("$.status").value(validateResponse.getStatus()))
//                .andExpect(jsonPath("$.errors").value(validateResponse.getError()))
//                .andReturn();
//    }

    @Test
    @DisplayName("should fail validation with wrong csv type")
    void should_fail_validation_with_wrong_csv_type() throws Exception{

        Config config = new Config(this.initializeFieldListRandomOrder());
        ValidateRequest request =ValidateRequest.builder()
                .filepath("justRandomInput")
                .config(config)
                .build();
        Mockito.when(csvValidationServiceMock.readCsvFileByPath(request.getFilepath()))
                .thenReturn(this.initializeCSFileInfoWrongNumber());
        Mockito.when(csvValidationServiceMock.checkValidation(this.initializeCSFileInfoWrongNumber(), request.getConfig()))
                .thenCallRealMethod();
        List<String> errorRes = new ArrayList<>();
        errorRes.add("Row 0 column 0: value WrongNumber is not a number");
        ValidateResponse validateResponse = new ValidateResponse(errorRes);
        MvcResult resultResponse = mockMvc.perform(MockMvcRequestBuilders
                        .post("/csv-validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(jsonPath("$.status").value(validateResponse.getStatus()))
                .andExpect(jsonPath("$.errors").value(validateResponse.getError()))
                .andReturn();
    }

    @Test
    @DisplayName("should fail validation with not exist enum element")
    void should_fail_validation_with_not_exist_enum_element() throws Exception{

        Config config = new Config(this.initializeFieldListRandomOrder());
        ValidateRequest request =ValidateRequest.builder()
                .filepath("justRandomInput")
                .config(config)
                .build();
        Mockito.when(csvValidationServiceMock.readCsvFileByPath(request.getFilepath()))
                .thenReturn(this.initializeCSFileInfoWrongEnum());
        Mockito.when(csvValidationServiceMock.checkValidation(this.initializeCSFileInfoWrongEnum(), request.getConfig()))
                .thenCallRealMethod();
        List<String> errorRes = new ArrayList<>();
        errorRes.add("Row 0 column 0: value WrongNumber is not a number");
        errorRes.add("Row 3 column 3: value FIL is not in the valid list of value: JPY,USD,CNY,HKD");
        ValidateResponse validateResponse = new ValidateResponse(errorRes);
        MvcResult resultResponse = mockMvc.perform(MockMvcRequestBuilders
                        .post("/csv-validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(jsonPath("$.status").value(validateResponse.getStatus()))
                .andExpect(jsonPath("$.errors").value(validateResponse.getError()))
                .andReturn();
    }


    public  String asJsonString(final Object obj) throws Exception {
        try{
            final ObjectMapper mapper = new ObjectMapper();
            final String jsonContent = mapper.writeValueAsString(obj);
            return jsonContent;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    private List<Field> initializeFieldList(){
        List<Field> fieldListExample = new ArrayList<Field>();
        List<String> enumStringExample = new ArrayList<String>();
        String [] arr = {"JPY","USD","CNY","HKD"};
        List arrayAdd = Arrays.asList(arr);
        enumStringExample.addAll(arrayAdd);
        Field fieldExample = new Field(0, "price", false, "number", null);
        Field fieldExample2 = new Field(1, "quantity", false, "number", null);
        Field fieldExample3 = new Field(2, "name", false, "string", null);
        Field fieldExample4 = new Field(3, "currency", false, "enum", enumStringExample);
        fieldListExample.add(fieldExample);
        fieldListExample.add(fieldExample2);
        fieldListExample.add(fieldExample3);
        fieldListExample.add(fieldExample4);
        return fieldListExample;
    }

    private List<Field> initializeFieldListEmptyPrice(){
        List<Field> fieldListExample = new ArrayList<Field>();
        List<String> enumStringExample = new ArrayList<String>();
        String [] arr = {"JPY","USD","CNY","HKD"};
        List arrayAdd = Arrays.asList(arr);
        enumStringExample.addAll(arrayAdd);
        Field fieldExample = new Field(0, "price", true, "number", null);
        Field fieldExample2 = new Field(1, "quantity", false, "number", null);
        Field fieldExample3 = new Field(2, "name", false, "string", null);
        Field fieldExample4 = new Field(3, "currency", false, "enum", enumStringExample);
        fieldListExample.add(fieldExample);
        fieldListExample.add(fieldExample2);
        fieldListExample.add(fieldExample3);
        fieldListExample.add(fieldExample4);
        return fieldListExample;
    }

    private List<Field> initializeFieldListRandomOrder(){
        List<Field> fieldListExample = new ArrayList<Field>();
        List<String> enumStringExample = new ArrayList<String>();
        String [] arr = {"JPY","USD","CNY","HKD"};
        List arrayAdd = Arrays.asList(arr);
        enumStringExample.addAll(arrayAdd);
        Field fieldExample = new Field(1, "quantity", false, "number", null);
        Field fieldExample2 = new Field(0, "price", false, "number", null);
        Field fieldExample3 = new Field(3, "currency", false, "enum", enumStringExample);
        Field fieldExample4 = new Field(2, "name", false, "string", null);
        fieldListExample.add(fieldExample);
        fieldListExample.add(fieldExample2);
        fieldListExample.add(fieldExample3);
        fieldListExample.add(fieldExample4);
        return fieldListExample;
    }

    private List<Field> initializeFieldListFewerFieldList(){
        List<Field> fieldListExample = new ArrayList<Field>();
        List<String> enumStringExample = new ArrayList<String>();
        String [] arr = {"JPY","USD","CNY","HKD"};
        List arrayAdd = Arrays.asList(arr);
        enumStringExample.addAll(arrayAdd);
        Field fieldExample = new Field(1, "quantity", false, "number", null);
        fieldListExample.add(fieldExample);
        return fieldListExample;
    }

    private List<Field> initializeFieldListWrongType() {
        List<Field> fieldListExample = new ArrayList<Field>();
        List<String> enumStringExample = new ArrayList<String>();
        String[] arr = {"JPY", "USD", "CNY", "HKD"};
        List arrayAdd = Arrays.asList(arr);
        enumStringExample.addAll(arrayAdd);
        Field fieldExample = new Field(1, "quantity", false, "AAA", null);
        Field fieldExample2 = new Field(0, "price", false, "number", null);
        Field fieldExample3 = new Field(3, "currency", false, "enum", enumStringExample);
        Field fieldExample4 = new Field(2, "name", false, "string", null);
        fieldListExample.add(fieldExample);
        fieldListExample.add(fieldExample2);
        fieldListExample.add(fieldExample3);
        fieldListExample.add(fieldExample4);
        return fieldListExample;
    }

//    private List<Field> initializeFieldListDuplicatedId() {
//        List<Field> fieldListExample = new ArrayList<Field>();
//        List<String> enumStringExample = new ArrayList<String>();
//        String[] arr = {"JPY", "USD", "CNY", "HKD"};
//        List arrayAdd = Arrays.asList(arr);
//        enumStringExample.addAll(arrayAdd);
//        Field fieldExample = new Field(1, "quantity", false, "number", null);
//        Field fieldExample2 = new Field(0, "price", false, "number", null);
//        Field fieldExample3 = new Field(2, "currency", false, "enum", enumStringExample);
//        Field fieldExample4 = new Field(2, "name", false, "string", null);
//        fieldListExample.add(fieldExample);
//        fieldListExample.add(fieldExample2);
//        fieldListExample.add(fieldExample3);
//        fieldListExample.add(fieldExample4);
//        return fieldListExample;
//    }

    private List<List<String>> initializeCSFileInfo(){
        List<List<String>> fileInfo = new ArrayList<>();
        List<String> csvRes1 = new ArrayList<>();
        csvRes1.add("price");
        csvRes1.add("quantity");
        csvRes1.add("name");
        csvRes1.add("currency");
        fileInfo.add(csvRes1);
        List<String> csvRes2 = new ArrayList<>();
        csvRes2.add("0.5");
        csvRes2.add("20000");
        csvRes2.add("DOGE");
        csvRes2.add("USD");
        fileInfo.add(csvRes2);
        List<String> csvRes3 = new ArrayList<>();
        csvRes3.add("1.15");
        csvRes3.add("50000");
        csvRes3.add("ADA");
        csvRes3.add("USD");
        fileInfo.add(csvRes3);
        List<String> csvRes4 = new ArrayList<>();
        csvRes4.add("2248");
        csvRes4.add("1000");
        csvRes4.add("ETH");
        csvRes4.add("USD");
        fileInfo.add(csvRes4);
        List<String> csvRes5 = new ArrayList<>();
        csvRes5.add("35000");
        csvRes5.add("0.12");
        csvRes5.add("BTC");
        csvRes5.add("USD");
        fileInfo.add(csvRes5);
        return fileInfo;
    }

    private List<List<String>> initializeCSFileInfoEmptyValue(){
        List<List<String>> fileInfo = new ArrayList<>();
        List<String> csvRes1 = new ArrayList<>();
        csvRes1.add("price");
        csvRes1.add("quantity");
        csvRes1.add("name");
        csvRes1.add("currency");
        fileInfo.add(csvRes1);
        List<String> csvRes2 = new ArrayList<>();
        csvRes2.add("");
        csvRes2.add("20000");
        csvRes2.add("DOGE");
        csvRes2.add("USD");
        fileInfo.add(csvRes2);
        List<String> csvRes3 = new ArrayList<>();
        csvRes3.add("1.15");
        csvRes3.add("50000");
        csvRes3.add("ADA");
        csvRes3.add("USD");
        fileInfo.add(csvRes3);
        List<String> csvRes4 = new ArrayList<>();
        csvRes4.add("2248");
        csvRes4.add("1000");
        csvRes4.add("ETH");
        csvRes4.add("USD");
        fileInfo.add(csvRes4);
        List<String> csvRes5 = new ArrayList<>();
        csvRes5.add("35000");
        csvRes5.add("0.12");
        csvRes5.add("BTC");
        csvRes5.add("USD");
        fileInfo.add(csvRes5);
        return fileInfo;
    }

    private List<List<String>> initializeCSFileInfoWrongNumber(){
        List<List<String>> fileInfo = new ArrayList<>();
        List<String> csvRes1 = new ArrayList<>();
        csvRes1.add("price");
        csvRes1.add("quantity");
        csvRes1.add("name");
        csvRes1.add("currency");
        fileInfo.add(csvRes1);
        List<String> csvRes2 = new ArrayList<>();
        csvRes2.add("WrongNumber");
        csvRes2.add("20000");
        csvRes2.add("DOGE");
        csvRes2.add("USD");
        fileInfo.add(csvRes2);
        List<String> csvRes3 = new ArrayList<>();
        csvRes3.add("1.15");
        csvRes3.add("50000");
        csvRes3.add("ADA");
        csvRes3.add("USD");
        fileInfo.add(csvRes3);
        List<String> csvRes4 = new ArrayList<>();
        csvRes4.add("2248");
        csvRes4.add("1000");
        csvRes4.add("ETH");
        csvRes4.add("USD");
        fileInfo.add(csvRes4);
        List<String> csvRes5 = new ArrayList<>();
        csvRes5.add("35000");
        csvRes5.add("0.12");
        csvRes5.add("BTC");
        csvRes5.add("USD");
        fileInfo.add(csvRes5);
        return fileInfo;
    }

    private List<List<String>> initializeCSFileInfoWrongEnum(){
        List<List<String>> fileInfo = new ArrayList<>();
        List<String> csvRes1 = new ArrayList<>();
        csvRes1.add("price");
        csvRes1.add("quantity");
        csvRes1.add("name");
        csvRes1.add("currency");
        fileInfo.add(csvRes1);
        List<String> csvRes2 = new ArrayList<>();
        csvRes2.add("WrongNumber");
        csvRes2.add("20000");
        csvRes2.add("DOGE");
        csvRes2.add("USD");
        fileInfo.add(csvRes2);
        List<String> csvRes3 = new ArrayList<>();
        csvRes3.add("1.15");
        csvRes3.add("50000");
        csvRes3.add("ADA");
        csvRes3.add("USD");
        fileInfo.add(csvRes3);
        List<String> csvRes4 = new ArrayList<>();
        csvRes4.add("2248");
        csvRes4.add("1000");
        csvRes4.add("ETH");
        csvRes4.add("USD");
        fileInfo.add(csvRes4);
        List<String> csvRes5 = new ArrayList<>();
        csvRes5.add("35000");
        csvRes5.add("0.12");
        csvRes5.add("BTC");
        csvRes5.add("FIL");
        fileInfo.add(csvRes5);
        return fileInfo;
    }

}
