package com.database.entity;

public class UserHobby {
    private int userId;
    private int hobbyId;

    public UserHobby(int userId, int hobbyId) {
        this.userId = userId;
        this.hobbyId = hobbyId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getHobbyId() {
        return hobbyId;
    }

    public void setHobbyId(int hobbyId) {
        this.hobbyId = hobbyId;
    }
}