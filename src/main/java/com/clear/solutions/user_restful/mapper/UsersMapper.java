package com.clear.solutions.user_restful.mapper;

import com.clear.solutions.user_restful.dto.UserInfoDTO;
import com.clear.solutions.user_restful.entity.Users;
import org.springframework.stereotype.Component;

@Component
public class UsersMapper {

    public Users usersDtoToEntity(UserInfoDTO userDTO) {
        Users user = new Users();
        user.setFirstName(userDTO.firstName());
        user.setLastName(userDTO.lastName());
        user.setEmail(userDTO.email());
        user.setBirthDate(userDTO.birthDate());
        user.setAddress(userDTO.address());
        user.setPhoneNumber(userDTO.phoneNumber());

        return user;
    }
}
