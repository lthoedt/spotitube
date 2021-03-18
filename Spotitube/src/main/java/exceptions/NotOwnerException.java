package exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class NotOwnerException extends Exception implements ExceptionMapper<NotOwnerException> {

    public NotOwnerException() {
        super("You do not own this playlist");
    }

    @Override
    public Response toResponse(NotOwnerException e) {
        return Response.status(403).entity(e.getMessage()).build();
    }
    
}
