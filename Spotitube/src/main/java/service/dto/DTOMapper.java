package service.dto;

import java.util.ArrayList;

import domain.Playlist;
import domain.Track;
import service.dto.response.PlaylistDTO;
import service.dto.response.PlaylistsDTO;
import service.dto.response.TrackDTO;
import service.dto.response.TracksDTO;

public class DTOMapper {

    public static PlaylistDTO buildPlaylistDTO( Playlist playlist ) {
        PlaylistDTO playlistDTO = new PlaylistDTO();
        playlistDTO.id = playlist.getId();
        playlistDTO.name = playlist.getName();
        playlistDTO.owner = playlist.getOwner();

        ArrayList<TrackDTO> trackDTOs = new ArrayList<>();
        for ( Track t : playlist.getTracks() ) {
            trackDTOs.add(DTOMapper.buildTrackDTO( t ));
        }
        playlistDTO.tracks = trackDTOs;

        return playlistDTO;
    }

    public static PlaylistsDTO buildPlaylistsDTO( ArrayList<Playlist> playlists ) {
        PlaylistsDTO playlistsDTO = new PlaylistsDTO();
        ArrayList<PlaylistDTO> playlistDTOs = new ArrayList<>();

        for ( Playlist playlist : playlists ) {
            playlistsDTO.length += playlist.duration;

            playlist.setTracks(new ArrayList<Track>());

            playlistDTOs.add(buildPlaylistDTO( playlist ));
        }

        playlistsDTO.playlists = playlistDTOs;
        
        return playlistsDTO;
    }

    public static TrackDTO buildTrackDTO( Track track ) {
        TrackDTO trackDTO = new TrackDTO();
        trackDTO.id = track.getId();
        trackDTO.title = track.getTitle();
        trackDTO.performer = track.getPerformer();
        trackDTO.duration = track.getDuration();
        trackDTO.playcount = track.getPlaycount();
        trackDTO.publicationDate = track.getPublicationDate();
        trackDTO.description = track.getDescription();
        trackDTO.offlineAvailable = track.getOfflineAvailable();
        
        try {
            trackDTO.album = track.getAlbum().getName();
        } catch ( NullPointerException e) {
            trackDTO.album = null;
        }
        return trackDTO;
    }

    public static TracksDTO buildTracksDTO( ArrayList<Track> tracks ) {
        TracksDTO tracksDTO = new TracksDTO();

        ArrayList<TrackDTO> trackDTOs = new ArrayList<>();

        for ( Track t : tracks ) {
            trackDTOs.add(buildTrackDTO( t ));
        }

        tracksDTO.tracks = trackDTOs;
        
        return tracksDTO;
    }
}
