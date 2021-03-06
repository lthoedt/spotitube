package tests.dao;

import org.junit.jupiter.api.Test;

import dao.IPlaylistDAO;
import dao.PlaylistDAO;
import domain.Playlist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.sql.DataSource;

public class PlaylistDAOTest {

    private String sqlUserJoin = "INNER JOIN PlaylistMappers ON Playlists.id=PlaylistMappers.playlist_id "
                                + "INNER JOIN Users ON PlaylistMappers.user_id=Users.id ";
    
    private  String sqlGetPlaylists = "SELECT Playlists.id, Playlists.name, (SELECT id FROM Users WHERE token = ?) AS users_id, PlaylistMappers.user_id, SUM(Tracks.duration) AS duration "
                                    + "FROM Playlists "
                                    + "LEFT JOIN PlaylistMappers ON PlaylistMappers.playlist_id=Playlists.id "
                                    + "INNER JOIN Users ON PlaylistMappers.user_id=Users.id "
                                    + "LEFT JOIN TrackMappers ON PlaylistMappers.playlist_id=TrackMappers.playlist_id "
                                    + "LEFT JOIN Tracks ON TrackMappers.track_id=Tracks.id "
                                    + "GROUP BY Playlists.id, Playlists.name, users_id, PlaylistMappers.user_id";


