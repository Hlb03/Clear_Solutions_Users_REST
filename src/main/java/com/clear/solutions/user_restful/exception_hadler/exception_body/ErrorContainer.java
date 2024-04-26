package com.clear.solutions.user_restful.exception_hadler.exception_body;

public record ErrorContainer<T> (
    T errors
){}
