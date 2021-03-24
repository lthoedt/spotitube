package exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class InvalidTokenException extends Exception implements ExceptionMapper<InvalidTokenException> {

    /**
     *
     */
    private static final long serialVersionUID = 2262729783077192938L;

    public InvalidTokenException() {
        super("Token is invalid");
    }
 
    @Override
    public Response toResponse(InvalidTokenException e) {
        return Response.status(403).entity(e.getMessage()).build();
    }
    
}
