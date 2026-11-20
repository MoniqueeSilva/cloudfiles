package com.cloudfiles.api.exceptions;

import org.springframework.http.HttpStatus;

public class UploadException extends RuntimeException {
    private final HttpStatus status;
    public UploadException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}