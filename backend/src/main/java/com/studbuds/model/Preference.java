package com.studbuds.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.persistence.*;

@Entity
@Table(name = "preferences", uniqueConstraints = @UniqueConstraint(columnNames = "user_id"))
public class Preference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonBackReference
    private User user;

    @Column(name = "available_days")
    private String availableDays;

    @Column(name = "subjects_to_learn")
    private String subjectsToLearn;

    @Column(name = "subjects_to_teach")
    private String subjectsToTeach;

    public Preference() {}

    // Getters and setters:
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getAvailableDays() { return availableDays; }
    public void setAvailableDays(String availableDays) { this.availableDays = availableDays; }
    
    public String getSubjectsToLearn() { return subjectsToLearn; }
    public void setSubjectsToLearn(String subjectsToLearn) { this.subjectsToLearn = subjectsToLearn; }
    
    public String getSubjectsToTeach() { return subjectsToTeach; }
    public void setSubjectsToTeach(String subjectsToTeach) { this.subjectsToTeach = subjectsToTeach; }
}