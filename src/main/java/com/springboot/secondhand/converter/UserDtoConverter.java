package com.springboot.secondhand.converter;

import com.springboot.secondhand.dto.UserDto;
import com.springboot.secondhand.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserDtoConverter {

    public UserDto convertFromUser(User user){
        return new UserDto(user.getMail(), user.getFirstName(), user.getLastName(), user.getMiddleName());
    }

    public List<UserDto> convertFromUserList(List<User> userList){
        return userList.stream()
                .map(user -> new UserDto(user.getMail(), user.getFirstName(), user.getLastName(), user.getMiddleName()))
                .collect(Collectors.toList());
    }
}
