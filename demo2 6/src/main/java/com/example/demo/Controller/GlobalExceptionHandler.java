package com.example.demo.Controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {
    // Handle OrderAlreadyExistsException


    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, RedirectAttributes ra) {
        if (!e.getMessage().contains("already handled")) { // Check for a specific condition
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/errorPage";
        }
        return "errorPage";  // Return the error view without redirecting if the condition is met
    }

}
