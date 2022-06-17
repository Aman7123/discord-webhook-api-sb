package com.aaronrenner.spring.exceptions;

@SuppressWarnings("serial")
public class DuplicateException extends RuntimeException {

    public DuplicateException(String message) {
        super(message);
    }
}
