package tests.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.IUserDAO;
import dao.UserDAO;
import domain.User;


public class UserDAOTest {

    private IUserDAO userDAO;
    private DataSource dataSource;

    private String usernameToTest = "henk";
    private String passwordToTest = "henk";

    @BeforeEach
    public void setup() {
        this.dataSource = mock(DataSource.class);
        this.userDAO = new UserDAO();
        this.userDAO.setDataSource(this.dataSource);
    }

    @Test
    public void loginUserTest() {
        try {
            // Arrange
            String expectedSQL = "UPDATE Users SET token = ? WHERE username = ? AND password = ?";

            // setup mocks
            Connection connection = mock(Connection.class);
            PreparedStatement preparedStatement = mock(PreparedStatement.class);

            // instruct mocks
            when(dataSource.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(expectedSQL)).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(1);
 
            // Act
            User user = userDAO.loginUser(usernameToTest, passwordToTest);

            // Assert
            verify(connection).prepareStatement(expectedSQL);
            verify(preparedStatement).setString(2, usernameToTest);
            verify(preparedStatement).setString(3, passwordToTest);
            verify(preparedStatement).executeUpdate();

            assertEquals(usernameToTest, user.getUsername());

        } catch ( Exception e ) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void loginUserNotFoundTest() {
        try {
            // Arrange
            String expectedSQL = "UPDATE Users SET token = ? WHERE username = ? AND password = ?";
            String usernameToTest = "notfound";

            // setup mocks
            Connection connection = mock(Connection.class);
            PreparedStatement preparedStatement = mock(PreparedStatement.class);

            // instruct mocks
            when(dataSource.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(expectedSQL)).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(0);
 
            // Act
            User user = userDAO.loginUser(usernameToTest, passwordToTest);

            // Assert
            verify(connection).prepareStatement(expectedSQL);
            verify(preparedStatement).setString(2, usernameToTest);
            verify(preparedStatement).setString(3, passwordToTest);
            verify(preparedStatement).executeUpdate();

            assertNull(user);

        } catch ( Exception e ) {
            fail();
        }
    }

    @Test
    public void createUserTestRegular() {
        try {
            String sql = "INSERT INTO Users (username, password, token) VALUES (?, ?, ?)";

            Connection connection = mock(Connection.class);
            PreparedStatement preparedStatement = mock(PreparedStatement.class);

            when(dataSource.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(1);

            // Act            
            User user = userDAO.createUser(usernameToTest, passwordToTest);

            // Assert
            verify(connection).prepareStatement(sql);
            verify(preparedStatement).setString(1, usernameToTest);
            verify(preparedStatement).setString(2, usernameToTest);
            verify(preparedStatement).executeUpdate();

            assertNotNull(user);

        } catch ( Exception e ) {
            fail();
        }
    }

    @Test
    public void createUserTestFailed() {
        try {
            String sql = "INSERT INTO Users (username, password, token) VALUES (?, ?, ?)";

            Connection connection = mock(Connection.class);
            PreparedStatement preparedStatement = mock(PreparedStatement.class);

            when(dataSource.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(0);

            // Act            
            User user = userDAO.createUser(usernameToTest, passwordToTest);

            // Assert
            verify(connection).prepareStatement(sql);
            verify(preparedStatement).setString(1, usernameToTest);
            verify(preparedStatement).setString(2, usernameToTest);
            verify(preparedStatement).executeUpdate();

            assertNull(user);

        } catch ( Exception e ) {
            fail();
        }
    }
}
