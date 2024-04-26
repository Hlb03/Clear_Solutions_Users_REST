package com.clear.solutions.user_restful.controller;

import com.clear.solutions.user_restful.dto.DataContainerDTO;
import com.clear.solutions.user_restful.dto.UserInfoDTO;
import com.clear.solutions.user_restful.entity.Users;
import com.clear.solutions.user_restful.exception_hadler.exception_body.ErrorDescription;
import com.clear.solutions.user_restful.exceptions.ValidationFailedException;
import com.clear.solutions.user_restful.service.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static com.clear.solutions.user_restful.util.UsersValidationUtil.validatePartialUserData;
import static com.clear.solutions.user_restful.util.UsersValidationUtil.validateUserData;
import static com.clear.solutions.user_restful.util.ValidateDataContainer.validateDataContainer;

@Controller
@RequestMapping("/v1/users")
public class UsersController {

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    private final UsersService usersService;

    @GetMapping
    public ResponseEntity<DataContainerDTO<List<Users>>> getUsersByBirthdateRange(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        if (startDate.isAfter(endDate))
            throw new ValidationFailedException(
                    List.of(new ErrorDescription(400, "Start date (%s) should be earlier than end date (%s)".formatted(startDate, endDate)))
            );

        return ResponseEntity.ok(usersService.getAllByBirthDateRange(startDate, endDate));
    }

    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody DataContainerDTO<UserInfoDTO> userInfo, HttpServletRequest request) {
        validateDataContainer(userInfo);
        validateUserData(userInfo.data());

        return ResponseEntity.created(
                        URI.create(request.getServletPath() + "/" + usersService.createNewUser(userInfo.data()).toString()))
                .build();
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Void> partialUserUpdate(@PathVariable Long userId, @RequestBody DataContainerDTO<UserInfoDTO> partialUserInfo) {
        validateDataContainer(partialUserInfo);
        validatePartialUserData(partialUserInfo.data());

        usersService.partialUpdateUserData(userId, partialUserInfo.data());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Void> fullUserUpdate(@PathVariable Long userId, @RequestBody DataContainerDTO<UserInfoDTO> userInfo) {
        validateDataContainer(userInfo);
        validateUserData(userInfo.data());

        usersService.updateAllUserData(userId, userInfo.data());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long userId) {
        usersService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }
}
