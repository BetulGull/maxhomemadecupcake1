package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpaceRestController {

    @GetMapping("/sindex")
    public String currentDestination(){
        return "redirect:/index";
    }


    @GetMapping("/cantina/menu/today")
    public String menu(){
        return "Microwaved Pizza";
    }
}
