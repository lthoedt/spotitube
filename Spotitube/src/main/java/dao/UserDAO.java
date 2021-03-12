package dao;

import javax.annotation.Resource;
import javax.enterprise.inject.Default;
import javax.sql.DataSource;

import java.nio.charset.Charset;
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
            ResultSet resultSet = statement.executeQuery();
        
            return null;
            
        } catch (Exception e) {
            System.out.println(e);
        }
        
        return null;
    }

    @Override
    public User loginUser(String username, String password) {
        String sql = "SELECT username, token FROM Users WHERE username = ? AND password = ?";
        
        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();

            while ( resultSet.next() ) {
                User user = new User();
                user.setUsername(resultSet.getString("username"));
                user.setToken(resultSet.getString("token"));
                return user;
            }

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
        byte[] array = new byte[15]; // length is bounded by 7
        new Random().nextBytes(array);
        return new String(array, Charset.forName("UTF-8"));    
    }

}
