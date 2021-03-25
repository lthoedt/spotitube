package tests.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.IUserDAO;
import domain.User;
import exceptions.UserNotFoundException;
import service.Login;
import service.dto.request.LoginReqDTO;
import service.dto.response.LoginDTO;

public class LoginTest {

    private LoginReqDTO loginReqDTO;
    private Login Login;
    private IUserDAO userDAOMock;

    @BeforeEach
    public void setup() {
        this.Login = new Login();

        this.loginReqDTO = new LoginReqDTO();
        this.loginReqDTO.user = "henk";
        this.loginReqDTO.password = "henk";

        this.userDAOMock = mock(IUserDAO.class);
        this.Login.setUserDAO(userDAOMock);

    }

    @Test
    public void loginTestRegular() {
        // Arrange
        int statuscodeExpected = 200;
        User user = new User();
        user.setUsername("henk");

        when(userDAOMock.loginUser(this.loginReqDTO.user, this.loginReqDTO.password)).thenReturn(user);
        
        Response response = null;
        try {
            // Act
            response = this.Login.login(this.loginReqDTO);
        } catch ( UserNotFoundException e ) {
            fail();
        }
        LoginDTO loginDTO = (LoginDTO) response.getEntity();

        // Assert
        assertEquals(statuscodeExpected, response.getStatus()); // status
        assertEquals(this.loginReqDTO.user, loginDTO.user); // username

    }

    @Test
    public void loginNotFoundTest() {
        // Arrange
        int statuscodeExpected = 401;

        when(userDAOMock.loginUser(this.loginReqDTO.user, this.loginReqDTO.password)).thenReturn(null);
        
        assertThrows(UserNotFoundException.class, () -> {
            Response response = this.Login.login(this.loginReqDTO);
            assertEquals(statuscodeExpected, response.getStatus()); // status
        });
    }
}
