package exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class MissingQueryParameterException extends Exception implements ExceptionMapper<MissingQueryParameterException> {

    public MissingQueryParameterException(String message) {
        super(message);
    }
 
    @Override
    public Response toResponse(MissingQueryParameterException e) {
        return Response.status(400).entity(e.getMessage()).build();
    }
    
}
