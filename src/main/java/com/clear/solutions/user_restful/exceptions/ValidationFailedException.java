package com.clear.solutions.user_restful.exceptions;

import com.clear.solutions.user_restful.exception_hadler.exception_body.ErrorDescription;

import java.util.List;

public class ValidationFailedException extends RuntimeException {

    private final List<ErrorDescription> errorResponses;

    public ValidationFailedException(List<ErrorDescription> errorResponses) {
        this.errorResponses = errorResponses;
    }

    public List<ErrorDescription> getErrorResponses() {
        return errorResponses;
    }
}
