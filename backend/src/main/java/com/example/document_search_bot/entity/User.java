package com.example.document_search_bot.entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name="users", uniqueConstraints = @UniqueConstraint(columnNames = "username"),
        indexes = @Index(name = "idx_username", columnList = "username"))
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String roles;
    @Column(nullable = false)
    private String email;
}
