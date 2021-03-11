package service;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import dao.IPlaylistDAO;

import service.dto.response.PlaylistDTO;
import service.dto.response.PlaylistsDTO;
import service.dto.response.TrackDTO;

@Path("playlists")
public class Playlist {

    private IPlaylistDAO PlaylistDAO;

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
            PlaylistDTO playlistDTO = new PlaylistDTO();
            playlistDTO.id = playlist.getId();
            playlistDTO.name = playlist.getName();
            playlistDTO.owner = playlist.getOwner();

            // they are empty but maybe thats not desired
            ArrayList<TrackDTO> trackDTOs = new ArrayList<>();
            playlistDTO.tracks = trackDTOs;

            playlistDTOs.add(playlistDTO);

            int length = playlist.getDuration();

            playlistsDTO.length += playlist.getDuration();
        }

        playlistsDTO.playlists = playlistDTOs;

        // TODO
        // calc the total length
        
        return Response.status(200).entity(playlistsDTO).build();
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

    @Inject
    public void setPlaylistDAO(IPlaylistDAO PlaylistDAO) {
        this.PlaylistDAO = PlaylistDAO;
    }
    
}
