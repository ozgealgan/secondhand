package com.springboot.secondhand.service;

import com.springboot.secondhand.TestUtil;
import com.springboot.secondhand.converter.UserDtoConverter;
import com.springboot.secondhand.dto.CreateUserRequest;
import com.springboot.secondhand.dto.UpdateUserRequest;
import com.springboot.secondhand.dto.UserDto;
import com.springboot.secondhand.exception.UserIsNotActiveException;
import com.springboot.secondhand.exception.UserNotFoundException;
import com.springboot.secondhand.model.User;
import com.springboot.secondhand.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest extends TestUtil {

    private UserDtoConverter converter;
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    public void setUp() {
        converter = Mockito.mock(UserDtoConverter.class);
        userRepository = Mockito.mock(UserRepository.class);

        userService = new UserService(userRepository, converter);
    }

    @Test
    public void getAllUsers_itShouldReturnUserDtoList() {
        List<User> userList = generateUsers();
        List<UserDto> userDtoList = generateUserDtoList(userList);

        when(userRepository.findAll()).thenReturn(userList);
        when(converter.convertFromUserList(userList)).thenReturn(userDtoList);

        List<UserDto> result = userService.getAllUsers();

        assertEquals(userDtoList, result);
        verify(userRepository).findAll();
        verify(converter).convertFromUserList(userList);
    }

    @Test
    public void getUserByMail_whenUserMailExist_itShouldReturnUserDto() {
        String mail = "mail@gmail.com";
        User user = generateUser(mail);
        UserDto userDto = generateUserDto(mail);

        when(userRepository.findByMail(mail)).thenReturn(Optional.of(user));
        when(converter.convertFromUser(user)).thenReturn(userDto);

        UserDto result = userService.getUserByMail(mail);

        assertEquals(userDto, result);
        verify(userRepository).findByMail(mail);
        verify(converter).convertFromUser(user);
    }

    @Test
    public void getUserByMail_whenUserMailDoesNotExist_itShouldThrowUserNotFoundException() {
        String mail = "mail@gmail.com";

        when(userRepository.findByMail(mail)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                userService.getUserByMail(mail));

        verify(userRepository).findByMail(mail);
        verifyNoInteractions(converter);
    }

    @Test
    public void createUser_itShouldReturnCreatedUserDto() {
        String mail = "mail@gmail.com";
        CreateUserRequest request = new CreateUserRequest(mail, "firtName", "lastName", "");
        User user = new User(mail, "firtName", "lastName", "", false);
        User savedUser = new User(1L, mail, "firtName", "lastName", "", false);
        UserDto userDto = new UserDto(mail, "firtName", "lastName", "");

        when(userRepository.save(user)).thenReturn(savedUser);
        when(converter.convertFromUser(savedUser)).thenReturn(userDto);

        UserDto result = userService.createUser(request);

        assertEquals(result, userDto);
        verify(userRepository).save(user);
        verify(converter).convertFromUser(savedUser);
    }


    @Test
    public void updateUser_whenUserMailExistAndUserActive_itShouldReturnUpdatedUserDto() {
        String mail = "mail@gmail.com";
        UpdateUserRequest request = new UpdateUserRequest("firtName2", "lastName2", "middleName");
        User user = new User(mail, "firtName", "lastName", "", true);
        User savedUser = new User(1L, mail, "firtName2", "lastName2", "middleName", true);
        UserDto userDto = new UserDto(mail, "firtName2", "lastName2", "middleName");

        when(userRepository.findByMail(mail)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(savedUser);
        when(converter.convertFromUser(savedUser)).thenReturn(userDto);

        UserDto result = userService.updateUser(mail, request);

        assertEquals(result, userDto);
        verify(userRepository).findByMail(mail);
        verify(userRepository).save(user);
        verify(converter).convertFromUser(savedUser);
    }

    @Test
    public void updateUser_whenUserMailDoesNotExist_itShouldThrowUserNotFoundException() {
        String mail = "mail@gmail.com";
        UpdateUserRequest request = new UpdateUserRequest("firtName2", "lastName2", "middleName");

        when(userRepository.findByMail(mail)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                userService.updateUser(mail, request)
        );

        verify(userRepository).findByMail(mail);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(converter);
    }

    @Test
    public void updateUser_whenUserMailExistButUserIsNotActive_itShouldThrowUserNotActiveException() {
        String mail = "mail@gmail.com";
        UpdateUserRequest request = new UpdateUserRequest("firtName2", "lastName2", "middleName");
        User user = new User(mail, "firtName", "lastName", "", false);

        when(userRepository.findByMail(mail)).thenReturn(Optional.of(user));

        assertThrows(UserIsNotActiveException.class, () ->
                userService.updateUser(mail, request)
        );

        verify(userRepository).findByMail(mail);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(converter);
    }

    @Test
    public void deactivateUser_whenUserIdExist_itShouldUpdateUserByActiveFalse3() {


        User user = new User(userId, "mail@gmail.com", "firtName", "lastName", "", true);
        User savedUser = new User(userId, "mail@gmail.com", "firtName", "lastName", "", false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deactivateUser(userId);

        verify(userRepository).findById(userId);
        verify(userRepository).save(savedUser);
    }

    @Test
    public void deactivateUser_whenUserIdDoesNotExist_itShouldThrowUserNotFoundException() {

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                userService.deactivateUser(userId)
        );

        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void activateUser_whenUserIdExist_itShouldUpdateUserByActiveTrue() {

        User user = new User(userId, "mail@gmail.com", "firtName", "lastName", "", false);
        User savedUser = new User(userId, "mail@gmail.com", "firtName", "lastName", "", true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.activateUser(userId);

        verify(userRepository).findById(userId);
        verify(userRepository).save(savedUser);
    }

    @Test
    public void activateUser_whenUserIdDoesNotExist_itShouldThrowUserNotFoundException() {

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                userService.activateUser(userId)
        );

        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void deleteUser_whenUserIdExist_itShouldDeleteUser() {

        User user = new User(userId, "mail@gmail.com", "firtName", "lastName", "", false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        verify(userRepository).findById(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    public void deleteUser_whenUserIdDoesNotExist_itShouldThrowUserNotFoundException() {

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                userService.deleteUser(userId)
        );

        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }
}