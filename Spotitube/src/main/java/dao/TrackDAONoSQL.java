package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.json.*;

import domain.Album;
import domain.Track;
import domain.Video;
import service.utils.DB;

public class TrackDAONoSQL {

    // @Override
    // public ArrayList<Track> getTracks(String token, String forPlaylist) {
    //     try (Connection con = DB.getNoSQLURL()) {
    //         String queryRetrieve = "MATCH (ts:Tracks), (pl:Playlists) "
    //                                 + "OPTIONAL MATCH (album:Albums)-[:contains]->(ts) "
    //                                 + "WITH ts, pl, album "
    //                                 + "WHERE NOT (ts)-[:in]->(pl { id: ? }) "
    //                                 + "RETURN ts AS tracks, album AS album";

    //         PreparedStatement stmtRetrieve = con.prepareStatement(queryRetrieve);
    //         stmtRetrieve.setString(1, forPlaylist);
    //         ResultSet resultSet = stmtRetrieve.executeQuery();

    //         ArrayList<Track> tracks = new ArrayList<>();

    //         while (resultSet.next()) {
    //             String dataString = resultSet.getString("data");
    //             JSONObject dataJSON = new JSONObject(dataString);

    //             JSONObject trackJSON = dataJSON.getJSONObject("tracks");

    //             Track track = new Video( trackJSON.getString("id") );
    //             track.setAlbum( new Album(dataJSON.getString("album_name")) );
    //             track.setDescription( trackJSON.getString("description") );
    //             track.setDuration(trackJSON.getInt("duration"));
    //             track.setPerformer(trackJSON.getString("performer"));
    //             track.setPlaycount(trackJSON.getInt("playcount"));
    //             track.setPublicationDate(trackJSON.getString("publication_date"));
    //             track.setTitle(trackJSON.getString("title"));
    //             track.setUrl(trackJSON.getString("url"));

    //             tracks.add(track);
    //         }

    //         return tracks;

    //     } catch ( SQLException e ) {
    //         // TODO: errorhandling
    //         e.printStackTrace();
    //     }

    //     return null;
    // }

    // @Override
    // public ArrayList<Track> getTracksFromPlaylist(String token, String playlist_id) {
    //     // TODO Auto-generated method stub
    //     return null;
    // }

    // @Override
    // public ArrayList<Track> addTrackToPlaylist(String token, String playlist_id, String track_id,
    //         boolean track_offlineAvailable) {
    //     // TODO Auto-generated method stub
    //     return null;
    // }

    // @Override
    // public ArrayList<Track> deleteTrackFromPlaylist(String token, String playlist_id, String track_id) {
    //     // TODO Auto-generated method stub
    //     return null;
    // }

    // @Override
    // public boolean ownsPlaylist(String token, String playlist_id) {
    //     // TODO Auto-generated method stub
    //     return false;
    // }

    // @Override
    // public void setDataSource(DataSource dataSource) {
    //     // TODO Auto-generated method stub
        
    // }
    
}
