package com.clear.solutions.user_restful.util;

import com.clear.solutions.user_restful.dto.UserInfoDTO;
import com.clear.solutions.user_restful.exception_hadler.exception_body.ErrorDescription;
import com.clear.solutions.user_restful.exceptions.IncorrectRequestException;
import com.clear.solutions.user_restful.exceptions.ValidationFailedException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UsersValidationUtil {

    public static void validateUserData(UserInfoDTO user) {
        List<ErrorDescription> errorDescriptions = new ArrayList<>();

        checkRequiredFieldsPresence(user, errorDescriptions);
        validateInputForPatterns(user, errorDescriptions);

        throwExceptionIfRequired(errorDescriptions);
    }

    public static void validatePartialUserData(UserInfoDTO user) {
        checkAllFieldsPresenceForPartialUpdate(user);

        List<ErrorDescription> errorDescriptions = new ArrayList<>();
        checkAllFieldsAbsence(user, errorDescriptions);

        validateEmailAgainstPattern(user, errorDescriptions);
        validateBirthDateAgainstCurrentDate(user, errorDescriptions);

        throwExceptionIfRequired(errorDescriptions);
    }

    public static void validateAllUserData(UserInfoDTO user) {
        List<ErrorDescription> errorDescriptions = new ArrayList<>();
        checkAllFieldsPresence(user, errorDescriptions);

        validateEmailAgainstPattern(user, errorDescriptions);
        validateBirthDateAgainstCurrentDate(user, errorDescriptions);

        throwExceptionIfRequired(errorDescriptions);
    }

    private static void throwExceptionIfRequired(List<ErrorDescription> errorDescriptions) {
        if (errorDescriptions.size() != 0)
            throw new ValidationFailedException(errorDescriptions);
    }

    private static void validateInputForPatterns(UserInfoDTO user, List<ErrorDescription> errorDescriptions) {
        validateEmailAgainstPattern(user, errorDescriptions);
        validateBirthDateAgainstCurrentDate(user, errorDescriptions);
    }

    private static void checkAllFieldsPresence(UserInfoDTO user, List<ErrorDescription> errorDescriptions) {
        if (user.address() == null)
            errorDescriptions.add(createErrorResponse("'address' field should be present"));
        if (user.phoneNumber() == null)
            errorDescriptions.add(createErrorResponse("'phoneNumber' field should be present"));

        checkRequiredFieldsPresence(user, errorDescriptions);
    }

    private static void checkRequiredFieldsPresence(UserInfoDTO user, List<ErrorDescription> errorDescriptions) {
        if (user.email() == null)
            errorDescriptions.add(createErrorResponse("'email' field should be present"));
        if (user.firstName() == null)
            errorDescriptions.add(createErrorResponse("'firstName' field should be present"));
        if (user.lastName() == null)
            errorDescriptions.add(createErrorResponse("'lastName' field should be present"));
        if (user.birthDate() == null)
            errorDescriptions.add(createErrorResponse("'birthDate' field should be present"));
    }

    private static void checkAllFieldsAbsence(UserInfoDTO user, List<ErrorDescription> errorDescriptions) {
        if (user.email() == null && user.firstName() == null && user.lastName() == null &&
                user.birthDate() == null && user.address() == null && user.phoneNumber() == null)
            errorDescriptions.add(
                    createErrorResponse("At least one field out of the following list should be present: 'email', 'firstName', 'lastName', " +
                            "'birthDate', 'address', 'phoneNumber'")
            );
    }

    private static void checkAllFieldsPresenceForPartialUpdate(UserInfoDTO user) {
        if (user.email() != null && user.firstName() != null && user.lastName() != null &&
                user.birthDate() != null && user.address() != null && user.phoneNumber() != null)
            throw new IncorrectRequestException("Request body contains all user fields. Use endpoint with PUT method");
    }

    private static void validateEmailAgainstPattern(UserInfoDTO user, List<ErrorDescription> errorDescriptions) {
        if (user.email() != null && !user.email().matches("[a-z]+\\.?[a-z0-9]+@[a-z]{2,6}(\\.[a-z]{2,4}){1,2}"))
            errorDescriptions.add(createErrorResponse("Email should start with a shorter letter and do not contain capital letters or special symbols (e.g. examplemail@lll.kpi.ua)"));
    }

    private static void validateBirthDateAgainstCurrentDate(UserInfoDTO user, List<ErrorDescription> errorDescriptions) {
        if (user.birthDate() != null && LocalDate.now().isBefore(user.birthDate()))
            errorDescriptions.add(createErrorResponse("Birth date should be less than current date value"));
    }

    private static ErrorDescription createErrorResponse(String detail) {
        return new ErrorDescription(400, detail);
    }
}
