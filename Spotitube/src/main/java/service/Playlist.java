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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import dao.IPlaylistDAO;
import dao.ITrackDAO;
import domain.Track;
import exceptions.InvalidTokenException;
import exceptions.NotOwnerException;
import filters.TokenQueryValidator;

import service.dto.DTOMapper;
import service.dto.request.PlaylistReqDTO;
import service.dto.request.TrackReqDTO;
import service.dto.response.PlaylistDTO;

@Path("playlists")
public class Playlist {

    private IPlaylistDAO PlaylistDAO;
    private ITrackDAO TrackDAO;

    @GET
    @TokenQueryValidator
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlaylist(@QueryParam("token") String token) {

        ArrayList<domain.Playlist> playlists = this.PlaylistDAO.getPlaylists(token);

        return Response.status(200).entity( DTOMapper.buildPlaylistsDTO(playlists) ).build();
    }

    @POST
    @TokenQueryValidator
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addPlaylist( @QueryParam("token") String token, PlaylistReqDTO playlistReqDTO ) throws InvalidTokenException {

        ArrayList<domain.Playlist> playlists = this.PlaylistDAO.addPlaylist(token, playlistReqDTO.name);
        
        if (playlists == null)
            throw new InvalidTokenException();
        
        return Response.status(200).entity( DTOMapper.buildPlaylistsDTO(playlists) ).build();
    }

    @DELETE
    @TokenQueryValidator
    @Path("/{id}")
    public Response deletePlaylist( @QueryParam("token") String token, @PathParam("id") String playlist_id ) throws NotOwnerException {
        ArrayList<domain.Playlist> playlists = this.PlaylistDAO.deletePlaylist(token, playlist_id );

        if (playlists == null) 
            throw new NotOwnerException();

        return Response.status(200).entity( DTOMapper.buildPlaylistsDTO(playlists) ).build();
    }

    @PUT
    @TokenQueryValidator
    @Path("/{id}")
    public Response editPlaylist( @QueryParam("token") String token, PlaylistDTO playlistReqDTO) throws NotOwnerException {

        ArrayList<domain.Playlist> playlists = this.PlaylistDAO.editPlaylist(token, playlistReqDTO.id, playlistReqDTO.name);

        if (playlists == null) 
            throw new NotOwnerException();

        return Response.status(200).entity( DTOMapper.buildPlaylistsDTO(playlists) ).build();
    }

    @GET
    @TokenQueryValidator
    @Path("{id}/tracks")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTracks(@QueryParam("token") String token, @PathParam("id") String playlist_id) {

        ArrayList<Track> tracks = this.TrackDAO.getTracksFromPlaylist(token, playlist_id);

        return Response.status(200).entity(DTOMapper.buildTracksDTO(tracks)).build();
    }

    @POST
    @TokenQueryValidator
    @Path("/{id}/tracks")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addTrack(@QueryParam("token") String token, @PathParam("id") String playlist_id, TrackReqDTO trackReqDTO) throws NotOwnerException {

        ArrayList<Track> tracks = this.TrackDAO.addTrackToPlaylist(token, playlist_id, trackReqDTO.id, trackReqDTO.offlineAvailable);

        if (tracks == null) 
            throw new NotOwnerException();

        return Response.status(201).entity(DTOMapper.buildTracksDTO(tracks)).build();
    }

    @DELETE
    @TokenQueryValidator
    @Path("/{id}/tracks/{track_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTrack(@QueryParam("token") String token, @PathParam("id") String playlist_id, @PathParam("track_id") String track_id) throws NotOwnerException {

        ArrayList<Track> tracks = this.TrackDAO.deleteTrackFromPlaylist(token, playlist_id, track_id);

        if (tracks == null) 
            throw new NotOwnerException();

        return Response.status(200).entity(DTOMapper.buildTracksDTO(tracks)).build();
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
