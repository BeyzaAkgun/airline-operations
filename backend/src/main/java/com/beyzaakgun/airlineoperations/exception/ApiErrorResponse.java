package com.beyzaakgun.airlineoperations.exception;

import java.time.OffsetDateTime;
import java.util.Map;

public class ApiErrorResponse {
    private OffsetDateTime timestamp;
    private int status;
    private String message;
    private String path;
    private Map<String,String> fieldErrors;

    public ApiErrorResponse(OffsetDateTime timestamp, int status,
                            String message, String path, Map<String, String> fieldErrors) {
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
        this.path = path;
        this.fieldErrors = fieldErrors;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }


    public int getStatus() {
        return status;
    }


    public String getMessage() {
        return message;
    }


    public String getPath() {
        return path;
    }


    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

}
