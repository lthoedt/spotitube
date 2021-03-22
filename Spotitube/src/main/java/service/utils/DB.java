package service.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import domain.User;

public class DB {
    public static String genId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-";
        int length = 34;

        String code = "";

        for (int i = 0; i < length; i++) {
            int index = new Random().nextInt(chars.length());
            code += chars.charAt(index);
        }

        return code;
    }

    public static User getUser(Connection connection, String token) throws SQLException {
        String sql = "SELECT id, username FROM Users WHERE token = ?";

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, token);
        ResultSet resultSet = statement.executeQuery();

        while ( resultSet.next() ) {
            User user = new User();
            user.setId(resultSet.getInt("id"));
            user.setUsername(resultSet.getString("username"));
            return user;
        }

        return null;
    }
}
