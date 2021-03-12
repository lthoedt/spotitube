package service;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import dao.IPlaylistDAO;
import dao.ITrackDAO;
import domain.Track;
import service.dto.request.PlaylistReqDTO;
import service.dto.response.PlaylistDTO;
import service.dto.response.PlaylistsDTO;
import service.dto.response.TrackDTO;
import service.dto.response.TracksDTO;

@Path("playlists")
public class Playlist {

    private IPlaylistDAO PlaylistDAO;
    private ITrackDAO TrackDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlaylist(@Context UriInfo info) {
        String token = info.getQueryParameters().getFirst("token");

        ArrayList<domain.Playlist> playlists = this.PlaylistDAO.getPlaylists(token);

        // TODO
        // Error handling
        if ( playlists == null )
            return Response.status(404).build();

        PlaylistsDTO playlistsDTO = new PlaylistsDTO();
        ArrayList<PlaylistDTO> playlistDTOs = new ArrayList<>();

        for ( domain.Playlist playlist : playlists ) {
            playlistDTOs.add(playlist.getDTO());

            int length = playlist.getDuration();

            playlistsDTO.length += playlist.getDuration();
        }

        playlistsDTO.playlists = playlistDTOs;

        // TODO
        // calc the total length
        
        return Response.status(200).entity(playlistsDTO).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addPlaylist( @Context UriInfo info, PlaylistReqDTO playlistReqDTO ) {
        String token = info.getQueryParameters().getFirst("token");
        

        return null;
    }

    @DELETE
    public Response deletePlaylist(@Context UriInfo info) {
        String token = info.getQueryParameters().getFirst("token");

        return null;
    }

    @PUT
    public Response editPlaylist(@Context UriInfo info) {
        String token = info.getQueryParameters().getFirst("token");

        return null;
    }

    @GET
    @Path("{id}/tracks")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTracks(@Context UriInfo info, @PathParam("id") String playlist_id) {
        String token = info.getQueryParameters().getFirst("token");

        ArrayList<Track> tracks = this.TrackDAO.getTracksFromPlaylist(token, playlist_id);

        if ( tracks == null ) {
            // TODO
            return Response.status(404).build();
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
    public void setPlaylistDAO(IPlaylistDAO PlaylistDAO) {
        this.PlaylistDAO = PlaylistDAO;
    }

    @Inject
    public void setTrackDAO(ITrackDAO TrackDAO) {
        this.TrackDAO = TrackDAO;
    }
    
}
