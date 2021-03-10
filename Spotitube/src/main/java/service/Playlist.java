package service;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("playlist")
public class Playlist {

    @GET
    public Response getPlaylist() {
        return null;
    }

    @POST
    public Response addPlaylist() {
        return null;
    }

    @DELETE
    public Response deletePlaylist() {
        return null;
    }

    @PUT
    public Response editPlaylist() {
        return null;
    }
    
}
