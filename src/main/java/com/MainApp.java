package com;
import com.controller.MatchController;
import com.database.repository.MatchRepository;
import com.database.repository.UserRepository;
import com.service.MatchService;
import com.service.MatchServiceImpl;
import com.service.UserService;
import com.service.UserServiceImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static Stage primaryStage;
    private static final UserRepository userRepository = new UserRepository();
    private static final MatchRepository matchRepository = new MatchRepository(userRepository);

    private static final UserService userService = new UserServiceImpl();
    private static final MatchService matchService = new MatchServiceImpl(matchRepository,userRepository);
    private static int loggedInUserId;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        showLogin();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void setLoggedInUserId(int id) {
        loggedInUserId = id;
    }

    public static int getLoggedInUserId() {
        return loggedInUserId;
    }

    public static void showLogin() {
        changeScene("/views/login.fxml");
    }

    public static void showMatchPage() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/views/match.fxml"));
            Parent root = loader.load();

            MatchController controller = loader.getController();
            controller.init(loggedInUserId, matchService, userService);

            primaryStage.setTitle("Find Friends");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void changeScene(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(MainApp.class.getResource(fxmlPath));
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void changeSceneSafe(String fxmlPath) {
        try {
            changeScene(fxmlPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void changeScene(String fxmlPath, int userId) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxmlPath));
            Parent root = loader.load();

            if ("/views/match.fxml".equals(fxmlPath)) {
                MatchController controller = loader.getController();
                controller.init(userId, matchService, userService);
                setLoggedInUserId(userId);
            }

            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
