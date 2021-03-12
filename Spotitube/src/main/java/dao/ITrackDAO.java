package dao;

import java.util.ArrayList;

import domain.Track;

public interface ITrackDAO {
    ArrayList<Track> getTracks(String token, String forPlaylist );
    ArrayList<Track> getTracksFromPlaylist(String token, String playlist_id);

    ArrayList<Track> addTrackFromPlaylist( String token, int playlist_id, int track_id);
    ArrayList<Track> deleteTrackFromPlaylist( String token, int playlist_id, int track_id);
    
}
