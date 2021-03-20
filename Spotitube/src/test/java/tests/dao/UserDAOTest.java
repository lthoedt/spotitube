package tests.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;

import dao.UserDAO;
import domain.User;


public class UserDAOTest {
    @Test
    public void loginUserTest() {
        try {
            // Arrange
            String expectedSQL = "SELECT username, token FROM Users WHERE username = ? AND password = ?";
            String usernameToTest = "henk";
            String passwordToTest = "henk";
            
            String tokenToExpect = "1425-2565-5487";

            // TODO wat doet dit?
            User userToExpect = new User();
            userToExpect.setUsername(usernameToTest);
            userToExpect.setToken(tokenToExpect);


            // setup mocks
            DataSource dataSource = mock(DataSource.class);
            Connection connection = mock(Connection.class);
            PreparedStatement preparedStatement = mock(PreparedStatement.class);
            ResultSet resultSet = mock(ResultSet.class);

            // instruct mocks
            when(dataSource.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(expectedSQL)).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true).thenReturn(false);
            when(resultSet.getString("username")).thenReturn(usernameToTest);
            when(resultSet.getString("token")).thenReturn(tokenToExpect);

            // setup classes
            UserDAO userDAO = new UserDAO();
            userDAO.setDataSource(dataSource);
 
            // Act
            User user = userDAO.loginUser(usernameToTest, passwordToTest);

            // Assert
            verify(connection).prepareStatement(expectedSQL);
            verify(preparedStatement).setString(1, usernameToTest);
            verify(preparedStatement).setString(2, passwordToTest);
            verify(preparedStatement).executeQuery();

            assertEquals(usernameToTest, user.getUsername());
            assertEquals(tokenToExpect, user.getToken());

        } catch ( Exception e ) {
            fail();
        }
    }

    @Test
    public void loginUserNotFoundTest() {
        try {
            // Arrange
            String expectedSQL = "SELECT username, token FROM Users WHERE username = ? AND password = ?";
            String usernameToTest = "notfound";
            String passwordToTest = "henk";

            // setup mocks
            DataSource dataSource = mock(DataSource.class);
            Connection connection = mock(Connection.class);
            PreparedStatement preparedStatement = mock(PreparedStatement.class);
            ResultSet resultSet = mock(ResultSet.class);

            // instruct mocks
            when(dataSource.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(expectedSQL)).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(false);

            // setup classes
            UserDAO userDAO = new UserDAO();
            userDAO.setDataSource(dataSource);
 
            // Act
            User user = userDAO.loginUser(usernameToTest, passwordToTest);

            // Assert
            verify(connection).prepareStatement(expectedSQL);
            verify(preparedStatement).setString(1, usernameToTest);
            verify(preparedStatement).setString(2, passwordToTest);
            verify(preparedStatement).executeQuery();

            assertNull(user);

        } catch ( Exception e ) {
            fail();
        }
    }
}
