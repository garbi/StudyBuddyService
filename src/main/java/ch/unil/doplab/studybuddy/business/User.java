package ch.unil.doplab.studybuddy.business;

import java.util.UUID;

public class User {
    private UUID id;
    private String firstName;
    private String lastName;

    private String username;
    private String email;

//    private String passwordHash;

    public User() {
        this.id = null;
        this.firstName = null;
        this.lastName = null;
        this.email = null;
        this.username = null;
    }

    public User(String firstName, String lastName, String email, String username) {
        this.id = null;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
    }

    public void merge(User user) {
        if (user.id != null) {
            this.id = user.id;
        }
        if (user.firstName != null) {
            this.firstName = user.firstName;
        }
        if (user.lastName != null) {
            this.lastName = user.lastName;
        }
        if (user.email != null) {
            this.email = user.email;
        }
        if (user.username != null) {
            this.username = user.username;
        }
    }
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String describe() {
        return "id=" + this.id + ", firstName='" + this.firstName + "', lastName='" + this.lastName + "'" +
                ", username='" + this.username + "', email='" + this.email + "'";
    }

    public String toString() {
        return this.getClass().getSimpleName() + "{" + this.describe() + "}";
    }
}
