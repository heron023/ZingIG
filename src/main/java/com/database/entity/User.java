package com.database.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class User {
    private int id;
    private String userName;
    private String name;
    private String lastName;
    private LocalDate birthDate;
    private String email;
    private String password;
    private Gender gender;

    private List<Hobby> hobbies = new ArrayList<>();
    private List<User> matchedUsers = new ArrayList<>();

    public User() {
    }

    public User(int id, String userName, String name, String lastName,
                LocalDate birthDate, String email, String password,
                Gender gender, List<Hobby> hobbies) {
        this.id = id;
        this.userName = userName;
        this.name = name;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.hobbies = (hobbies != null) ? hobbies : new ArrayList<>();
    }

    public User(int id, String userName, String name, String lastName,
                LocalDate birthDate, String email, String password, Gender gender) {
        this(id, userName, name, lastName, birthDate, email, password, gender, new ArrayList<>());
    }

    public User(int id, String userName, String name, String lastName,
                LocalDate birthDate, String email, String password,
                Gender gender, List<Hobby> hobbies, List<User> matchedUsers) {
        this.id = id;
        this.userName = userName;
        this.name = name;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.hobbies = (hobbies != null) ? hobbies : new ArrayList<>();
        this.matchedUsers = (matchedUsers != null) ? matchedUsers : new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
    public List<Hobby> getHobbies() { return hobbies; }
    public void setHobbies(List<Hobby> hobbies) {
        this.hobbies = (hobbies != null) ? hobbies : new ArrayList<>();
    }

    public List<User> getMatchedUsers() { return matchedUsers; }
    public void setMatchedUsers(List<User> matchedUsers) {
        this.matchedUsers = (matchedUsers != null) ? matchedUsers : new ArrayList<>();
    }
}