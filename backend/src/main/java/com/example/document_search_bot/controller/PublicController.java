package com.example.document_search_bot.controller;

import com.example.document_search_bot.dto.UserDtoRes;
import com.example.document_search_bot.entity.User;
import com.example.document_search_bot.service.CustomUserDetailsServiceImpl;
import com.example.document_search_bot.service.UserService;
import com.example.document_search_bot.util.EmbeddingUtil;
import com.example.document_search_bot.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public")
public class PublicController {
    @Autowired
    private UserService userService;
    @Autowired
    private CustomUserDetailsServiceImpl userDetailsService;
    @Autowired
    @Lazy
    private AuthenticationManager authenticationManager;
    @Autowired
    private EmbeddingUtil embeddings;
    @Autowired
    private JwtUtil jwtUtil;
    @GetMapping("/health-check")
    public String healthCheck(){
        return "ok";
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDtoRes> createEntry(@RequestBody User myEntry){
        try {
            userService.saveEntry(myEntry);
            UserDtoRes dto=new UserDtoRes(myEntry.getUser_id(), myEntry.getUsername(),myEntry.getRoles());
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user){
        try {
           authenticationManager.authenticate( new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword()));
            UserDetails userDetails=userDetailsService.loadUserByUsername(user.getUsername());
            String jwt=jwtUtil.generateToken(userDetails.getUsername());
            return  new ResponseEntity<>(jwt,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("incorrect username or password",HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/embed/{text}")
    public  HttpEntity<? extends Object> Embed(@PathVariable String text){
        try {
               return new ResponseEntity<>(embeddings.getEmbeddings(text),HttpStatus.OK) ;
        } catch (Exception e) {
            return new ResponseEntity<>("incorrect username or password",HttpStatus.BAD_REQUEST);
        }
    }
}
