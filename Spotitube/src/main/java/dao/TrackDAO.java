package dao;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.json.*;

import java.sql.*;
import java.util.ArrayList;

import domain.Album;
import domain.Song;
import domain.Track;
import domain.Video;
import service.utils.DB;

public class TrackDAO implements ITrackDAO {

    @Resource(name = "jdbc/spotitube")
    DataSource dataSource;

    private String sqlFields = "SELECT TrackMappers.offline_available, Tracks.id, Tracks.performer, Tracks.title, Tracks.url, Tracks.duration";
    private String sqlJoins = "LEFT JOIN TrackMappers ON TrackMappers.track_id=Tracks.id "
                            + "LEFT JOIN PlaylistMappers ON PlaylistMappers.playlist_id=TrackMappers.playlist_id "
                            + "LEFT JOIN Users ON Users.id=PlaylistMappers.user_id ";
    
    private String sqlVideo = sqlFields + ", DATE_FORMAT(Videos.publication_date, '%e-%c-%Y') AS publication_date, Videos.description, Videos.playcount "
                            + "FROM Videos "
                            + "INNER JOIN Tracks ON Videos.track_id=Tracks.id "
                            + sqlJoins;
    private String sqlSong  = sqlFields + ", Albums.name AS album_name "
                            + "FROM Songs "
                            + "LEFT JOIN Albums ON Songs.album_id=Albums.id "
                            + "INNER JOIN Tracks ON Songs.track_id=Tracks.id "
                            + sqlJoins;

    // @Override
    // public ArrayList<Track> getTracks(String token, String forPlaylist ) {
    //     String sqlWhere = "WHERE TrackMappers.track_id NOT IN ( SELECT track_id FROM TrackMappers WHERE playlist_id = ? ) OR TrackMappers.playlist_id IS NULL";

    //     try (Connection connection = this.dataSource.getConnection()) {
    //         PreparedStatement statementVideo = connection.prepareStatement(sqlVideo+sqlWhere);
    //         statementVideo.setString(1, forPlaylist);
    //         ResultSet resultSetVideo = statementVideo.executeQuery();

    //         PreparedStatement statementSong = connection.prepareStatement(sqlSong+sqlWhere);
    //         statementSong.setString(1, forPlaylist);
    //         ResultSet resultSetSong = statementSong.executeQuery();

    //         return this.parseTrackData(resultSetVideo, resultSetSong);
            
    //     } catch ( SQLException e ) {
    //         e.printStackTrace();
    //     }
    //     return null;
    // }

    @Override
    public ArrayList<Track> getTracks(String token, String forPlaylist) {
        try (Connection con = DB.getNoSQLURL()) {
            String queryRetrieve = "MATCH (ts:Tracks), (pl:Playlists) "
                                    + "OPTIONAL MATCH (album:Albums)-[:contains]->(ts) "
                                    + "WITH ts, pl, album "
                                    + "WHERE NOT (ts)-[:in]->(pl { id: ? }) "
                                    + "RETURN {album_name: album.name, track: ts} AS data";

            PreparedStatement stmtRetrieve = con.prepareStatement(queryRetrieve);
            stmtRetrieve.setString(1, forPlaylist);
            ResultSet resultSet = stmtRetrieve.executeQuery();

            ArrayList<Track> tracks = new ArrayList<>();

            while (resultSet.next()) {
                String dataString = resultSet.getString("data");
                JSONObject dataJSON = new JSONObject(dataString);

                JSONObject trackJSON = dataJSON.getJSONObject("track");

                Track track = null;

                if ( trackJSON.getString("type").equals("song") ) {
                    track = new Song( trackJSON.getString("id") );
                    parseTrackDataJSON(track, trackJSON);

                    if ( dataJSON.has("album_name") )
                        track.setAlbum( new Album(dataJSON.getString("album_name")) );

                } else {
                    track = new Video( trackJSON.getString("id") );
                    parseTrackDataJSON(track, trackJSON);

                    if ( trackJSON.has("playcount") ) 
                        track.setPlaycount(trackJSON.getInt("playcount"));

                    if ( trackJSON.has("publication_date") ) 
                        track.setPublicationDate(trackJSON.getString("publication_date"));
                    
                    if ( trackJSON.has("description") ) 
                        track.setDescription( trackJSON.getString("description") );
                }

                tracks.add(track);
            }

            return tracks;

        } catch ( SQLException e ) {
            // TODO: errorhandling
            e.printStackTrace();
        }
        
        return null;
    }

