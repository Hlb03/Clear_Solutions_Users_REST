package com.clear.solutions.user_restful.service;

import com.clear.solutions.user_restful.dto.DataContainerDTO;
import com.clear.solutions.user_restful.dto.UserInfoDTO;
import com.clear.solutions.user_restful.entity.Users;

import java.time.LocalDate;
import java.util.List;

public interface UsersService {

    DataContainerDTO<List<Users>> getAllByBirthDateRange(LocalDate startDate, LocalDate endDate);

    Long createNewUser(UserInfoDTO userInfoDTO);

    void updateAllUserData(Long userId, UserInfoDTO newUserInfo);

    void partialUpdateUserData(Long userId, UserInfoDTO partialUserInfo);

    void deleteUserById(Long id);
}
