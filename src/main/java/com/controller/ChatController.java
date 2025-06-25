package com.controller;

import com.database.entity.Message;
import com.database.entity.User;
import com.service.MessageService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChatController {

    @FXML private VBox messageListVBox;
    @FXML private TextArea messageInput;
    @FXML private ScrollPane messageScrollPane;

    private int loggedInUserId;
    private User chatPartner;
    private MessageService messageService;

    private final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

    public void init(int loggedInUserId, User chatPartner, MessageService messageService) {
        this.loggedInUserId = loggedInUserId;
        this.chatPartner    = chatPartner;
        this.messageService = messageService;
        loadMessages();
    }

    private void loadMessages() {
        messageListVBox.getChildren().clear();

        List<Message> msgs = messageService.getMessagesBetween(loggedInUserId, chatPartner.getId());

        for (Message m : msgs) {
            boolean me = m.getSenderId() == loggedInUserId;

            Text body = new Text(m.getMessage());
            body.setFill(me ? Color.BLACK : Color.WHITE);

            Text time = new Text(" " + m.getTimestamp().format(timeFmt));
            time.setFill(Color.GRAY);
            time.setStyle("-fx-font-size: 9px;");

            TextFlow bubble = new TextFlow(body, time);
            bubble.setMaxWidth(300);
            bubble.setStyle(me
                    ? "-fx-background-color:#00BFFF;-fx-padding:6;-fx-background-radius:10;"
                    : "-fx-background-color:#444;-fx-padding:6;-fx-background-radius:10;");

            VBox wrapper = new VBox(bubble);
            wrapper.setMaxWidth(320);
            wrapper.setStyle(me
                    ? "-fx-alignment:center-right;-fx-padding:4;"
                    : "-fx-alignment:center-left;-fx-padding:4;");

            messageListVBox.getChildren().add(wrapper);
        }

        Platform.runLater(() -> messageScrollPane.setVvalue(1.0));
    }

    @FXML
    private void onSendMessage() {
        String txt = messageInput.getText().trim();
        if (txt.isEmpty()) return;

        Message msg = new Message(0, loggedInUserId, chatPartner.getId(), txt, null);
        messageService.saveMessage(msg);

        messageInput.clear();
        loadMessages();
    }
}