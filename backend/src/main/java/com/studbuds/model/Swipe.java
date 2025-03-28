package com.studbuds.model;

import javax.persistence.*;

@Entity
@Table(name = "swipes")
public class Swipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "from_user_id", nullable = false)
    private User fromUser;
    
    @ManyToOne
    @JoinColumn(name = "to_user_id", nullable = false)
    private User toUser;

    public Swipe() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public User getToUser() {
        return toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }
}