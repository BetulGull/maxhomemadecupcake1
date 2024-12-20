package com.example.demo;



import com.example.demo.DTOs.RegistrationDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(classes = Demo2Application.class)
@ComponentScan(basePackages = "com.example.demo")

class RegistrationDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidRegistrationDto() {
        RegistrationDto dto = new RegistrationDto();
        dto.setUsername("testuser");
        dto.setEmail("testuser@example.com");
        dto.setPassword("securepassword");
        dto.setRePassword("securepassword");

        Set<ConstraintViolation<RegistrationDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "DTO should be valid with correct data");
        assertTrue(dto.isPasswordMatch(), "Passwords should match");
    }

    @Test
    void testEmptyUsername() {
        RegistrationDto dto = new RegistrationDto();
        dto.setUsername("");
        dto.setEmail("testuser@example.com");
        dto.setPassword("securepassword");
        dto.setRePassword("securepassword");

        Set<ConstraintViolation<RegistrationDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Username should not be empty");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Username cannot be empty")));
    }

    @Test
    void testInvalidEmailFormat() {
        RegistrationDto dto = new RegistrationDto();
        dto.setUsername("testuser");
        dto.setEmail("invalid-email");
        dto.setPassword("securepassword");
        dto.setRePassword("securepassword");

        Set<ConstraintViolation<RegistrationDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Email should be invalid");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid email format")));
    }

    @Test
    void testEmptyPassword() {
        RegistrationDto dto = new RegistrationDto();
        dto.setUsername("testuser");
        dto.setEmail("testuser@example.com");
        dto.setPassword("");
        dto.setRePassword("");

        Set<ConstraintViolation<RegistrationDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Password should not be empty");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Password cannot be empty")));
    }

    @Test
    void testShortPassword() {
        RegistrationDto dto = new RegistrationDto();
        dto.setUsername("testuser");
        dto.setEmail("testuser@example.com");
        dto.setPassword("123");
        dto.setRePassword("123");

        Set<ConstraintViolation<RegistrationDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Password should be at least 6 characters long");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Password must be at least 6 characters long")));
    }

    @Test
    void testPasswordMismatch() {
        RegistrationDto dto = new RegistrationDto();
        dto.setUsername("testuser");
        dto.setEmail("testuser@example.com");
        dto.setPassword("securepassword");
        dto.setRePassword("differentpassword");

        Set<ConstraintViolation<RegistrationDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Validation should not catch mismatched passwords");
        assertFalse(dto.isPasswordMatch(), "Passwords should not match");
    }

    @Test
    void testEmptyEmail() {
        RegistrationDto dto = new RegistrationDto();
        dto.setUsername("testuser");
        dto.setEmail("");
        dto.setPassword("securepassword");
        dto.setRePassword("securepassword");

        Set<ConstraintViolation<RegistrationDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Email should not be empty");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Email cannot be empty")));
    }
}
