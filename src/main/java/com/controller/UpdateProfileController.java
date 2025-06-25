package com.controller;

import com.database.entity.Gender;
import com.database.entity.Hobby;
import com.database.entity.User;
import com.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UpdateProfileController {

    @FXML private TextField nameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private DatePicker birthDatePicker;
    @FXML private ComboBox<String> genderCombo;
    @FXML private TextField hobbiesField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    private UserService userService;
    private User currentUser;

    public void init(User user, UserService userService) {
        this.currentUser = user;
        this.userService = userService;

        genderCombo.getItems().addAll("MALE", "FEMALE");

        nameField.setText(user.getName());
        lastNameField.setText(user.getLastName());
        emailField.setText(user.getEmail());
        birthDatePicker.setValue(user.getBirthDate());

        if (user.getGender() != null) {
            genderCombo.setValue(user.getGender().name());
        }

        if (user.getHobbies() != null) {
            hobbiesField.setText(user.getHobbies().stream()
                    .map(Hobby::getHobbyName)
                    .collect(Collectors.joining(", ")));
        }

        passwordField.setPromptText("Leave empty to keep current");
    }

    @FXML
    private void onUpdate() {
        if (nameField.getText().trim().isEmpty() ||
                lastNameField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty() ||
                birthDatePicker.getValue() == null ||
                genderCombo.getValue() == null) {

            messageLabel.setText("Please fill all required fields.");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        currentUser.setName(nameField.getText().trim());
        currentUser.setLastName(lastNameField.getText().trim());
        currentUser.setEmail(emailField.getText().trim());
        currentUser.setBirthDate(birthDatePicker.getValue());

        try {
            currentUser.setGender(Gender.valueOf(genderCombo.getValue()));
        } catch (IllegalArgumentException e) {
            messageLabel.setText("Invalid gender selected.");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        String passwordInput = passwordField.getText().trim();

        if (!passwordInput.isEmpty()) {
            currentUser.setPassword(passwordInput);
        } else {
            User dbUser = userService.getUser(currentUser.getId());
            if (dbUser != null && dbUser.getPassword() != null) {
                currentUser.setPassword(dbUser.getPassword());
            } else {
                messageLabel.setText("Cannot update without existing password.");
                messageLabel.setStyle("-fx-text-fill: red;");
                return;
            }
        }
        String[] hobbyNames = hobbiesField.getText().split(",");
        List<Hobby> hobbyList = new ArrayList<>();
        int hobbyIdCounter = 1;
        for (String h : hobbyNames) {
            String hobbyName = h.trim();
            if (!hobbyName.isEmpty()) {
                hobbyList.add(new Hobby(hobbyIdCounter++, hobbyName));
            }
        }
        currentUser.setHobbies(hobbyList);

        boolean success = userService.updateUser(currentUser);

        if (success) {
            messageLabel.setText("Profile updated successfully!");
            messageLabel.setStyle("-fx-text-fill: lightgreen;");
        } else {
            messageLabel.setText("Failed to update profile.");
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void onCancel() {
        messageLabel.getScene().getWindow().hide();
    }
}