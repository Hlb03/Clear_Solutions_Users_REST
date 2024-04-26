package com.clear.solutions.user_restful.util;

import com.clear.solutions.user_restful.dto.DataContainerDTO;
import com.clear.solutions.user_restful.exception_hadler.exception_body.ErrorDescription;
import com.clear.solutions.user_restful.exceptions.ValidationFailedException;

import java.util.List;

public class ValidateDataContainer {

    public static <T> void validateDataContainer(DataContainerDTO<T> container) {
        if (container.data() == null)
            throw new ValidationFailedException(
                    List.of(new ErrorDescription(400, "Request body should contain 'data' argument with actual user data")));
    }
}
