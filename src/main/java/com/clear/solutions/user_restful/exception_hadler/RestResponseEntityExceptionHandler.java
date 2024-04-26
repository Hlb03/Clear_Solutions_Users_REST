package com.clear.solutions.user_restful.exception_hadler;

import com.clear.solutions.user_restful.exception_hadler.exception_body.ErrorContainer;
import com.clear.solutions.user_restful.exception_hadler.exception_body.ErrorDescription;
import com.clear.solutions.user_restful.exceptions.IncorrectRequestException;
import com.clear.solutions.user_restful.exceptions.NotSupportedAgeException;
import com.clear.solutions.user_restful.exceptions.UserNotFoundException;
import com.clear.solutions.user_restful.exceptions.ValidationFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class RestResponseEntityExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    protected ErrorContainer<ErrorDescription> handleUsersNotFound(UserNotFoundException e) {
        return new ErrorContainer<>(new ErrorDescription(404, e.getMessage()));
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(IncorrectRequestException.class)
    protected ErrorContainer<ErrorDescription> handlePatchRequestInsteadOfPut(IncorrectRequestException e) {
        return new ErrorContainer<>(new ErrorDescription(405, e.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NotSupportedAgeException.class)
    protected ErrorContainer<ErrorDescription> handleUnsupportedUserAge(NotSupportedAgeException e) {
        return new ErrorContainer<>(new ErrorDescription(400, e.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationFailedException.class)
    protected ErrorContainer<List<ErrorDescription>> handleValidationProblems(ValidationFailedException e) {
        return new ErrorContainer<>(e.getErrorResponses());
    }
}
