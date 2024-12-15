package com.example.demo.Services;

import com.example.demo.DTOs.RegistrationDto;
import com.example.demo.Model.User;

public interface UserService {
    void saveUser(RegistrationDto registrationDto);
    String getUserRole(String username);
    User findByEmail(String email);

    User findByUsername(String username);
}
