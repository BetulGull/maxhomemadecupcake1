package com.example.demo.Controller;

import com.example.demo.Model.User;
import com.example.demo.Repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class AdminController {

    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/admin/users")
    public String getUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "/admin/user-list";
    }

    @PostMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable int id) {
        userRepository.deleteUserRolesByUserId(id);
        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/update/{id}")
    public String updateUser(@PathVariable int id, @ModelAttribute("user") User updatedUser) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            existingUser.setUsername(updatedUser.getUsername());
            existingUser.setEmail(updatedUser.getEmail());
            userRepository.save(existingUser);
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/users/edit/{id}")
    public String editUserForm(@PathVariable int id, Model model) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            model.addAttribute("user", optionalUser.get());
            return "/admin/user-edit";
        } else {
            return "redirect:/admin/users";
        }
    }
}
