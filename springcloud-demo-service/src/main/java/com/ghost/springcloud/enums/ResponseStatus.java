package com.ghost.springcloud.enums;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public enum ResponseStatus {

    SUCCESS(200, "OK"),
    BAD_REQUEST( 400, "Bad Request"),
    INTERNAL_SERVER_ERROR( 500, "Internal Server Error"),;

    private Integer code;
    private String message;

    ResponseStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
