package tests.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.TrackDAO;
import domain.Track;

public class TrackDAOTest {
    private String sqlFields = "SELECT TrackMappers.offline_available, Tracks.id, Tracks.performer, Tracks.title, Tracks.url, Tracks.duration";
    private String sqlJoins = "LEFT JOIN TrackMappers ON TrackMappers.track_id=Tracks.id "
                            + "LEFT JOIN PlaylistMappers ON PlaylistMappers.playlist_id=TrackMappers.playlist_id "
                            + "LEFT JOIN Users ON Users.id=PlaylistMappers.user_id ";

    private String sqlTracksWhere = "WHERE TrackMappers.playlist_id = ?";
    private String sqlVideo = sqlFields + ", DATE_FORMAT(Videos.publication_date, '%e-%c-%Y') AS publication_date, Videos.description, Videos.playcount "
                            + "FROM Videos "
                            + "INNER JOIN Tracks ON Videos.track_id=Tracks.id "
                            + sqlJoins
                            + sqlTracksWhere;
    private String sqlSong  = sqlFields + ", Albums.name AS album_name "
                            + "FROM Songs "
                            + "LEFT JOIN Albums ON Songs.album_id=Albums.id "
                            + "INNER JOIN Tracks ON Songs.track_id=Tracks.id "
                            + sqlJoins
                            + sqlTracksWhere;

    private String sqlOwns = "SELECT PlaylistMappers.owner as owns FROM Users INNER JOIN PlaylistMappers ON Users.id=PlaylistMappers.user_id WHERE Users.token = ? AND PlaylistMappers.playlist_id = ? ";


    private TrackDAO trackDAO;
    private DataSource dataSource;
    private Connection connection;

    @BeforeEach
    public void setup() {
        this.dataSource = mock(DataSource.class);
        this.connection = mock(Connection.class);

        this.trackDAO = new TrackDAO();
        this.trackDAO.setDataSource(this.dataSource);
    }

