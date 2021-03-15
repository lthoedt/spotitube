package dao;

import java.util.ArrayList;

import domain.Track;
import service.dto.request.TrackReqDTO;

public interface ITrackDAO {
    ArrayList<Track> getTracks(String token, String forPlaylist );
    ArrayList<Track> getTracksFromPlaylist(String token, String playlist_id);

    ArrayList<Track> addTrackToPlaylist( String token, String playlist_id, TrackReqDTO trackReqDTO);
    ArrayList<Track> deleteTrackFromPlaylist( String token, String playlist_id, String track_id);
    
}
