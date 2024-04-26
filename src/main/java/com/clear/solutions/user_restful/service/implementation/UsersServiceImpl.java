package com.clear.solutions.user_restful.service.implementation;

import com.clear.solutions.user_restful.dto.DataContainerDTO;
import com.clear.solutions.user_restful.dto.UserInfoDTO;
import com.clear.solutions.user_restful.entity.Users;
import com.clear.solutions.user_restful.exceptions.NotSupportedAgeException;
import com.clear.solutions.user_restful.exceptions.UserNotFoundException;
import com.clear.solutions.user_restful.mapper.UsersMapper;
import com.clear.solutions.user_restful.service.UsersService;
import com.clear.solutions.user_restful.storage.UserStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class UsersServiceImpl implements UsersService {

    public UsersServiceImpl(UserStorage userStorage, UsersMapper usersMapper) {
        this.userStorage = userStorage;
        this.usersMapper = usersMapper;
    }

    private final UserStorage userStorage;
    private final UsersMapper usersMapper;

    @Value("${user.minimal.age}")
    private int minimumRegistrationAge;

    @Override
    public DataContainerDTO<List<Users>> getAllByBirthDateRange(LocalDate startDate, LocalDate endDate) {
        return new DataContainerDTO<>(userStorage.getAllByBirthDateRange(startDate, endDate));
    }

    @Override
    public Long createNewUser(UserInfoDTO userInfoDTO) {
        checkUserBirthDateValidity(userInfoDTO.birthDate());

        return userStorage.addNewUser(
                usersMapper.usersDtoToEntity(userInfoDTO)
        );
    }

    @Override
    public void updateAllUserData(Long userId, UserInfoDTO newUserInfo) {
        Users user = getUserFromStorage(userId);
        checkUserBirthDateValidity(newUserInfo.birthDate());

        userStorage.updateUserData(user, newUserInfo.email(), newUserInfo.firstName(), newUserInfo.lastName(),
                newUserInfo.birthDate(), newUserInfo.address(), newUserInfo.phoneNumber());
    }

    @Override
    public void partialUpdateUserData(Long userId, UserInfoDTO partialUserInfo) {
        Users user = getUserFromStorage(userId);
        if (partialUserInfo.birthDate() != null)
            checkUserBirthDateValidity(partialUserInfo.birthDate());

        userStorage.updateUserData(user, partialUserInfo.email(), partialUserInfo.firstName(), partialUserInfo.lastName(),
                partialUserInfo.birthDate(), partialUserInfo.address(), partialUserInfo.phoneNumber());
    }

    @Override
    public void deleteUserById(Long userId) {
        userStorage.removeUser(userId);
    }

    private void checkUserBirthDateValidity(LocalDate userBirthDate) {
        if (ChronoUnit.YEARS.between(userBirthDate, LocalDate.now()) < minimumRegistrationAge)
            throw new NotSupportedAgeException("Application works with users whose age is %s+".formatted(minimumRegistrationAge));
    }

    private Users getUserFromStorage(Long userId) {
        return userStorage.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with identifier %s wasn't found".formatted(userId)));
    }
}
