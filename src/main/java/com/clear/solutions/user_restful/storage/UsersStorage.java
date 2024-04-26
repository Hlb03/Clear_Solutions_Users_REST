package com.clear.solutions.user_restful.storage;

import com.clear.solutions.user_restful.entity.Users;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class UsersStorage {

    private final List<Users> userList = new ArrayList<>();
    private Long userIdCounter = 1L;

    public Optional<Users> getUserById(Long userId) {
        int userIndexInList = userList.indexOf(new Users(userId));
        if (userIndexInList < 0)
            return Optional.empty();

        return Optional.of(userList.get(userIndexInList));
    }

    public List<Users> getAllByBirthDateRange(LocalDate startDate, LocalDate endDate) {
        return userList.stream()
                .filter(user -> (user.getBirthDate().isAfter(startDate) || user.getBirthDate().isEqual(startDate)) &&
                        (user.getBirthDate().isBefore(endDate) || user.getBirthDate().isEqual(endDate)))
                .toList();
    }

    public Long addNewUser(Users userToAdd) {
        userToAdd.setId(userIdCounter);
        userIdCounter++;
        userList.add(userToAdd);
        return userIdCounter;
    }

    public void updateUserData(Users userToUpdate, String email, String firstName, String lastName,
                                  LocalDate birthDate, String address, String phoneNumber) {
        if (email != null)
            userToUpdate.setEmail(email);

        if (firstName != null)
            userToUpdate.setFirstName(firstName);

        if(lastName != null)
            userToUpdate.setLastName(lastName);

        if (birthDate != null)
            userToUpdate.setBirthDate(birthDate);

        if (address != null)
            userToUpdate.setAddress(address);

        if (phoneNumber != null)
            userToUpdate.setPhoneNumber(phoneNumber);
    }

    public void removeUser(Long userId) {
        userList.remove(new Users(userId));
    }
}
