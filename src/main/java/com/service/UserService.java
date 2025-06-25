package com.service;

import com.database.entity.User;

import java.util.List;

public interface UserService {
    boolean registerUser(User user);
    User login(String usernameOrEmail, String password);
    User getUser(int id);
    boolean updateUser(User user);
    List<User> getSuggestedUsers(int userId);

}
