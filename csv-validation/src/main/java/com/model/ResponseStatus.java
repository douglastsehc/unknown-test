package com.model;

public enum ResponseStatus {
    SUCCESS("SUCCESS"),
    ERROR("ERROR");
    private String status;
    ResponseStatus(String s){
        status = s;
    }
    public String getStatus(){
        return this.status;
    }
}
