package exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class PlaylistNotFoundException extends Exception implements ExceptionMapper<PlaylistNotFoundException> {
    public PlaylistNotFoundException() {
        super("Playlist not found");
    }

    @Override
    public Response toResponse(PlaylistNotFoundException e) {
        return Response.status(404).entity(e.getMessage()).build();
    }
}
