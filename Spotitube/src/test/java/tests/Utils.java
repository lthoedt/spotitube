package tests;

import java.util.ArrayList;

import domain.Playlist;
import domain.Track;
import domain.Video;
import service.dto.request.PlaylistReqDTO;
import service.dto.request.TrackReqDTO;
import service.dto.response.PlaylistDTO;
import service.dto.response.TrackDTO;

public final class Utils {
    public static Track getSampleTrack() {
        Track track = new Video("LaaDzWjBjiVi8krXYTLW8b8iuW6wW4HX5u");
        track.setPerformer("Stevie Ray Vaughan And Double Trouble");
        track.setTitle("Texas Flood (from Live at the El Mocambo");
        track.setUrl("");
        track.setDuration(123);
        track.setOfflineAvailable(true);
        track.setPublicationDate("2021-03-15 11:04:11");
        track.setDescription("");
        track.setPlaycount(7585665);
        return track;
    }

    public static Playlist getSamplePlaylist() {
        Playlist playlist = new Playlist();

        ArrayList<Track> tracks = new ArrayList<>();
        tracks.add(getSampleTrack());

        playlist.setId("RtUtzbPwzN1rds0qEGtSvsmcvtIT3Rpxg0");
        playlist.setName("SRV");
        playlist.setOwner(true);
        playlist.setTracks(tracks);
        
        return playlist;
    }

    public static PlaylistReqDTO getSamplePlaylistReqDTO() {
        PlaylistReqDTO playlistReqDTO = new PlaylistReqDTO();
        playlistReqDTO.id = -1;
        playlistReqDTO.name = "Progressive Rock";
        playlistReqDTO.owner = false;
        playlistReqDTO.tracks = new TrackDTO[0];
        return playlistReqDTO;
    }

    public static PlaylistDTO getSamplePlaylistDTO(String name) {
        PlaylistDTO playlistDTO = new PlaylistDTO();
        playlistDTO.id = "5NOVKXx5r_66y42IIK61th-PT9hU6C4hts";
        playlistDTO.name = name;
        playlistDTO.owner = false;
        playlistDTO.tracks = new ArrayList<>();
        return playlistDTO;
    }

    public static TrackReqDTO getSampleTrackReqDTO() {
        TrackReqDTO trackReqDTO = new TrackReqDTO();
        trackReqDTO.id = "1";
        trackReqDTO.title = "title";
        trackReqDTO.performer = "performer";
        trackReqDTO.duration = 123;
        trackReqDTO.album = "albumName";
        trackReqDTO.offlineAvailable = true;
        return trackReqDTO;
    }

    public static Playlist convertPlaylistDTOToPlaylist( PlaylistReqDTO playlistReqDTO ) {
        Playlist playlist = new Playlist();

        ArrayList<Track> tracks = new ArrayList<>();

        playlist.setId("RtUtzbPwzN1rds0qEGaSvsmcvtIT3Rpxg0");
        playlist.setName(playlistReqDTO.name);
        playlist.setOwner(true);
        playlist.setTracks(tracks);
        
        return playlist;
    }
}
