package com.database.repository;

import com.database.Database;
import com.database.entity.Gender;
import com.database.entity.Hobby;
import com.database.entity.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatchRepository {

    private final UserRepository userRepository;

    public MatchRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void likeUser(int senderId, int receiverId) {
        String sql = "INSERT IGNORE INTO matches (sender_id, receiver_id) VALUES (?, ?)";
        if (senderId == receiverId) return;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isMutualLike(int userId1, int userId2) {
        String sql = "SELECT 1 FROM matches WHERE sender_id = ? AND receiver_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt1 = conn.prepareStatement(sql);
             PreparedStatement stmt2 = conn.prepareStatement(sql)) {

            stmt1.setInt(1, userId1);
            stmt1.setInt(2, userId2);

            stmt2.setInt(1, userId2);
            stmt2.setInt(2, userId1);

            ResultSet rs1 = stmt1.executeQuery();
            ResultSet rs2 = stmt2.executeQuery();

            return rs1.next() && rs2.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Integer> getMatchedUserIds(int userId) {
        List<Integer> ids = new ArrayList<>();
        String sql = """
            SELECT m1.receiver_id FROM matches m1
            JOIN matches m2 ON m1.receiver_id = m2.sender_id AND m1.sender_id = m2.receiver_id
            WHERE m1.sender_id = ?
        """;
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ids.add(rs.getInt("receiver_id"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }

    public List<User> getUsersWhoLikedMe(int userId) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.* FROM users u JOIN matches m ON u.id = m.sender_id WHERE m.receiver_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("name"),
                        rs.getString("lastname"),
                        rs.getDate("birthdate").toLocalDate(),
                        rs.getString("email"),
                        null,
                        Gender.valueOf(rs.getString("gender"))
                );
                user.setHobbies(userRepository.loadUserHobbies(user.getId()));

                list.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}