    @Test
    public void getTracksTestRegular() {
        String sqlWhere = "WHERE TrackMappers.track_id NOT IN ( SELECT track_id FROM TrackMappers WHERE playlist_id = ? ) OR TrackMappers.playlist_id IS NULL";

        String sqlVideo = sqlFields + ", DATE_FORMAT(Videos.publication_date, '%e-%c-%Y') AS publication_date, Videos.description, Videos.playcount "
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
        String tokenToUse = "1425-2565-5487";
        String forPlaylistToUse = "RtUtzbPwzN1rds0qEGtSvsmcvtIT3Rpxg0";

        // setup mocks
        // VIDEO
        PreparedStatement preparedStatementVideo = mock(PreparedStatement.class);
        ResultSet resultSetVideo = mock(ResultSet.class);

        // SONG
        PreparedStatement preparedStatementSong = mock(PreparedStatement.class);
        ResultSet resultSetSong = mock(ResultSet.class);

        try {
            // VIDEO
            when(dataSource.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(sqlVideo)).thenReturn(preparedStatementVideo);
            when(preparedStatementVideo.executeQuery()).thenReturn(resultSetVideo);
            when(resultSetVideo.next()).thenReturn(true).thenReturn(false);
            when(resultSetVideo.getString("performer")).thenReturn("srv");
            when(resultSetVideo.getString("title")).thenReturn("texas flood");
            when(resultSetVideo.getInt("duration")).thenReturn(123);
            when(resultSetVideo.getBoolean("offline_available")).thenReturn(true);
            when(resultSetVideo.getString("publication_date")).thenReturn("2021-03-15 11:04:11");
            when(resultSetVideo.getString("description")).thenReturn("");
            when(resultSetVideo.getInt("playcount")).thenReturn(123);

            // SONG
            when(dataSource.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(sqlSong)).thenReturn(preparedStatementSong);
            when(preparedStatementSong.executeQuery()).thenReturn(resultSetSong);
            when(resultSetSong.next()).thenReturn(true).thenReturn(false);
            when(resultSetSong.getString("performer")).thenReturn("srv");
            when(resultSetSong.getString("title")).thenReturn("texas flood");
            when(resultSetSong.getInt("duration")).thenReturn(123);
            when(resultSetSong.getBoolean("offline_available")).thenReturn(true);
            when(resultSetSong.getString("id")).thenReturn("LaaDzWjBjiVi8krXYTLW8b8iuW6wW4HX5u");
            when(resultSetSong.getString("alumb_name")).thenReturn("Texas Flood");

            // Act
            ArrayList<Track> tracks = trackDAO.getTracks(tokenToUse, forPlaylistToUse);
    
            // Assert
            verify(connection).prepareStatement(sqlVideo);
            verify(preparedStatementVideo).setString(1, forPlaylistToUse);
            verify(preparedStatementVideo).executeQuery();

            verify(connection).prepareStatement(sqlSong);
            verify(preparedStatementSong).setString(1, forPlaylistToUse);
            verify(preparedStatementSong).executeQuery();

            assertEquals(2, tracks.size());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getTracksFromPlaylistTest() {
        try {
            String tokenToExpect = "1425-2565-5487";

            String test_id = "1";

            // setup mocks
            Connection connection = mock(Connection.class);

            PreparedStatement preparedStatementVideo = mock(PreparedStatement.class);
            ResultSet resultSetVideo = mock(ResultSet.class);

            PreparedStatement preparedStatementSong = mock(PreparedStatement.class);
            ResultSet resultSetSong = mock(ResultSet.class);
            
            // instruct mocks
            when(dataSource.getConnection()).thenReturn(connection);
            
            when(connection.prepareStatement(sqlVideo)).thenReturn(preparedStatementVideo);
            when(preparedStatementVideo.executeQuery()).thenReturn(resultSetVideo);
            when(resultSetVideo.next()).thenReturn(true).thenReturn(false);

            when(connection.prepareStatement(sqlSong)).thenReturn(preparedStatementSong);
            when(preparedStatementSong.executeQuery()).thenReturn(resultSetSong);
            when(resultSetSong.next()).thenReturn(true).thenReturn(false);

            // Act
            ArrayList<Track> tracks = this.trackDAO.getTracksFromPlaylist(tokenToExpect, test_id);

            // Assert
            verify(connection).prepareStatement(sqlVideo);
            verify(preparedStatementVideo).setString(1, test_id);
            verify(preparedStatementVideo).executeQuery();

            verify(connection).prepareStatement(sqlSong);
            verify(preparedStatementSong).setString(1, test_id);
            verify(preparedStatementSong).executeQuery();

            assertNotNull(tracks);

        } catch ( Exception e ) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void addTrackToPlaylistTestRegular() {
        try {
            String sql = "INSERT INTO TrackMappers ( track_id, playlist_id, offline_available ) VALUES ( ?, ?, ? )";

            String tokenToExpect = "1425-2565-5487";
            String test_playlist_id = "1";
            String test_track_id = "1";
            boolean test_offline_available = true;

            // setup mocks
            Connection connection = mock(Connection.class);

            PreparedStatement preparedStatement = mock(PreparedStatement.class);

            PreparedStatement preparedStatementOwns = mock(PreparedStatement.class);
            ResultSet resultSetOwns = mock(ResultSet.class);

            PreparedStatement preparedStatementVideo = mock(PreparedStatement.class);
            ResultSet resultSetVideo = mock(ResultSet.class);

            PreparedStatement preparedStatementSong = mock(PreparedStatement.class);
            ResultSet resultSetSong = mock(ResultSet.class);
            
            // instruct mocks
            when(dataSource.getConnection()).thenReturn(connection);
            
            when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(1);

            when(dataSource.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(sqlOwns)).thenReturn(preparedStatementOwns);
            when(preparedStatementOwns.executeQuery()).thenReturn(resultSetOwns);
            when(resultSetOwns.next()).thenReturn(true).thenReturn(false);
            when(resultSetOwns.getBoolean("owns")).thenReturn(true);

            when(connection.prepareStatement(sqlVideo)).thenReturn(preparedStatementVideo);
            when(preparedStatementVideo.executeQuery()).thenReturn(resultSetVideo);
            when(resultSetVideo.next()).thenReturn(false);

            when(connection.prepareStatement(sqlSong)).thenReturn(preparedStatementSong);
            when(preparedStatementSong.executeQuery()).thenReturn(resultSetSong);
            when(resultSetSong.next()).thenReturn(false);

            // Act
            ArrayList<Track> tracks = this.trackDAO.addTrackToPlaylist(tokenToExpect, test_playlist_id, test_track_id, test_offline_available);

            // Assert
            verify(connection).prepareStatement(sql);
            verify(preparedStatement).setString(1, test_track_id);
            verify(preparedStatement).setString(2, test_playlist_id);
            verify(preparedStatement).setBoolean(3, test_offline_available);
            verify(preparedStatement).executeUpdate();

            verify(connection).prepareStatement(sqlOwns);
            verify(preparedStatementOwns).setString(1, tokenToExpect);
            verify(preparedStatementOwns).setString(2, test_playlist_id);
            verify(preparedStatementOwns).executeQuery();

            assertNotNull(tracks);

        } catch ( Exception e ) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void addTrackToPlaylistTestNotOwner() {
        try {
            String tokenToExpect = "1425-2565-5487";
            String test_playlist_id = "1";
            String test_track_id = "1";
            boolean test_offline_available = true;

            // setup mocks
            Connection connection = mock(Connection.class);

            PreparedStatement preparedStatementOwns = mock(PreparedStatement.class);
            ResultSet resultSetOwns = mock(ResultSet.class);
            
            // instruct mocks
            when(dataSource.getConnection()).thenReturn(connection);

            when(connection.prepareStatement(sqlOwns)).thenReturn(preparedStatementOwns);
            when(preparedStatementOwns.executeQuery()).thenReturn(resultSetOwns);
            when(resultSetOwns.next()).thenReturn(true).thenReturn(false);
            when(resultSetOwns.getBoolean("owns")).thenReturn(false);

            // Act
            ArrayList<Track> tracks = this.trackDAO.addTrackToPlaylist(tokenToExpect, test_playlist_id, test_track_id, test_offline_available);

            // Assert
            verify(connection).prepareStatement(sqlOwns);
            verify(preparedStatementOwns).setString(1, tokenToExpect);
            verify(preparedStatementOwns).setString(2, test_playlist_id);
            verify(preparedStatementOwns).executeQuery();

            assertNull(tracks);

        } catch ( Exception e ) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void deleteTrackFromPlaylistTestRegular() {
        try {
            String sql = "DELETE TrackMappers "
                        + "FROM TrackMappers "
                        + "INNER JOIN PlaylistMappers ON TrackMappers.playlist_id=PlaylistMappers.playlist_id "
                        + "INNER JOIN Users On PlaylistMappers.user_id=Users.id "
                        + "WHERE TrackMappers.playlist_id = ? "
                        + "AND TrackMappers.track_id = ? "
                        + "AND Users.token = ?";


            String tokenToExpect = "1425-2565-5487";
            String test_playlist_id = "1";
            String test_track_id = "1";

            // setup mocks
            Connection connection = mock(Connection.class);
            PreparedStatement preparedStatement = mock(PreparedStatement.class);

            PreparedStatement preparedStatementVideo = mock(PreparedStatement.class);
            ResultSet resultSetVideo = mock(ResultSet.class);

            PreparedStatement preparedStatementSong = mock(PreparedStatement.class);
            ResultSet resultSetSong = mock(ResultSet.class);
            
            // instruct mocks
            when(dataSource.getConnection()).thenReturn(connection);
            
            when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(1);

            when(connection.prepareStatement(sqlVideo)).thenReturn(preparedStatementVideo);
            when(preparedStatementVideo.executeQuery()).thenReturn(resultSetVideo);
            when(resultSetVideo.next()).thenReturn(false);

            when(connection.prepareStatement(sqlSong)).thenReturn(preparedStatementSong);
            when(preparedStatementSong.executeQuery()).thenReturn(resultSetSong);
            when(resultSetSong.next()).thenReturn(false);

            // Act
            ArrayList<Track> tracks = this.trackDAO.deleteTrackFromPlaylist(tokenToExpect, test_playlist_id, test_track_id);

            // Assert
            verify(connection).prepareStatement(sql);
            verify(preparedStatement).setString(1, test_playlist_id);
            verify(preparedStatement).setString(2, test_track_id);
            verify(preparedStatement).setString(3, tokenToExpect);
            verify(preparedStatement).executeUpdate();

            assertNotNull(tracks);

        } catch ( Exception e ) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void deleteTrackFromPlaylistTestNotOwner() {
        try {
            String sql = "DELETE TrackMappers "
                        + "FROM TrackMappers "
                        + "INNER JOIN PlaylistMappers ON TrackMappers.playlist_id=PlaylistMappers.playlist_id "
                        + "INNER JOIN Users On PlaylistMappers.user_id=Users.id "
                        + "WHERE TrackMappers.playlist_id = ? "
                        + "AND TrackMappers.track_id = ? "
                        + "AND Users.token = ?";


            String tokenToExpect = "1425-2565-5487";
            String test_playlist_id = "1";
            String test_track_id = "1";

            // setup mocks
            Connection connection = mock(Connection.class);
            PreparedStatement preparedStatement = mock(PreparedStatement.class);
            
            // instruct mocks
            when(dataSource.getConnection()).thenReturn(connection);
            
            when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(0);

            // Act
            ArrayList<Track> tracks = this.trackDAO.deleteTrackFromPlaylist(tokenToExpect, test_playlist_id, test_track_id);

            // Assert
            verify(connection).prepareStatement(sql);
            verify(preparedStatement).setString(1, test_playlist_id);
            verify(preparedStatement).setString(2, test_track_id);
            verify(preparedStatement).setString(3, tokenToExpect);
            verify(preparedStatement).executeUpdate();

            assertNull(tracks);

        } catch ( Exception e ) {
            e.printStackTrace();
            fail();
        }
    }

    // @Test
    // public void OwnsPlaylistTestRegular() {
    //     try {
    //         String sql = "SELECT PlaylistMappers.owner as owns FROM Users INNER JOIN PlaylistMappers ON Users.id=PlaylistMappers.user_id WHERE Users.token = ? AND PlaylistMappers.playlist_id = ? ";
    //         String tokenToExpect = "1425-2565-5487";
    //         String test_playlist_id = "1";

    //         PreparedStatement preparedStatement = mock(PreparedStatement.class);
    //         ResultSet resultSet = mock(ResultSet.class);

    //         when(dataSource.getConnection()).thenReturn(this.connection);
                
    //         when(this.connection.prepareStatement(sql)).thenReturn(preparedStatement);
    //         when(preparedStatement.executeQuery()).thenReturn(resultSet);
    //         when(resultSet.next()).thenReturn(true).thenReturn(false);
    //         when(resultSet.getBoolean("owns")).thenReturn(true);

    //         verify(this.connection).prepareStatement(sql);
    //         verify(preparedStatement).setString(1, tokenToExpect);
    //         verify(preparedStatement).setString(2, test_playlist_id);
    //         verify(preparedStatement).executeUpdate();

    //         boolean owns = trackDAO.ownsPlaylist(tokenToExpect, test_playlist_id);

    //         assertTrue(owns);

    //     } catch ( Exception e ) {
    //         e.printStackTrace();
    //         fail();
    //     }
    // }

    // @Test
    // public void OwnsPlaylistTestNotOwner(String token, String playlist_id) {
    //     try {
    //         String sql = "SELECT PlaylistMappers.owner as owns FROM Users INNER JOIN PlaylistMappers ON Users.id=PlaylistMappers.user_id WHERE Users.token = ? AND PlaylistMappers.playlist_id = ? ";

    //         Connection connection = mock(Connection.class);

    //         PreparedStatement preparedStatement = mock(PreparedStatement.class);
    //         ResultSet resultSet = mock(ResultSet.class);

    //         when(dataSource.getConnection()).thenReturn(connection);
                
    //         when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
    //         when(preparedStatement.executeQuery()).thenReturn(resultSet);
    //         when(resultSet.next()).thenReturn(true).thenReturn(false);
    //         when(resultSet.getBoolean("owns")).thenReturn(false);

    //         verify(connection).prepareStatement(sql);
    //         verify(preparedStatement).setString(1, token);
    //         verify(preparedStatement).setString(2, playlist_id);
    //         verify(preparedStatement).executeUpdate();

    //         boolean owns = trackDAO.ownsPlaylist(token, playlist_id);

    //         assertFalse(owns);

    //     } catch ( Exception e ) {
    //         e.printStackTrace();
    //         fail();
    //     }
    // }
}
