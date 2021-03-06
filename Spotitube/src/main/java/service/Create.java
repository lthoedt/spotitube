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
import service.dto.request.LoginReqDTO;
import service.dto.response.LoginDTO;

@Path("create")
public class Create {

    private IUserDAO userDAO;
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create( LoginReqDTO loginDTORequest ) {

        // create user in db
        User user = this.userDAO.createUser(loginDTORequest.user, loginDTORequest.password);
        
        if (user == null) 
            return Response.status(500).build();
        
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
