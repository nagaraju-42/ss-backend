package com.pastrypoint.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "pastry_users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String username;
    
    private String password;
    private String role; // Will be "OWNER" or "STAFF"
}