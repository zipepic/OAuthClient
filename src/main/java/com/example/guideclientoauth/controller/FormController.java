package com.example.guideclientoauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class FormController {
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @GetMapping("/")
    public String home() {
        return "<h1>Home</h1>";
    }
}
