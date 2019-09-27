package com.chriniko.lunatech.movies.error;

public class ProcessingException extends RuntimeException {

    public ProcessingException(String message, Throwable error) {
        super(message, error);
    }

    public ProcessingException(Throwable error) {
        super(error);
    }
}
