package service;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import dao.IUserDAO;
import domain.User;
import exceptions.UserNotFoundException;
import service.dto.request.LoginReqDTO;
import service.dto.response.LoginDTO;

@Path("login")
public class Login {

    private IUserDAO userDAO;
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login( LoginReqDTO loginDTORequest ) throws UserNotFoundException {
        // create user in db
        User user = this.userDAO.loginUser(loginDTORequest.user, loginDTORequest.password);

        if (user == null) 
            throw new UserNotFoundException();
        
        // make dto
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.user = user.getUsername(); 
        loginDTO.token = user.getToken();

        return Response.status(200).entity(loginDTO).build();
    }

    @Inject
    public void setUserDAO(IUserDAO userDAO) {
        this.userDAO = userDAO;
    }

}
