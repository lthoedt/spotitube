package tests.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.IUserDAO;
import domain.User;
import service.Create;
import service.dto.request.LoginReqDTO;
import service.dto.response.LoginDTO;

public class CreateTest {
    private Create create;
    private LoginReqDTO loginReqDTO;

    @BeforeEach
    public void setup() {
        this.create = new Create();

        this.loginReqDTO = new LoginReqDTO();
        this.loginReqDTO.user = "test";
        this.loginReqDTO.password = "test";
    }

    @Test
    public void CreateTestRegular() {
        // Arrange
        int statusCodeExpected = 200;
        User user = new User();
        user.setUsername(this.loginReqDTO.user);
        user.setToken("1");
        
        IUserDAO loginDAOMock = mock(IUserDAO.class);
        when(loginDAOMock.createUser(this.loginReqDTO.user, this.loginReqDTO.password)).thenReturn(user);
        this.create.setUserDAO(loginDAOMock);
        
        // Act
        Response response = this.create.create(this.loginReqDTO);
        LoginDTO loginDTO = (LoginDTO) response.getEntity();

        // Assert
        assertEquals(statusCodeExpected, response.getStatus()); // status
        assertEquals(this.loginReqDTO.user, loginDTO.user); // username
    }


    @Test
    public void CreateTestFailed() {
        // Arrange
        int statusCodeExpected = 500;
        User user = new User();
        user.setUsername(this.loginReqDTO.user);
        user.setToken("1");
        
        IUserDAO UserDAOMock = mock(IUserDAO.class);
        when(UserDAOMock.createUser(this.loginReqDTO.user, this.loginReqDTO.password)).thenReturn(null);
        this.create.setUserDAO(UserDAOMock);
        
        // Act
        Response response = this.create.create(this.loginReqDTO);

        // Assert
        assertEquals(statusCodeExpected, response.getStatus()); // status
    }
}
