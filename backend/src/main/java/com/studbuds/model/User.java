package com.studbuds.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   
   @Column(unique = true, nullable = false)
   private String email;
   
   @Column(nullable = false)
   private String password;
   
   private String major;
   private String year;
   private LocalDateTime createdAt = LocalDateTime.now();
   
   // Getters and setters
   public Long getId() { return id; }
   public void setId(Long id) { this.id = id; }
   public String getEmail() { return email; }
   public void setEmail(String email) { this.email = email; }
   public String getPassword() { return password; }
   public void setPassword(String password) { this.password = password; }
   public String getMajor() { return major; }
   public void setMajor(String major) { this.major = major; }
   public String getYear() { return year; }
   public void setYear(String year) { this.year = year; }
   public LocalDateTime getCreatedAt() { return createdAt; }
   public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}