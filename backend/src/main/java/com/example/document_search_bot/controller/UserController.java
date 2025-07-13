package com.example.document_search_bot.controller;


import com.example.document_search_bot.dto.UserDtoRes;
import com.example.document_search_bot.entity.User;
import com.example.document_search_bot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<UserDtoRes> list = userService.getAll();
        if (list != null && !list.isEmpty()) {
            return new ResponseEntity<>(list, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/{username}")
    public ResponseEntity<?> toggleRole(@PathVariable String username) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getName().equals(username)){
            User user = userService.findByUserName(username);
            return new ResponseEntity<>(new UserDtoRes(user.getUser_id(), user.getUsername(),user.getRoles()),HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PutMapping("/{username}")
    public ResponseEntity<UserDtoRes> toggleUserRole(@PathVariable String username) {
        UserDtoRes updatedUser = userService.toggleRoleByUsername(username);
        return ResponseEntity.ok(updatedUser);
    }
}