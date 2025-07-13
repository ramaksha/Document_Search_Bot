package com.example.document_search_bot.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/me")
public class AuthTestController {
    @GetMapping
    public String whoami(Authentication auth) {
        return "Authenticated as: " + auth.getName()+" "+auth.getAuthorities()+" "+auth.isAuthenticated();
    }
}
