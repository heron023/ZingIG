package com.controller;

import com.MainApp;
import com.database.entity.Gender;
import com.database.entity.Hobby;
import com.database.entity.User;
import com.service.UserService;
import com.service.UserServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegisterController {

    @FXML private TextField usernameField, nameField, lastnameField, emailField;
    @FXML private PasswordField passwordField;
    @FXML private DatePicker birthDatePicker;
    @FXML private ComboBox<Gender> genderCombo;
    @FXML private CheckBox hobbyMusic, hobbySports, hobbyBooks,hobbyGaming,hobbyCooking,hobbyTravel,hobbyPhotography,hobbyArt;
    @FXML private Label messageLabel;

    private final UserService userService = new UserServiceImpl();

    @FXML
    private void initialize() {
        genderCombo.getItems().addAll(Gender.MALE, Gender.FEMALE);
    }

    @FXML
    private void onRegister() {
        User user = new User();
        user.setUserName(usernameField.getText());
        user.setName(nameField.getText());
        user.setLastName(lastnameField.getText());
        user.setEmail(emailField.getText());
        user.setPassword(passwordField.getText());
        user.setBirthDate(birthDatePicker.getValue());
        user.setGender(genderCombo.getValue());

        if (hobbyMusic.isSelected()) user.getHobbies().add(new Hobby(1, "Music"));
        if (hobbySports.isSelected()) user.getHobbies().add(new Hobby(2, "Sports"));
        if (hobbyBooks.isSelected()) user.getHobbies().add(new Hobby(3, "Books"));
        if (hobbyGaming.isSelected()) user.getHobbies().add(new Hobby(4, "Gaming"));
        if (hobbyCooking.isSelected()) user.getHobbies().add(new Hobby(5, "Cooking"));
        if (hobbyTravel.isSelected()) user.getHobbies().add(new Hobby(6, "Travel"));
        if (hobbyPhotography.isSelected()) user.getHobbies().add(new Hobby(7, "Photography"));
        if (hobbyArt.isSelected()) user.getHobbies().add(new Hobby(8, "Art"));

        boolean success = userService.registerUser(user);

        if (success) {
            User loggedInUser = userService.login(user.getUserName(), user.getPassword());
            if (loggedInUser != null) {
                MainApp.setLoggedInUserId(loggedInUser.getId());
                MainApp.showMatchPage();
            } else {
                messageLabel.setText("Registered but failed to login.");
            }
        } else {
            messageLabel.setText("Registration failed.");
        }
    }

    @FXML
    private void switchToLogin() throws Exception {
        MainApp.changeScene("/views/login.fxml");
    }
}