package com.service;

import com.database.entity.User;
import com.database.repository.MatchRepository;
import com.database.repository.UserRepository;
import com.service.MatchService;

import java.util.List;

public class MatchServiceImpl implements MatchService {
    private final MatchRepository matchRepository;
    private final UserRepository userRepository;

    public MatchServiceImpl(MatchRepository matchRepository, UserRepository userRepository) {
        this.matchRepository = matchRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void likeUser(int senderId, int receiverId) {
        matchRepository.likeUser(senderId, receiverId);
    }

    @Override
    public boolean isMutualLike(int id1, int id2) {
        return matchRepository.isMutualLike(id1, id2);
    }

    @Override
    public List<User> getMatchedUsers(int id) {
        List<Integer> matchedIds = matchRepository.getMatchedUserIds(id);
        return matchedIds.stream().map(userRepository::getUser).toList();
    }

    @Override
    public List<User> getUsersWhoLikedMe(int id) {
        return matchRepository.getUsersWhoLikedMe(id);
    }

    @Override
    public boolean canChat(int userId1, int userId2) {
        return matchRepository.isMutualLike(userId1, userId2);
    }
}
