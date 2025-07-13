package com.example.document_search_bot.repository;

import com.example.document_search_bot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    void deleteByUsername(String username);
}
