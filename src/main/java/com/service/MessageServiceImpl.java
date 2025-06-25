package com.service;

import com.database.entity.Message;
import com.database.repository.MessageRepository;

import java.util.List;

public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public void saveMessage(Message message) {
        messageRepository.saveMessage(message);
    }

    @Override
    public List<Message> getMessagesBetween(int userId1, int userId2) {
        return messageRepository.getMessagesBetween(userId1, userId2);
    }
}