package exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UserNotFoundException extends Exception implements ExceptionMapper<UserNotFoundException> {
    public UserNotFoundException() {
        super("Username/password is wrong");
    }

    @Override
    public Response toResponse(UserNotFoundException e) {
        return Response.status(401).entity(e.getMessage()).build();
    }
}
