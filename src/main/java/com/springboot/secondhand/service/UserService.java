package com.springboot.secondhand.service;

import com.springboot.secondhand.converter.UserDtoConverter;
import com.springboot.secondhand.dto.CreateUserRequest;
import com.springboot.secondhand.dto.UpdateUserRequest;
import com.springboot.secondhand.dto.UserDto;
import com.springboot.secondhand.exception.UserIsNotActiveException;
import com.springboot.secondhand.exception.UserNotFoundException;
import com.springboot.secondhand.model.User;
import com.springboot.secondhand.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserDtoConverter userDtoConverter;

    public UserService(UserRepository userRepository, UserDtoConverter userDtoConverter) {
        this.userRepository = userRepository;
        this.userDtoConverter = userDtoConverter;
    }

    public List<UserDto> getAllUsers(){
        return userDtoConverter.convertFromUserList(userRepository.findAll());
    }

    public UserDto getUserByMail(String mail){
        User user = findUserByMail(mail);
        return userDtoConverter.convertFromUser(user);
    }

    public UserDto createUser(CreateUserRequest userRequest){
        User user = new User();
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setMiddleName(userRequest.getMiddleName());
        user.setMail(userRequest.getMail());
        user.setActive(false);

        return userDtoConverter.convertFromUser(userRepository.save(user));
    }

    public UserDto updateUser(String mail, UpdateUserRequest userRequest){
        User user = findUserByMail(mail);
        if(!user.getActive()){
            logger.warn(String.format("The user wanted update is not active!, user mail: %s", mail));
            throw new UserIsNotActiveException();
        }

        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setMiddleName(userRequest.getMiddleName());

        return userDtoConverter.convertFromUser(userRepository.save(user));
    }

    public void deactivateUser(Long id){
        User user = findUserById(id);
        user.setActive(false);
        userRepository.save(user);
    }

    public void activateUser(Long id){
        User user = findUserById(id);
        user.setActive(true);
        userRepository.save(user);
    }

    private User findUserById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User couldn't be found by following id: " + id));
    }

    private User findUserByMail(String mail){
        return userRepository.findByMail(mail)
                .orElseThrow(() -> new UserNotFoundException("User couldn't be found by following mail: " + mail));
    }

    public void deleteUser(Long id) {
        findUserById(id);
        userRepository.deleteById(id);
    }
}
