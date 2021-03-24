package exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class MissingQueryParameterException extends Exception implements ExceptionMapper<MissingQueryParameterException> {

    /**
     *
     */
    private static final long serialVersionUID = 584996056075860635L;

    public MissingQueryParameterException(String message) {
        super(message);
    }
 
    @Override
    public Response toResponse(MissingQueryParameterException e) {
        return Response.status(400).entity(e.getMessage()).build();
    }
    
}
