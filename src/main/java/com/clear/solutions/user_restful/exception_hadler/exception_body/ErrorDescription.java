package com.clear.solutions.user_restful.exception_hadler.exception_body;

public record ErrorDescription(
    int statusCode,
    String detail
) {}
