package com.ghost.springcloud.common;

import com.ghost.springcloud.enums.ResponseStatus;
import lombok.Data;

/**
 * @program springcloud-demo
 * @description:
 * @author: jackchow
 * @create: 2022/01/09 15:05
 */
@Data
public class Response<T> {
    private Integer code;
    private String message;
    private T data;

    private Response(ResponseStatus responseStatus, T data) {
        this.code = responseStatus.getCode();
        this.message = responseStatus.getMessage();
        this.data = data;
    }

    public static Response<Void> success() {
        return new Response<>(ResponseStatus.SUCCESS, null);
    }

    public static <T> Response<T> success(T data) {
        return new Response<>(ResponseStatus.SUCCESS, data);
    }

    public static <T> Response<T> success(ResponseStatus responseStatus, T data) {
        if (responseStatus == null) {
            return success(data);
        }
        return new Response<T>(responseStatus, data);
    }

    public static <T> Response<T> failure() {
        return new Response<>(ResponseStatus.INTERNAL_SERVER_ERROR, null);
    }

    public static <T> Response<T> failure(ResponseStatus responseStatus) {
        return failure(responseStatus, null);
    }

    public static <T> Response<T> failure(ResponseStatus responseStatus, T data) {
        if (responseStatus == null) {
            return new Response<>(ResponseStatus.INTERNAL_SERVER_ERROR, null);
        }
        return new Response<>(responseStatus, data);
    }
}
