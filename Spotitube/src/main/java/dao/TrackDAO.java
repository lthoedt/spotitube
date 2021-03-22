package dao;

import javax.annotation.Resource;
import javax.sql.DataSource;

import java.sql.*;
import java.util.ArrayList;

import domain.Album;
import domain.Song;
import domain.Track;
import domain.Video;

public class TrackDAO implements ITrackDAO {

    @Resource(name = "jdbc/spotitube")
    DataSource dataSource;

    private String sqlFields = "SELECT TrackMappers.offline_available, Tracks.id, Tracks.performer, Tracks.title, Tracks.url, Tracks.duration";
    private String sqlJoins = "LEFT JOIN TrackMappers ON TrackMappers.track_id=Tracks.id "
                            + "LEFT JOIN PlaylistMappers ON PlaylistMappers.playlist_id=TrackMappers.playlist_id "
                            + "LEFT JOIN Users ON Users.id=PlaylistMappers.user_id ";


    @Override
    public ArrayList<Track> getTracks(String token, String forPlaylist ) {
        String sqlWhere = "WHERE TrackMappers.track_id NOT IN ( SELECT track_id FROM TrackMappers WHERE playlist_id = ? ) OR TrackMappers.playlist_id IS NULL";

        String sqlVideo = sqlFields + ", Videos.publication_date, Videos.description, Videos.playcount "
                            + "FROM Videos "
                            + "INNER JOIN Tracks ON Videos.track_id=Tracks.id "
                            + sqlJoins
                            + sqlWhere;
        String sqlSong  = sqlFields + ", Albums.name AS album_name "
                            + "FROM Songs "
                            + "LEFT JOIN Albums ON Songs.album_id=Albums.id "
                            + "INNER JOIN Tracks ON Songs.track_id=Tracks.id "
                            + sqlJoins
                            + sqlWhere;

        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement statementVideo = connection.prepareStatement(sqlVideo);
            statementVideo.setString(1, forPlaylist);
            ResultSet resultSetVideo = statementVideo.executeQuery();

            PreparedStatement statementSong = connection.prepareStatement(sqlSong);
            statementSong.setString(1, forPlaylist);
            ResultSet resultSetSong = statementSong.executeQuery();

            return this.parseTrackData(resultSetVideo, resultSetSong);
            
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
        return null;
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

    private void addTrackData( Track track, ResultSet set ) {
        try {
            track.setPerformer(set.getString("performer"));
            track.setTitle    (set.getString("title"));
            track.setUrl(set.getString("url"));
            track.setDuration(set.getInt("duration"));
            track.setOfflineAvailable(set.getBoolean("offline_available"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ArrayList<Track> getTracksFromPlaylist(String token, String playlist_id) {
        String sqlWhere = "WHERE TrackMappers.playlist_id = ?";

        String sqlVideo = sqlFields + ", Videos.publication_date, Videos.description, Videos.playcount "
                            + "FROM Videos "
                            + "INNER JOIN Tracks ON Videos.track_id=Tracks.id "
                            + sqlJoins
                            + sqlWhere;
        String sqlSong  = sqlFields + ", Albums.name AS album_name "
                            + "FROM Songs "
                            + "LEFT JOIN Albums ON Songs.album_id=Albums.id "
                            + "INNER JOIN Tracks ON Songs.track_id=Tracks.id "
                            + sqlJoins
                            + sqlWhere;

        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement statementVideo = connection.prepareStatement(sqlVideo);
            statementVideo.setString(1, playlist_id);
            ResultSet resultSetVideo = statementVideo.executeQuery();

            PreparedStatement statementSong = connection.prepareStatement(sqlSong);
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

            if ( statement.executeUpdate() != 1 ) return null;

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
            // TODO
            e.printStackTrace();
        }

        return false;
    }
}