    private void parseTrackDataJSON( Track track, JSONObject trackJSON ) {
        track.setDuration(trackJSON.getInt("duration"));
        track.setPerformer(trackJSON.getString("performer"));
        track.setTitle(trackJSON.getString("title"));
        track.setUrl(trackJSON.getString("url"));
    }

    private ArrayList<Track> parseTrackData(ResultSet resultSetVideo, ResultSet resultSetSong) {
        ArrayList<Track> tracks = new ArrayList<>();

        try {
            while( resultSetVideo.next() ) {
                Track track = new Video(resultSetVideo.getString("id"));
                addTrackData(track, resultSetVideo);
                
                track.setPublicationDate(resultSetVideo.getString("publication_date"));
                track.setDescription(resultSetVideo.getString("description"));
                track.setPlaycount(resultSetVideo.getInt("playcount"));
    
                tracks.add(track);
            }
    
            while( resultSetSong.next() ) {
                Track track = new Song(resultSetSong.getString("id"));
                addTrackData(track, resultSetSong);
                
                Album album = new Album(resultSetSong.getString("album_name"));
                track.setAlbum(album);
    
                tracks.add(track);
            }
    
        } catch ( SQLException e ) {
            e.printStackTrace();
        }

        return tracks;
    }

    private void addTrackData( Track track, ResultSet set ) throws SQLException {
        track.setPerformer(set.getString("performer"));
        track.setTitle    (set.getString("title"));
        track.setUrl(set.getString("url"));
        track.setDuration(set.getInt("duration"));
        track.setOfflineAvailable(set.getBoolean("offline_available"));
    }

    @Override
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ArrayList<Track> getTracksFromPlaylist(String token, String playlist_id) {
        String sqlWhere = "WHERE TrackMappers.playlist_id = ?";

        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement statementVideo = connection.prepareStatement(sqlVideo+sqlWhere);
            statementVideo.setString(1, playlist_id);
            ResultSet resultSetVideo = statementVideo.executeQuery();

            PreparedStatement statementSong = connection.prepareStatement(sqlSong+sqlWhere);
            statementSong.setString(1, playlist_id);

            ResultSet resultSetSong = statementSong.executeQuery();

            return this.parseTrackData(resultSetVideo, resultSetSong);
            
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<Track> addTrackToPlaylist(String token, String playlist_id, String track_id, boolean track_offlineAvailable) {

        boolean owns = this.ownsPlaylist(token, playlist_id);
        if ( !owns ) return null;

        String sql = "INSERT INTO TrackMappers ( track_id, playlist_id, offline_available ) "
                    + "VALUES ( ?, ?, ? )";

        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, track_id);
            statement.setString(2, playlist_id);
            statement.setBoolean(3, track_offlineAvailable);

            statement.executeUpdate();

            return this.getTracksFromPlaylist( token, playlist_id );

        } catch ( SQLException e ) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public ArrayList<Track> deleteTrackFromPlaylist(String token, String playlist_id, String track_id) {

        String sql = "DELETE TrackMappers "
                    + "FROM TrackMappers "
                    + "INNER JOIN PlaylistMappers ON TrackMappers.playlist_id=PlaylistMappers.playlist_id "
                    + "INNER JOIN Users On PlaylistMappers.user_id=Users.id "
                    + "WHERE TrackMappers.playlist_id = ? "
                    + "AND TrackMappers.track_id = ? "
                    + "AND Users.token = ?";

        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, playlist_id);
            statement.setString(2, track_id);
            statement.setString(3, token);

            if ( statement.executeUpdate() != 1 ) return null;

            return this.getTracksFromPlaylist( token, playlist_id );

        } catch ( SQLException e ) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean ownsPlaylist(String token, String playlist_id) {
        
        String sql = "SELECT PlaylistMappers.owner as owns FROM Users INNER JOIN PlaylistMappers ON Users.id=PlaylistMappers.user_id WHERE Users.token = ? AND PlaylistMappers.playlist_id = ? ";

        try ( Connection connection = this.dataSource.getConnection() ) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, token);
            statement.setString(2, playlist_id);
            ResultSet result = statement.executeQuery();

            while ( result.next() ) {
                return result.getBoolean("owns");
            }
        } catch ( SQLException e ) {
        }

        return false;
    }
}
