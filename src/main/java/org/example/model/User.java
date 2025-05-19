package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    private String lastName;
    private String firstName;
    private String email;
    private String due;
    private String website;

    @Override
    public String toString() {
        return "User{" +
                "lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", email='" + email + '\'' +
                ", due='" + due + '\'' +
                ", website='" + website + '\'' +
                '}';
    }
} 