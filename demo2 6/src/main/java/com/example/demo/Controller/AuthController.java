package com.example.demo.Controller;


import com.example.demo.DTOs.RegistrationDto;
import com.example.demo.Model.Customer;
import com.example.demo.Model.User;
import com.example.demo.Repository.CustomerRepository;
import com.example.demo.Services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Controller
public class AuthController {
    private UserService userService;
    private CustomerRepository customerRepository;

    @Autowired
    public AuthController(UserService userService, CustomerRepository customerRepository) {
        this.userService = userService;
        this.customerRepository = customerRepository;
    }

    @GetMapping("/register")
    public String getRegisterForm(Model model) {
        RegistrationDto user = new RegistrationDto();
        model.addAttribute("user", user);
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam("file") MultipartFile file,
                               @Valid @ModelAttribute("user") RegistrationDto userDto,
                               BindingResult bindingResult, Model model) {
        // Kullanıcı ve şifre kontrolü
        User existingUserByEmail = userService.findByEmail(userDto.getEmail());
        if (existingUserByEmail != null) {
            bindingResult.rejectValue("email", "error.user", "There is already an account registered with this email.");
        }
        if (!userDto.isPasswordMatch()) {
            bindingResult.rejectValue("rePassword", "error.user", "Passwords don't match");
        }
        User existingUserByUsername = userService.findByUsername(userDto.getUsername());
        if (existingUserByUsername != null) {
            bindingResult.rejectValue("username", "error.user", "There is already a user registered with this username.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", userDto);
            return "auth/register";
        }

        try {
            // Resmi kaydetme işlemi
            String fileName = saveImage(file);
            userDto.setProfilePicture(fileName);
        } catch (IOException e) {
            bindingResult.rejectValue("file", "error.user", "Could not save image file: " + e.getMessage());
            model.addAttribute("user", userDto);
            return "auth/register";
        }

        // User ve Customer oluşturma
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());  // Ensure proper password hashing before saving
        user.setProfilePicture(userDto.getProfilePicture());

        Customer customer = new Customer();
        customer.setUsername(user.getUsername());
        customer.setPhotoUrl(user.getProfilePicture());
        customer.setName(userDto.getUsername());
        customer.setAddress(userDto.getEmail());

        // User ile Customer'ı ilişkilendir
        user.setCustomer(customer);

        // Veritabanına kayıt
        customerRepository.save(customer); // Önce Customer kaydedilir
        userService.saveUser(userDto);     // Sonra User kaydedilir

        return "redirect:/?success";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        return "auth/login";
    }
    @GetMapping("/access-denied")
    public String accessDenied(){
        return "auth/access-denied";
    }
    private String saveImage(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get("src/main/resources/static/user-photos/");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Could not save image file: " + fileName, e);
        }
        return fileName;
    }

}