import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;

import dao.TrackDAO;
import domain.Track;

public class TrackDAOTest {
    @Test
    public void getTracksTestRegular() {
        String sqlFields = "SELECT TrackMappers.offline_available, Tracks.id, Tracks.performer, Tracks.title, Tracks.url, Tracks.duration";
        String sqlJoins = "LEFT JOIN TrackMappers ON TrackMappers.track_id=Tracks.id "
                            + "LEFT JOIN PlaylistMappers ON PlaylistMappers.playlist_id=TrackMappers.playlist_id "
                            + "LEFT JOIN Users ON Users.id=PlaylistMappers.user_id ";
        String sqlWhere = "WHERE TrackMappers.playlist_id != ? OR TrackMappers.playlist_id IS NULL";

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
        String tokenToUse = "1425-2565-5487";
        String forPlaylistToUse = "RtUtzbPwzN1rds0qEGtSvsmcvtIT3Rpxg0";

        // setup mocks
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
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

            TrackDAO trackDAO = new TrackDAO();
            trackDAO.setDataSource(dataSource);

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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }   
}
