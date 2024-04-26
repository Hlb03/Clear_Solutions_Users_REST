package com.clear.solutions.user_restful.dto;

import java.time.LocalDate;

public record UserInfoDTO(
        String email,
        String firstName,
        String lastName,
        LocalDate birthDate,
        String address,
        String phoneNumber
){}
