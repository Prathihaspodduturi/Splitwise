package com.PrathihasProjects.PrathihasSplitwise.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    // annotate class as an entity and map to database table

    // define the fields

    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    public User() {}

    public User(String username, String password)
    {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

}
