package com.example.document_search_bot.service;





import com.example.document_search_bot.dto.UserDtoRes;
import com.example.document_search_bot.entity.User;
import com.example.document_search_bot.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
private UserRepository userRepository;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void saveEntry(User myEntry) {
        
        myEntry.setPassword(passwordEncoder.encode(myEntry.getPassword()));
        myEntry.setRoles("USER");
        userRepository.save(myEntry);
    }


    public List<UserDtoRes>getAll(){
        List<User> all = userRepository.findAll();
        List<UserDtoRes> userDTORes = new ArrayList<>();
        for(User u:all){
          userDTORes.add(new UserDtoRes(u.getUser_id(),u.getUsername(),u.getRoles()));
        }
        return userDTORes;
    }

    public UserDtoRes toggleRoleByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        String currentRole = user.getRoles();
        String newRole = "USER".equalsIgnoreCase(currentRole) ? "ADMIN" : "USER";
        user.setRoles(newRole);
        userRepository.save(user);

        return new UserDtoRes(user.getUser_id(), user.getUsername(), user.getRoles());
    }

    @Transactional
    public User getId(Long id){
        return userRepository.getById(id);
    }

    public  void  deleteById(Long id){
        userRepository.deleteById(id);
    }

    public ResponseEntity<String> updateById(Long id, User newEntry) {
        Optional<User> optionalEntry = userRepository.findById(id);
        if (optionalEntry.isPresent()) {
            User oldEntry = optionalEntry.get();
            oldEntry.setUsername(newEntry.getUsername());
            oldEntry.setPassword(newEntry.getPassword());
            User updatedEntry = userRepository.save(oldEntry);
            return ResponseEntity.ok(updatedEntry.getRoles());
        } else {
            return ResponseEntity.status(404).build();
        }
    }
    public User findByUserName(String username){
        return userRepository.findByUsername(username);
    }

    public void deleteByUserName(String username) {
        userRepository.deleteByUsername(username);
    }
}
