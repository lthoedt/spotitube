package dao;

import javax.annotation.Resource;
import javax.enterprise.inject.Default;
import javax.sql.DataSource;

import java.sql.*;
import java.util.Random;

import domain.User;

@Default
public class UserDAO implements IUserDAO {
    
    @Resource(name = "jdbc/spotitube")
    DataSource dataSource;

    @Override
    public User createUser(String username, String password) {
        String sql = "INSERT INTO Users (username, password, token) VALUES (?, ?, ?)";
        String token = generateToken();

        try {
            Connection connection = this.dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, token);
            if (statement.executeUpdate() != 1) return null;

            User user = new User();
            user.setUsername(username);
            user.setToken(token);
            return user;
            
        } catch (Exception e) {
            System.out.println(e);
        }
        
        return null;
    }

    @Override
    public User loginUser(String username, String password) {
        String sql = "UPDATE Users SET token = ? WHERE username = ? AND password = ?";

        String token = generateToken();
        
        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, token);
            statement.setString(2, username);
            statement.setString(3, password);

            if ( statement.executeUpdate() != 1 ) return null;
            
            User user = new User();
            user.setUsername(username);
            user.setToken(token);
            return user;
        } catch ( SQLException e ) {
            e.printStackTrace();
        }

        return null;
    }

    // public User getUser( String token ) {
    //     String sql = "SELECT * FROM Users WHERE token=?";

    //     try (Connection connection = this.dataSource.getConnection()) {
    //         PreparedStatement statement = connection.prepareStatement(sql);
    //         statement.setString(1, token);

    //         ResultSet resultSet = statement.executeQuery();

    //         while( resultSet.next() ) {
    //             User user = new User();
    //             user.setUsername(resultSet.getString("username"));
    //             user.setToken(resultSet.getString("token"));
    //             return user;
    //         }
    //     } catch ( SQLException e ) {
    //         e.printStackTrace();
    //     }

    //     return null;
    // }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String generateToken() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";
        String token = "";
        for ( int g = 0; g < 3; g++ ) {
            if ( g != 0 ) token += "-";
            for ( int c = 0; c < 4; c++ ) {
                token += chars.charAt(new Random().nextInt(chars.length()));
            }
        }

        return token;
    }

}
