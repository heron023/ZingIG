package com.controller;

import com.MainApp;
import com.database.entity.User;
import com.database.repository.MessageRepository;
import com.service.MatchService;
import com.service.MessageServiceImpl;
import com.service.UserService;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class MatchController {

    @FXML private VBox userListVBox;
    @FXML private StackPane mainStack;
    @FXML private VBox slidePanel;
    @FXML private Label fullNameLabel, emailLabel, birthdateLabel, genderLabel, hobbiesLabel;
    @FXML private Label titleLabel;
    @FXML private Button logoutButton, whoLikedButton, backToSuggestionsButton, editProfileButton, matchedUsersButton;

    private int loggedInUserId;
    private MatchService matchService;
    private UserService userService;

    public void init(int userId, MatchService matchService, UserService userService) {
        this.loggedInUserId = userId;
        this.matchService = matchService;
        this.userService = userService;

        backToSuggestionsButton.setVisible(false);
        onShowSuggestions();
    }

    @FXML
    public void onLogout() {
        MainApp.changeSceneSafe("/views/login.fxml");
    }

    @FXML
    public void onEditProfile() {
        try {
            FXMLLoader fx = new FXMLLoader(getClass().getResource("/views/updateProfile.fxml"));
            Parent root = fx.load();
            UpdateProfileController c = fx.getController();
            c.init(userService.getUser(loggedInUserId), userService);

            Stage s = new Stage();
            s.setTitle("Update Profile");
            s.setScene(new Scene(root));
            s.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void onWhoLiked() {
        titleLabel.setText("Users Who Liked Me");
        backToSuggestionsButton.setVisible(true);
        populateList(matchService.getUsersWhoLikedMe(loggedInUserId), true);
    }

    @FXML
    public void onViewMatches() {
        titleLabel.setText("Your Matches");
        backToSuggestionsButton.setVisible(true);
        populateList(matchService.getMatchedUsers(loggedInUserId), false);
    }

    @FXML
    private void onShowSuggestions() {
        titleLabel.setText("Suggested Users");
        backToSuggestionsButton.setVisible(false);
        populateList(userService.getSuggestedUsers(loggedInUserId), true);
    }

    private void populateList(List<User> users, boolean showLikeButton) {
        userListVBox.getChildren().clear();

        for (User user : users) {
            Label name = new Label(user.getName() + " " + user.getLastName());
            name.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

            Button actionButton = null;
            if (showLikeButton) {
                actionButton = new Button("Like ❤️");
                actionButton.setStyle("-fx-background-color: #00BFFF; -fx-text-fill: black;");

                if (matchService.isMutualLike(loggedInUserId, user.getId())) {
                    actionButton.setText("💬 Matched!");
                    actionButton.setDisable(true);
                }

                Button finalBtn = actionButton;
                actionButton.setOnAction(e -> {
                    matchService.likeUser(loggedInUserId, user.getId());
                    finalBtn.setDisable(true);
                    if (matchService.isMutualLike(loggedInUserId, user.getId())) {
                        finalBtn.setText("💬 Matched!");
                    } else {
                        finalBtn.setText("Liked ✓");
                    }
                });
            }

            Button viewBtn = new Button("View Info");
            viewBtn.setStyle("-fx-background-color: #444; -fx-text-fill: lightblue;");
            viewBtn.setOnAction(e -> showUserDetails(user.getId()));

            VBox card;
            if (showLikeButton) {
                card = new VBox(5, name, actionButton, viewBtn);
            } else {
                Button chatBtn = new Button("Chat 💬");
                chatBtn.setStyle("-fx-background-color: #00FFAA; -fx-text-fill: black;");
                chatBtn.setOnAction(e -> openChat(user));
                card = new VBox(5, name, viewBtn, chatBtn);
            }

            card.setStyle("""
                -fx-padding: 10;
                -fx-background-color: #222;
                -fx-spacing: 5;
                -fx-border-color: #00BFFF;
                -fx-border-radius: 10;
                -fx-background-radius: 10;
            """);

            userListVBox.getChildren().add(card);
        }
    }

    private void showUserDetails(int id) {
        User u = userService.getUser(id);
        if (u == null) return;

        fullNameLabel.setText("Name: " + u.getName() + " " + u.getLastName());
        emailLabel.setText("Email: " + u.getEmail());
        birthdateLabel.setText("Birthdate: " + u.getBirthDate());
        genderLabel.setText("Gender: " + u.getGender());

        String hobbies = u.getHobbies() == null || u.getHobbies().isEmpty()
                ? "None"
                : String.join(", ", u.getHobbies().stream().map(h -> h.getHobbyName()).toList());
        hobbiesLabel.setText("Hobbies: " + hobbies);

        if (!slidePanel.isVisible()) {
            slidePanel.setVisible(true);
            TranslateTransition tt = new TranslateTransition(Duration.millis(300), slidePanel);
            tt.setFromX(600);
            tt.setToX(0);
            tt.play();
        }
    }

    @FXML
    private void closeSlidePanel() {
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), slidePanel);
        tt.setFromX(0);
        tt.setToX(600);
        tt.setOnFinished(e -> slidePanel.setVisible(false));
        tt.play();
    }

    @FXML
    private void openChat(User chatPartner) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/chat.fxml"));
            Parent root = loader.load();
            ChatController controller = loader.getController();
            controller.init(loggedInUserId, chatPartner, new MessageServiceImpl(new MessageRepository()));
            Stage stage = new Stage();
            stage.setTitle("Chat with " + chatPartner.getName());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

