package com.apartment.apart;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND,  reason = "entity not found")
public class DataNotException extends RuntimeException {
    public DataNotException (String msg) {
        super(msg);
    }
}
