package service;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import dao.ITrackDAO;
import domain.Track;
import service.dto.response.TrackDTO;
import service.dto.response.TracksDTO;

import java.util.ArrayList;

@Path("tracks")
public class Tracks {
    
    private ITrackDAO trackDAO;

    @GET
    /**
     * Receives all tracks that are available
     * @return
     */
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTracks(@Context UriInfo info) {
        String token = info.getQueryParameters().getFirst("token");
        String forPlaylist = info.getQueryParameters().getFirst("forPlaylist");

        ArrayList<Track> tracks = this.trackDAO.getTracks( token, forPlaylist );

        // TODO error handling
        if (tracks==null) {
            return Response.status(500).build();
        }

        TracksDTO tracksDTO = new TracksDTO();

        ArrayList<TrackDTO> trackDTOs = new ArrayList<>();

        for ( Track t : tracks ) {
            trackDTOs.add(t.getDTO());
        }

        tracksDTO.tracks = trackDTOs;
        
        return Response.status(200).entity(tracksDTO).build();
    }

    @Inject
    public void setUserDAO(ITrackDAO trackDAO) {
        this.trackDAO = trackDAO;
    }
}
