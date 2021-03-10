package dao;

import java.util.ArrayList;

import domain.Track;

public interface ITrackDAO {
    ArrayList<Track> getTracks();
    ArrayList<Track> getTracksFromPlaylist(int playlist_id);

    ArrayList<Track> addTrackFromPlaylist(int playlist_id, int track_id);
    ArrayList<Track> deleteTrackFromPlaylist(int playlist_id, int track_id);
    
}
