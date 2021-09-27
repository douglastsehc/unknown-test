package com.service;

import com.model.Config;
import com.model.Field;
import com.model.RequestTypeStatus;
import com.model.ValidateResponse;
import com.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class CsvValidationService {

    public List<List<String>> readCsvFileByPath(String path) throws Exception {
        if (!path.endsWith(".csv")){
            throw new Exception("The file is not a csv format file");
        }
        List<List<String>> records = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(path));) {
            String[] values = null;
            while ((values = csvReader.readNext()) != null) {
                records.add(Arrays.asList(values));
            }
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("The file path is wrong, not able to find the file " + path);
        } catch (IOException e) {
            throw new IOException("Cannot serialised the file" + e.getMessage());
        }
        return records;
    }
    public ValidateResponse checkValidation(List<List<String>> fileInfo, @Valid Config config) throws Exception {
            Comparator<Field> compareById = (Field o1, Field o2) -> o1.compareTo( o2);
            Collections.sort(config.getFieldsList(), compareById);
            List<String> errorList = new ArrayList<>();
            if (this.checkIsDuplicatedIdList(config.getFieldsList()) != null) {
                errorList.add("Some field id is duplicated");
                return new ValidateResponse(errorList);
            }

            for (int row = 1; row < fileInfo.size(); row++) {
                for (int col = 0; col < fileInfo.get(row).size(); col++) {
                    if(!config.getFieldsList().get(col).getId().equals(col)){
                        throw new Exception("POST body is not well formatted");
                    }
                    String itemError = this.checkTypeAndContent(config.getFieldsList().get(col), fileInfo.get(row).get(col));
                    if (itemError != null) {
                        Integer exactRow = row - 1;
                        Integer exactCol = col;
                        String errorRowAndColumn = "Row " + exactRow.toString() + " column " + exactCol.toString() + ": ";
                        errorList.add(errorRowAndColumn + itemError);
                    }
                }
            }
            if (errorList.size() > 0) {
                return new ValidateResponse(errorList);
            }

            return new ValidateResponse();
    }

    private String checkTypeAndContent(Field field, String content) throws Exception {
        try {
            if (Objects.equals(field.getType(), RequestTypeStatus.ENUM.getStatus())) {
                if(content.isEmpty() && field.isCanBeEmpty()){
                    return null;
                } else if (content.isEmpty()){
                    return "value " + field.getType() + " that cannot be empty";
                }
                return this.checkEnum(field.getValues(), content);
            } else if (Objects.equals(field.getType(), RequestTypeStatus.NUMBER.getStatus())) {
                if(content.isEmpty() && field.isCanBeEmpty()){
                    return null;
                } else if (content.isEmpty()){
                    return "value " + field.getType() + " that cannot be empty";
                }
                return this.checkNumber(content);
            } else if (Objects.equals(field.getType(), RequestTypeStatus.STRING.getStatus())) {
                if(content.isEmpty() && field.isCanBeEmpty()){
                    return null;
                } else if (content.isEmpty()){
                    return "value " + field.getType() + " that cannot be empty";
                }
                return this.checkString(content);
            } else {
                throw new Exception("the type " + field.getType() + " does not exist");
            }
        } catch (Exception e){
            throw new Exception("config cannot be serialize " +e.getMessage());
        }
    }

    private String checkNumber(String content) throws Exception {
        try{
            Double checking = Double.parseDouble(content);
        } catch (Exception e){
            return "value " + content +" is not a number";
        }
        return null;
    }
    private String checkString(String content){
        return null;
    }
    private String checkEnum(List<String> configEnum, String content){
            if(configEnum.contains(content)){
                return null;
            }
            StringBuilder enumValue= new StringBuilder();
            for (int enumItem = 0; enumItem < configEnum.size(); enumItem++){
                if(enumItem > 0 && enumItem < configEnum.size()){
                    enumValue.append(",");
                }
                enumValue.append(configEnum.get(enumItem));
            }
        return "value " + content + " is not in the valid list of value: "+ enumValue;
    }
    public String checkIsDuplicatedIdList(List<Field> fieldList){
        HashMap<Integer, Boolean> checkIdList = new HashMap<>();
        for(Field field: fieldList){
            if (checkIdList.containsKey(field.getId())){
                return "Some field id is duplicated";
            }
            checkIdList.put(field.getId(), true);
        }
        return null;
    }
}
