package com.database.repository;

import com.database.entity.Gender;
import com.database.entity.Hobby;
import com.database.entity.User;
import com.database.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    public boolean registerUser(User user) {
        String sqlUser = "INSERT INTO users (username, name, lastname, birthdate, email, password, gender) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlHobby = "INSERT INTO user_hobby (user_id, hobby_id) VALUES (?, ?)";

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            PreparedStatement userStmt = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);
            userStmt.setString(1, user.getUserName());
            userStmt.setString(2, user.getName());
            userStmt.setString(3, user.getLastName());
            userStmt.setDate(4, Date.valueOf(user.getBirthDate()));
            userStmt.setString(5, user.getEmail());
            userStmt.setString(6, user.getPassword());
            userStmt.setString(7, user.getGender().toString());

            userStmt.executeUpdate();
            ResultSet rs = userStmt.getGeneratedKeys();
            if (rs.next()) {
                int userId = rs.getInt(1);
                if (user.getHobbies() != null && !user.getHobbies().isEmpty()) {
                    PreparedStatement hobbyStmt = conn.prepareStatement(sqlHobby);
                    for (Hobby hobby : user.getHobbies()) {
                        hobbyStmt.setInt(1, userId);
                        hobbyStmt.setInt(2, hobby.getHobbyId());
                        hobbyStmt.addBatch();
                    }
                    hobbyStmt.executeBatch();
                }
                conn.commit();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public User login(String usernameOrEmail, String password) {
        String sql = "SELECT * FROM users WHERE (username = ? OR email = ?) AND password = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usernameOrEmail);
            stmt.setString(2, usernameOrEmail);
            stmt.setString(3, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
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
                user.setHobbies(loadUserHobbies(user.getId()));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUser(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
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
                user.setHobbies(loadUserHobbies(user.getId()));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE users SET name = ?, lastname = ?, birthdate = ?, email = ?, password = ?, gender = ? WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getLastName());
            stmt.setDate(3, Date.valueOf(user.getBirthDate()));
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getPassword());
            stmt.setString(6, user.getGender().toString());
            stmt.setInt(7, user.getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<User> findUsersWithSharedHobbies(int userId) {
        List<User> matches = new ArrayList<>();
        String sql = """
            SELECT DISTINCT u.* FROM users u
            JOIN user_hobby uh1 ON u.id = uh1.user_id
            JOIN user_hobby uh2 ON uh1.hobby_id = uh2.hobby_id
            WHERE uh2.user_id = ? AND u.id != ?
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, userId);

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
                user.setHobbies(loadUserHobbies(user.getId()));
                matches.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matches;
    }

    public List<Hobby> loadUserHobbies(int userId) {
        List<Hobby> hobbies = new ArrayList<>();
        String sql = "SELECT h.hobby_id, h.hobby_name FROM hobby h JOIN user_hobby uh ON h.hobby_id = uh.hobby_id WHERE uh.user_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                hobbies.add(new Hobby(rs.getInt("hobby_id"), rs.getString("hobby_name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hobbies;
    }
    public List<User> getSuggestedUsers(int userId) {
        List<User> suggested = new ArrayList<>();
        String sql = """
        SELECT DISTINCT u.* FROM users u
        JOIN user_hobby uh1 ON u.id = uh1.user_id
        WHERE uh1.hobby_id IN (
            SELECT hobby_id FROM user_hobby WHERE user_id = ?
        )
        AND u.id != ?
        AND u.id NOT IN (
            SELECT receiver_id FROM matches WHERE sender_id = ?
        )
    """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);

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
                user.setHobbies(loadUserHobbies(user.getId()));
                suggested.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suggested;
    }
}
