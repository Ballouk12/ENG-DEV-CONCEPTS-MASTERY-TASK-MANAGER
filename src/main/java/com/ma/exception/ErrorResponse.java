package com.ma.exception;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String code,
        String message,
        String path,
        Map<String,String> fieldsError
) {
    public ErrorResponse(int status, String code, String message, String path) {
        this(Instant.now(), status, code, message, path, null);
    }

    public ErrorResponse(int status, String code, String message, String path, Map<String,String> fieldsError) {
        this(Instant.now(), status, code, message, path, fieldsError);
    }
}
