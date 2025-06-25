package com.service;

import com.database.entity.User;
import com.database.repository.UserRepository;

import java.util.List;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository = new UserRepository();

    @Override
    public boolean registerUser(User user) {
        return userRepository.registerUser(user);
    }

    @Override
    public User login(String usernameOrEmail, String password) {
        return userRepository.login(usernameOrEmail, password);
    }

    @Override
    public User getUser(int id) {
        return userRepository.getUser(id);
    }

    @Override
    public boolean updateUser(User user) {
        return userRepository.updateUser(user);

    }
    @Override
    public List<User> getSuggestedUsers(int userId) {
        return userRepository.getSuggestedUsers(userId);
    }
}

