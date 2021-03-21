package dao;

import java.util.ArrayList;

import domain.Track;

public interface ITrackDAO {
    ArrayList<Track> getTracks(String token, String forPlaylist );
    ArrayList<Track> getTracksFromPlaylist(String token, String playlist_id);

    ArrayList<Track> addTrackToPlaylist( String token, String playlist_id, String track_id, boolean track_offlineAvailable);
    ArrayList<Track> deleteTrackFromPlaylist( String token, String playlist_id, String track_id);
    public boolean ownsPlaylist( String token, String playlist_id );
}
