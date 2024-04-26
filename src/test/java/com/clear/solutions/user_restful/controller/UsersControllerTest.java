package com.clear.solutions.user_restful.controller;

import com.clear.solutions.user_restful.dto.DataContainerDTO;
import com.clear.solutions.user_restful.dto.UserInfoDTO;
import com.clear.solutions.user_restful.entity.Users;
import com.clear.solutions.user_restful.exceptions.NotSupportedAgeException;
import com.clear.solutions.user_restful.exceptions.UserNotFoundException;
import com.clear.solutions.user_restful.service.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UsersController.class)
public class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private UsersService usersService;

    private final String BASIC_URL = "/v1/users";

    @Test
    public void getUsersByBirthDateRange_OkResponse() throws Exception {
        LocalDate startDate = LocalDate.of(2010, 10, 10);
        LocalDate endDate = LocalDate.of(2010, 10, 10);

        when(usersService.getAllByBirthDateRange(startDate, endDate))
                .thenReturn(new DataContainerDTO<>(
                                List.of(
                                        new Users(1L)
                                )
                        )
                );

        mockMvc.perform(get(BASIC_URL)
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(1))
                .andExpect(jsonPath("$.data.[0].id").value(1L));
    }

    @Test
    public void getUsersByBirthDateRange_BadRequest_StartDateIsBiggerThanEndOne() throws Exception {
        LocalDate startDate = LocalDate.of(2015, 10, 10);
        LocalDate endDate = LocalDate.of(2010, 10, 10);

        when(usersService.getAllByBirthDateRange(startDate, endDate))
                .thenReturn(new DataContainerDTO<>(
                                List.of(
                                        new Users(1L)
                                )
                        )
                );

        mockMvc.perform(get(BASIC_URL)
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.[0].detail").value("Start date (%s) should be earlier than end date (%s)".formatted(startDate, endDate)))
                .andExpect(jsonPath("$.errors.[0].statusCode").value("400"));
    }

    @Test
    public void createUser_CreatedResponse() throws Exception {
        DataContainerDTO<UserInfoDTO> container = new DataContainerDTO<>(
                new UserInfoDTO("email@gmail.com", "name", "surname",
                        LocalDate.of(2000, 10, 10), null, null)
        );

        when(usersService.createNewUser(container.data())).thenReturn(1L);

        mockMvc.perform(post(BASIC_URL)
                        .content(mapper.writeValueAsString(container))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().stringValues("Location", "/1"));
    }

    @Test
    public void createUser_BadRequest_UserUnderMinimalAge() throws Exception {
        DataContainerDTO<UserInfoDTO> container = new DataContainerDTO<>(
                new UserInfoDTO("email@gmail.com", "name", "surname",
                        LocalDate.of(2010, 10, 10), null, null)
        );

        when(usersService.createNewUser(container.data())).thenThrow(new NotSupportedAgeException("Application works with users whose age is 18+"));

        mockMvc.perform(post(BASIC_URL)
                        .content(mapper.writeValueAsString(container))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.detail").value("Application works with users whose age is 18+"))
                .andExpect(jsonPath("$.errors.statusCode").value("400"));
    }

    @Test
    public void createUser_BadRequest_DataContainerIsAbsent() throws Exception {
        DataContainerDTO<UserInfoDTO> container = new DataContainerDTO<>(
                null
        );

        when(usersService.createNewUser(container.data())).thenReturn(1L);

        mockMvc.perform(post(BASIC_URL)
                        .content(mapper.writeValueAsString(container))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.[0].detail").value("Request body should contain 'data' argument with actual user data"))
                .andExpect(jsonPath("$.errors.[0].statusCode").value("400"));
    }

    @Test
    public void createUser_BadRequest_BadRequest_RequiredFieldsAbsent() throws Exception {
        DataContainerDTO<UserInfoDTO> container = new DataContainerDTO<>(
                new UserInfoDTO(null, null, null, null, "Street 10/2", "0502123369")
        );

        when(usersService.createNewUser(container.data())).thenReturn(1L);

        mockMvc.perform(post(BASIC_URL)
                        .content(mapper.writeValueAsString(container))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.[0].detail").value("'email' field should be present"))
                .andExpect(jsonPath("$.errors.[0].statusCode").value("400"))
                .andExpect(jsonPath("$.errors.[1].detail").value("'firstName' field should be present"))
                .andExpect(jsonPath("$.errors.[1].statusCode").value("400"))
                .andExpect(jsonPath("$.errors.[2].detail").value("'lastName' field should be present"))
                .andExpect(jsonPath("$.errors.[2].statusCode").value("400"))
                .andExpect(jsonPath("$.errors.[3].detail").value("'birthDate' field should be present"))
                .andExpect(jsonPath("$.errors.[3].statusCode").value("400"));
    }

    @Test
    public void createUser_BadRequest_InvalidDataPattern() throws Exception {
        DataContainerDTO<UserInfoDTO> container = new DataContainerDTO<>(
                new UserInfoDTO("TEST@gmail.com", "name", "surname",
                        LocalDate.of(2025, 10, 10), "Street 10/2", "0502123369")
        );

        when(usersService.createNewUser(container.data())).thenReturn(1L);

        mockMvc.perform(post(BASIC_URL)
                        .content(mapper.writeValueAsString(container))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.[0].detail").value("Email should start with a shorter letter and do not contain capital letters or special symbols (e.g. examplemail@lll.kpi.ua)"))
                .andExpect(jsonPath("$.errors.[0].statusCode").value("400"))
                .andExpect(jsonPath("$.errors.[1].detail").value("Birth date should be less than current date value"))
                .andExpect(jsonPath("$.errors.[1].statusCode").value("400"));
    }

    @Test
    public void partialUserUpdate_OkResponse() throws Exception {
        DataContainerDTO<UserInfoDTO> container = new DataContainerDTO<>(
                new UserInfoDTO(null, null, null, null, "Street 10/2", "0502123369")
        );

        doNothing().when(usersService).partialUpdateUserData(1L, container.data());

        mockMvc.perform(patch(BASIC_URL + "/1")
                        .content(mapper.writeValueAsString(container))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void partialUserUpdate_NotFound() throws Exception {
        DataContainerDTO<UserInfoDTO> container = new DataContainerDTO<>(
                new UserInfoDTO(null, null, null, null, "Street 10/2", "0502123369")
        );

        doThrow(new UserNotFoundException("User with identifier 1 wasn't found"))
                .when(usersService).partialUpdateUserData(1L, container.data());

        mockMvc.perform(patch(BASIC_URL + "/1")
                        .content(mapper.writeValueAsString(container))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors.detail").value("User with identifier 1 wasn't found"))
                .andExpect(jsonPath("$.errors.statusCode").value("404"));
    }

    @Test
    public void partialUserUpdate_BadRequest_DataContainerIsAbsent() throws Exception {
        DataContainerDTO<UserInfoDTO> container = new DataContainerDTO<>(
                null
        );

        doNothing().when(usersService).partialUpdateUserData(1L, container.data());

        mockMvc.perform(patch(BASIC_URL + "/1")
                        .content(mapper.writeValueAsString(container))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.[0].detail").value("Request body should contain 'data' argument with actual user data"))
                .andExpect(jsonPath("$.errors.[0].statusCode").value("400"));
    }

    @Test
    public void partialUserUpdate_BadRequest_AllFieldsAreAbsent() throws Exception {
        DataContainerDTO<UserInfoDTO> container = new DataContainerDTO<>(
                new UserInfoDTO(null, null, null, null, null, null)
        );

        doNothing().when(usersService).partialUpdateUserData(1L, container.data());

        mockMvc.perform(patch(BASIC_URL + "/1")
                        .content(mapper.writeValueAsString(container))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.[0].detail").value("At least one field out of the following list should be present: 'email', 'firstName', 'lastName', 'birthDate', 'address', 'phoneNumber'"))
                .andExpect(jsonPath("$.errors.[0].statusCode").value("400"));
    }

    @Test
    public void partialUserUpdate_MethodNotAllowed_AllFieldsArePresent() throws Exception {
        DataContainerDTO<UserInfoDTO> container = new DataContainerDTO<>(
                new UserInfoDTO("email@gmail.com", "name", "surname",
                        LocalDate.of(2000, 10, 10), "Street 10", "0677508213")
        );

        doNothing().when(usersService).partialUpdateUserData(1L, container.data());

        mockMvc.perform(patch(BASIC_URL + "/1")
                        .content(mapper.writeValueAsString(container))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.errors.detail").value("Request body contains all user fields. Use endpoint with PUT method"))
                .andExpect(jsonPath("$.errors.statusCode").value("405"));
    }

    @Test
    public void partialDataUpdate_BadRequest_InvalidDataPatterns() throws Exception {
        DataContainerDTO<UserInfoDTO> container = new DataContainerDTO<>(
                new UserInfoDTO("TEST@gmail.com", null, null,
                        LocalDate.of(2025, 10, 10), null, null)
        );

        doNothing().when(usersService).partialUpdateUserData(1L, container.data());

        mockMvc.perform(patch(BASIC_URL + "/1")
                        .content(mapper.writeValueAsString(container))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.[0].detail").value("Email should start with a shorter letter and do not contain capital letters or special symbols (e.g. examplemail@lll.kpi.ua)"))
                .andExpect(jsonPath("$.errors.[0].statusCode").value("400"))
                .andExpect(jsonPath("$.errors.[1].detail").value("Birth date should be less than current date value"))
                .andExpect(jsonPath("$.errors.[1].statusCode").value("400"));
    }

    @Test
    public void fullUserUpdate_OkResponse() throws Exception {
        DataContainerDTO<UserInfoDTO> container = new DataContainerDTO<>(
                new UserInfoDTO("email@gmail.com", "name", "surname",
                        LocalDate.of(2000, 10, 10), "Street 10/2", "0502123369")
        );

        doNothing().when(usersService).updateAllUserData(1L, container.data());

        mockMvc.perform(put(BASIC_URL + "/1")
                        .content(mapper.writeValueAsString(container))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void fullUserUpdate_NotFound() throws Exception {
        DataContainerDTO<UserInfoDTO> container = new DataContainerDTO<>(
                new UserInfoDTO("email@gmail.com", "name", "surname",
                        LocalDate.of(2000, 10, 10), "Street 10/2", "0502123369")
        );

        doThrow(new UserNotFoundException("User with identifier 1 wasn't found"))
                .when(usersService).updateAllUserData(1L, container.data());

        mockMvc.perform(put(BASIC_URL + "/1")
                        .content(mapper.writeValueAsString(container))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors.detail").value("User with identifier 1 wasn't found"))
                .andExpect(jsonPath("$.errors.statusCode").value("404"));
    }

    @Test
    public void fullUserUpdate_BadRequest_DataContainIsAbsent() throws Exception {
        DataContainerDTO<UserInfoDTO> container = new DataContainerDTO<>(
                null
        );

        doNothing().when(usersService).updateAllUserData(1L, container.data());

        mockMvc.perform(put(BASIC_URL + "/1")
                        .content(mapper.writeValueAsString(container))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.[0].detail").value("Request body should contain 'data' argument with actual user data"))
                .andExpect(jsonPath("$.errors.[0].statusCode").value("400"));
    }

    @Test
    public void fullUserUpdate_BadRequest_NotAllFieldsArePresent() throws Exception {
        DataContainerDTO<UserInfoDTO> container = new DataContainerDTO<>(
                new UserInfoDTO("email@gmail.com", "name", "surname",
                        LocalDate.of(2000, 10, 10), null, null)
        );

        doNothing().when(usersService).updateAllUserData(1L, container.data());

        mockMvc.perform(put(BASIC_URL + "/1")
                        .content(mapper.writeValueAsString(container))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.[0].detail").value("'address' field should be present"))
                .andExpect(jsonPath("$.errors.[0].statusCode").value("400"))
                .andExpect(jsonPath("$.errors.[1].detail").value("'phoneNumber' field should be present"))
                .andExpect(jsonPath("$.errors.[1].statusCode").value("400"));
    }

    @Test
    public void fullUserUpdate_BadRequest_InvalidDataPatterns() throws Exception {
        DataContainerDTO<UserInfoDTO> container = new DataContainerDTO<>(
                new UserInfoDTO("INVALIDEMAIL@gmail.com", "name", "surname",
                        LocalDate.of(2030, 10, 10), "Address 12a", "0552395786")
        );

        doNothing().when(usersService).updateAllUserData(1L, container.data());

        mockMvc.perform(put(BASIC_URL + "/1")
                        .content(mapper.writeValueAsString(container))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.[0].detail").value("Email should start with a shorter letter and do not contain capital letters or special symbols (e.g. examplemail@lll.kpi.ua)"))
                .andExpect(jsonPath("$.errors.[0].statusCode").value("400"))
                .andExpect(jsonPath("$.errors.[1].detail").value("Birth date should be less than current date value"))
                .andExpect(jsonPath("$.errors.[1].statusCode").value("400"));
    }

    @Test
    public void deleteUserById_OkResponse() throws Exception {
        doNothing().when(usersService).deleteUserById(1L);

        mockMvc.perform(delete(BASIC_URL + "/1"))
                .andExpect(status().isNoContent());
    }
}
