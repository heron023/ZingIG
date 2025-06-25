package com.service;

import com.database.entity.User;

import java.util.List;

public interface MatchService {

    void likeUser(int senderId, int receiverId);

    boolean isMutualLike(int userId1, int userId2);

    List<User> getMatchedUsers(int userId);

    List<User> getUsersWhoLikedMe(int userId);

    boolean canChat(int userId1, int userId2);
}
