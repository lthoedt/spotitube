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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import dao.IPlaylistDAO;
import dao.ITrackDAO;
import domain.Track;
import service.dto.request.PlaylistReqDTO;
import service.dto.request.TrackReqDTO;
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
    public Response getPlaylist(@QueryParam("token") String token) {

        ArrayList<domain.Playlist> playlists = this.PlaylistDAO.getPlaylists(token);

        return this.buildPlaylistsDTO(playlists);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addPlaylist( @QueryParam("token") String token, PlaylistReqDTO playlistReqDTO ) {
        
        ArrayList<domain.Playlist> playlists = this.PlaylistDAO.addPlaylist(token, playlistReqDTO.name);
        
        return this.buildPlaylistsDTO(playlists);
    }

    @DELETE
    @Path("/{id}")
    public Response deletePlaylist(@Context UriInfo info, @PathParam("id") String playlist_id) {
        String token = info.getQueryParameters().getFirst("token");

        ArrayList<domain.Playlist> playlists = this.PlaylistDAO.deletePlaylist(token, playlist_id );

        return this.buildPlaylistsDTO(playlists);
    }

    @PUT
    @Path("/{id}")
    public Response editPlaylist(@Context UriInfo info, PlaylistDTO playlistReqDTO) {
        String token = info.getQueryParameters().getFirst("token");

        ArrayList<domain.Playlist> playlists = this.PlaylistDAO.editPlaylist(token, playlistReqDTO );

        return this.buildPlaylistsDTO(playlists);
    }

    @GET
    @Path("{id}/tracks")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTracks(@Context UriInfo info, @PathParam("id") String playlist_id) {
        String token = info.getQueryParameters().getFirst("token");

        ArrayList<Track> tracks = this.TrackDAO.getTracksFromPlaylist(token, playlist_id);

        return this.buildTracksDTO(tracks);
    }

    @POST
    @Path("/{id}/tracks")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addTrack(@Context UriInfo info, @PathParam("id") String playlist_id, TrackReqDTO trackReqDTO) {
        String token = info.getQueryParameters().getFirst("token");

        ArrayList<Track> tracks = this.TrackDAO.addTrackToPlaylist(token, playlist_id, trackReqDTO);

        return this.buildTracksDTO(tracks);
    }

    @DELETE
    @Path("/{id}/tracks/{track_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addTrack(@Context UriInfo info, @PathParam("id") String playlist_id, @PathParam("track_id") String track_id) {
        String token = info.getQueryParameters().getFirst("token");

        ArrayList<Track> tracks = this.TrackDAO.deleteTrackFromPlaylist(token, playlist_id, track_id);

        return this.buildTracksDTO(tracks);
    }

    private Response buildTracksDTO( ArrayList<Track> tracks ) {
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

    private Response buildPlaylistsDTO( ArrayList<domain.Playlist> playlists ) {
        // TODO
        // Error handling
        if ( playlists == null )
            return Response.status(404).build();

        PlaylistsDTO playlistsDTO = new PlaylistsDTO();
        ArrayList<PlaylistDTO> playlistDTOs = new ArrayList<>();

        for ( domain.Playlist playlist : playlists ) {
            playlistsDTO.length += playlist.getDuration();

            // Remove this if tracks should not be empty
            playlist.setTracks(new ArrayList<Track>());

            playlistDTOs.add(playlist.getDTO());

        }

        playlistsDTO.playlists = playlistDTOs;
        
        return Response.status(200).entity(playlistsDTO).build();
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
