package com.service;

import com.database.entity.Message;

import java.util.List;

public interface MessageService {
    void saveMessage(Message message);
    List<Message> getMessagesBetween(int userId1, int userId2);
}