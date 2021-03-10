import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.IUserDAO;
import domain.User;
import service.Login;
import service.dto.request.LoginReqDTO;
import service.dto.response.LoginDTO;

public class LoginTest {

    private LoginReqDTO loginReqDTO;
    private Login Login;

    @BeforeEach
    public void setup() {
        this.Login = new Login();

        this.loginReqDTO = new LoginReqDTO();
        this.loginReqDTO.user = "henk";
        this.loginReqDTO.password = "henk";
    }

    @Test
    public void loginTestRegular() {
        // Arrange
        int statuscodeExpected = 200;
        User user = new User();
        user.setUsername("henk");

        IUserDAO loginDAOMock = mock(IUserDAO.class);
        when(loginDAOMock.loginUser(this.loginReqDTO.user, this.loginReqDTO.password)).thenReturn(user);
        this.Login.setUserDAO(loginDAOMock);
        
        // Act
        Response response = this.Login.login(this.loginReqDTO);
        LoginDTO loginDTO = (LoginDTO) response.getEntity();

        // Assert
        assertEquals(statuscodeExpected, response.getStatus()); // status
        assertEquals(this.loginReqDTO.user, loginDTO.user); // username

    }

    @Test
    public void loginNotFoundTest() {
        // Arrange
        int statuscodeExpected = 404;

        IUserDAO loginDAOMock = mock(IUserDAO.class);
        when(loginDAOMock.loginUser(this.loginReqDTO.user, this.loginReqDTO.password)).thenReturn(null);
        this.Login.setUserDAO(loginDAOMock);
        
        // Act
        Response response = this.Login.login(this.loginReqDTO);

        // Assert
        assertEquals(statuscodeExpected, response.getStatus()); // status

    }
}
