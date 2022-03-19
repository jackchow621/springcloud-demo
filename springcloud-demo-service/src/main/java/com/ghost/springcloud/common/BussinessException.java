package com.ghost.springcloud.common;

import com.ghost.springcloud.enums.ResponseStatus;
import lombok.Getter;

/**
 * @program springcloud-demo
 * @description:
 * @author: jackchow
 * @create: 2022/01/09 15:19
 */
@Getter
public class BussinessException extends Exception {
    ResponseStatus responseStatus;

    public BussinessException() {
        this(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    public BussinessException(ResponseStatus responseStatus) {
        super(responseStatus.getMessage());
        this.responseStatus = responseStatus;
    }
}
