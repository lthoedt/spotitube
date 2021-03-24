package exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UserNotFoundException extends Exception implements ExceptionMapper<UserNotFoundException> {
    /**
     *
     */
    private static final long serialVersionUID = -2187144747529383965L;

    public UserNotFoundException() {
        super("Username/password is wrong");
    }

    @Override
    public Response toResponse(UserNotFoundException e) {
        return Response.status(401).entity(e.getMessage()).build();
    }
}
