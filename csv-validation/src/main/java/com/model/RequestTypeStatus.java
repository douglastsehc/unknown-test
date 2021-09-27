package com.model;

public enum RequestTypeStatus {
    NUMBER("number"),
    STRING("string"),
    ENUM("enum");
    private String status;
    RequestTypeStatus(String s){
        status = s;
    }
    public String getStatus(){
        return this.status;
    }
}
