package com.springboot.usermanagement.service;

import com.springboot.usermanagement.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);
    UserDto getUserById(Long id);
    List<UserDto> getAllUsers();
    UserDto updateUsers(Long id, UserDto userDto);
    void deleteUser(Long id);
}
