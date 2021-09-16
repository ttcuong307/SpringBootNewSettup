package com.enums;

public enum StatusCode {
    BAD_REQUEST("BAD_REQUEST"),
    ACCESS_DENY("ACCESS_DENY"),
    NOT_FOUND("NOT_FOUND"),
    SUCCESS("SUCCESS"),
    INTERNATIONAL_ERROR("INTERNATIONAL_ERROR");

    private String value;

    private StatusCode(String value) {
        this.value = value;
    }

    public String getCode(){
        return this.value;
    }
}
