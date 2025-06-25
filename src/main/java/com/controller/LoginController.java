package com.controller;

import com.MainApp;
import com.database.entity.User;
import com.service.UserService;
import com.service.UserServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    private final UserService userService = new UserServiceImpl();

    @FXML
    private void onLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter username and password.");
            return;
        }

        User user = userService.login(username, password);

        if (user != null) {
            messageLabel.setText("Login successful!");

            try {
                MainApp.setLoggedInUserId(user.getId());
                MainApp.showMatchPage();
            } catch (Exception e) {
                e.printStackTrace();
                messageLabel.setText("Failed to open main screen.");
            }

        } else {
            messageLabel.setText("Invalid username or password.");
        }
    }

    @FXML
    private void switchToRegister() {
        try {
            MainApp.changeScene("/views/register.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Failed to open register screen.");
        }
    }
}