    @Test
    public void getPlaylistsTest() {
        try {
            String expectedSQL = "SELECT Playlists.id, Playlists.name, (SELECT id FROM Users WHERE token = ?) AS users_id, PlaylistMappers.user_id, SUM(Tracks.duration) AS duration "
                                + "FROM Playlists "
                                + "LEFT JOIN PlaylistMappers ON PlaylistMappers.playlist_id=Playlists.id "
                                + "INNER JOIN Users ON PlaylistMappers.user_id=Users.id "
                                + "LEFT JOIN TrackMappers ON PlaylistMappers.playlist_id=TrackMappers.playlist_id "
                                + "LEFT JOIN Tracks ON TrackMappers.track_id=Tracks.id "
                                + "GROUP BY Playlists.id, Playlists.name, users_id, PlaylistMappers.user_id";
            String tokenToExpect = "1425-2565-5487";

            String test_id = "RtUtzbPwzN1rds0qEGtSvsmcvtIT3Rpxg0";
            String test_name = "SRV";
            String test_users_id = "1";
            String test_user_id = "1";

            // setup mocks
            DataSource dataSource = mock(DataSource.class);
            Connection connection = mock(Connection.class);
            PreparedStatement preparedStatement = mock(PreparedStatement.class);
            ResultSet resultSet = mock(ResultSet.class);
            
            // instruct mocks
            when(dataSource.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(expectedSQL)).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true).thenReturn(false);
            when(resultSet.getString("id")).thenReturn(test_id);
            when(resultSet.getString("name")).thenReturn(test_name);
            when(resultSet.getString("users_id")).thenReturn(test_users_id);
            when(resultSet.getString("user_id")).thenReturn(test_user_id);

            IPlaylistDAO playlistDAO = new PlaylistDAO();
            playlistDAO.setDataSource(dataSource);

            // Act
            ArrayList<Playlist> playlists = playlistDAO.getPlaylists(tokenToExpect);

            // Assert
            verify(connection).prepareStatement(expectedSQL);
            verify(preparedStatement).setString(1, tokenToExpect);
            verify(preparedStatement).executeQuery();

            assertEquals(test_name, playlists.get(0).getName());
            assertTrue(playlists.get(0).getOwner());

        } catch ( Exception e ) {
            System.out.println("");
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void addPlaylistTest() {
        try {
            String sqlPL = "INSERT INTO Playlists ( id, name ) VALUES ( ?, ? )";
            String sqlPLM = "INSERT INTO PlaylistMappers ( playlist_id, user_id, owner ) "
                            + "SELECT ?, id, 1 "
                            + "FROM Users "
                            + "WHERE token=?";
            String tokenToExpect = "htuE-0f6l-ybEx";

            String test_name = "test";

            // setup mocks
            DataSource dataSource = mock(DataSource.class);
            Connection connection = mock(Connection.class);

            PreparedStatement preparedStatementPL = mock(PreparedStatement.class);

            PreparedStatement preparedStatementPLM = mock(PreparedStatement.class);

            PreparedStatement preparedStatementGetPlaylists = mock(PreparedStatement.class);
            ResultSet resultSetGetPlaylists = mock(ResultSet.class);
            
            // instruct mocks
            when(dataSource.getConnection()).thenReturn(connection);

            when(connection.prepareStatement(sqlPL)).thenReturn(preparedStatementPL);
            when(preparedStatementPL.executeUpdate()).thenReturn(1);

            when(connection.prepareStatement(sqlPLM)).thenReturn(preparedStatementPLM);
            when(preparedStatementPLM.executeUpdate()).thenReturn(1);

            when(connection.prepareStatement(sqlGetPlaylists)).thenReturn(preparedStatementGetPlaylists);
            when(preparedStatementGetPlaylists.executeQuery()).thenReturn(resultSetGetPlaylists);

            IPlaylistDAO playlistDAO = new PlaylistDAO();
            playlistDAO.setDataSource(dataSource);

            // Act
            ArrayList<Playlist> playlists = playlistDAO.addPlaylist(tokenToExpect, test_name);

            // Assert
            verify(connection).prepareStatement(sqlPL);
            verify(preparedStatementPL).setString(2, test_name);
            verify(preparedStatementPL).executeUpdate();

            verify(connection).prepareStatement(sqlPLM);
            verify(preparedStatementPLM).setString(2, tokenToExpect);
            verify(preparedStatementPLM).executeUpdate();

            verify(connection).prepareStatement(sqlGetPlaylists);
            verify(preparedStatementGetPlaylists).setString(1, tokenToExpect);

            assertNotNull(playlists);

        } catch ( Exception e ) {
            System.out.println("");
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void deletePlaylistTest() {
        try {
            String sql = "DELETE Playlists "
                        + "FROM Playlists "
                        + sqlUserJoin
                        + "WHERE Playlists.id = ? "
                        + "AND Users.token = ?";

            String tokenToExpect = "1425-2565-5487";
            int expectedSize = 0;

            String test_id = "1";

            // setup mocks
            DataSource dataSource = mock(DataSource.class);
            Connection connection = mock(Connection.class);
            PreparedStatement preparedStatement = mock(PreparedStatement.class);

            PreparedStatement preparedStatementGetPlaylists = mock(PreparedStatement.class);
            ResultSet resultSetGetPlaylists = mock(ResultSet.class);
            
            // instruct mocks
            when(dataSource.getConnection()).thenReturn(connection);

            when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(1);
            
            when(connection.prepareStatement(sqlGetPlaylists)).thenReturn(preparedStatementGetPlaylists);
            when(preparedStatementGetPlaylists.executeQuery()).thenReturn(resultSetGetPlaylists);


            IPlaylistDAO playlistDAO = new PlaylistDAO();
            playlistDAO.setDataSource(dataSource);

            // Act
            ArrayList<Playlist> playlists = playlistDAO.deletePlaylist(tokenToExpect, test_id);

            // Assert
            verify(connection).prepareStatement(sql);
            verify(preparedStatement).setString(1, test_id);
            verify(preparedStatement).setString(2, tokenToExpect);
            verify(preparedStatement).executeUpdate();

            verify(connection).prepareStatement(sqlGetPlaylists);
            verify(preparedStatementGetPlaylists).setString(1, tokenToExpect);

            assertEquals(expectedSize, playlists.size());

        } catch ( Exception e ) {
            System.out.println("");
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void deletePlaylistTestNotOwner() {
        try {
            String sql = "DELETE Playlists "
                        + "FROM Playlists "
                        + sqlUserJoin
                        + "WHERE Playlists.id = ? "
                        + "AND Users.token = ?";

            String tokenToExpect = "1";

            String test_id = "1";

            // setup mocks
            DataSource dataSource = mock(DataSource.class);
            Connection connection = mock(Connection.class);
            PreparedStatement preparedStatement = mock(PreparedStatement.class);
            
            // instruct mocks
            when(dataSource.getConnection()).thenReturn(connection);

            when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(0);

            IPlaylistDAO playlistDAO = new PlaylistDAO();
            playlistDAO.setDataSource(dataSource);

            // Act
            ArrayList<Playlist> playlists = playlistDAO.deletePlaylist(tokenToExpect, test_id);

            // Assert
            verify(connection).prepareStatement(sql);
            verify(preparedStatement).setString(1, test_id);
            verify(preparedStatement).executeUpdate();

            assertNull(playlists);

        } catch ( Exception e ) {
            System.out.println("");
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void editPlaylistTest() {
        try {
            String sql = "UPDATE Playlists "
                        + sqlUserJoin
                        + "SET name = ? "
                        + "WHERE Playlists.id = ? "
                        + "AND Users.token = ?";

            String tokenToExpect = "1425-2565-5487";

            String test_id = "1";
            String test_name = "henk";

            // setup mocks
            DataSource dataSource = mock(DataSource.class);
            Connection connection = mock(Connection.class);
            PreparedStatement preparedStatement = mock(PreparedStatement.class);

            PreparedStatement preparedStatementGetPlaylists = mock(PreparedStatement.class);
            ResultSet resultSetGetPlaylists = mock(ResultSet.class);
            
            // instruct mocks
            when(dataSource.getConnection()).thenReturn(connection);

            when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(1);

            when(connection.prepareStatement(sqlGetPlaylists)).thenReturn(preparedStatementGetPlaylists);
            when(preparedStatementGetPlaylists.executeQuery()).thenReturn(resultSetGetPlaylists);

            IPlaylistDAO playlistDAO = new PlaylistDAO();
            playlistDAO.setDataSource(dataSource);

            // Act
            ArrayList<Playlist> playlists = playlistDAO.editPlaylist(tokenToExpect, test_id, test_name);

            // Assert
            verify(connection).prepareStatement(sql);
            verify(preparedStatement).setString(1, test_name);
            verify(preparedStatement).setString(2, test_id);
            verify(preparedStatement).executeUpdate();

            verify(connection).prepareStatement(sqlGetPlaylists);
            verify(preparedStatementGetPlaylists).setString(1, tokenToExpect);

            assertNotNull(playlists);

        } catch ( Exception e ) {
            System.out.println("");
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void editPlaylistTestNotOwner() {
        try {
            String sql = "UPDATE Playlists "
                        + sqlUserJoin
                        + "SET name = ? "
                        + "WHERE Playlists.id = ? "
                        + "AND Users.token = ?";

            String tokenToExpect = "1425-2565-5487";

            String test_id = "1";
            String test_name = "henk";

            // setup mocks
            DataSource dataSource = mock(DataSource.class);
            Connection connection = mock(Connection.class);
            PreparedStatement preparedStatement = mock(PreparedStatement.class);
            
            // instruct mocks
            when(dataSource.getConnection()).thenReturn(connection);

            when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(0);

            IPlaylistDAO playlistDAO = new PlaylistDAO();
            playlistDAO.setDataSource(dataSource);

            // Act
            ArrayList<Playlist> playlists = playlistDAO.editPlaylist(tokenToExpect, test_id, test_name);

            // Assert
            verify(connection).prepareStatement(sql);
            verify(preparedStatement).setString(1, test_name);
            verify(preparedStatement).setString(2, test_id);
            verify(preparedStatement).setString(3, tokenToExpect);
            verify(preparedStatement).executeUpdate();

            assertNull(playlists);

        } catch ( Exception e ) {
            System.out.println("");
            e.printStackTrace();
            fail();
        }
    }
    
}
