package com.springboot.usermanagement.mapper;

import com.springboot.usermanagement.dto.UserDto;
import com.springboot.usermanagement.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDto toDTO(User user){
        return new UserDto(user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );
    }
    public User toEntity(UserDto userDto){
        return new User(userDto.id(),
                userDto.firstName(),
                userDto.lastName(),
                userDto.email()
        );
    }
}





