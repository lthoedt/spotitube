package tests.dao;

import org.junit.jupiter.api.Test;

import dao.PlaylistDAO;
import domain.Playlist;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    @Test
    public void getPlaylistsTest() {
        try {
            String expectedSQL = "SELECT Playlists.id, Playlists.name, (SELECT id FROM Users WHERE token = ?) AS users_id, PlaylistMappers.user_id "
                            + "FROM Playlists "
                            + "LEFT JOIN PlaylistMappers ON PlaylistMappers.playlist_id=Playlists.id "
                            + "INNER JOIN Users ON PlaylistMappers.user_id=Users.id";
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

            PlaylistDAO playlistDAO = new PlaylistDAO();
            playlistDAO.setDataSource(dataSource);

            // Act
            ArrayList<Playlist> playlists = playlistDAO.getPlaylists(tokenToExpect);

            // Assert
            verify(connection).prepareStatement(expectedSQL);
            verify(preparedStatement).setString(1, tokenToExpect);
            verify(preparedStatement).executeQuery();

            // dependency in PlaylistDAO moet weg gehaald worden voordat deze test kan werken.
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
            String sqlPLM = "INSERT INTO PlaylistMappers ( playlist_id, user_id, owner ) VALUES ( ?, ?, 1 )";
    
            String tokenToExpect = "1425-2565-5487";

            String test_id = "1";
            String test_name = "test";
            String test_users_id = "1";
            String test_user_id = "1";

            // setup mocks
            DataSource dataSource = mock(DataSource.class);
            Connection connection = mock(Connection.class);

            PreparedStatement preparedStatementPL = mock(PreparedStatement.class);
            ResultSet resultSetPL = mock(ResultSet.class);

            PreparedStatement preparedStatementPLM = mock(PreparedStatement.class);
            ResultSet resultSetPLM = mock(ResultSet.class);
            
            // instruct mocks
            when(dataSource.getConnection()).thenReturn(connection);

            when(connection.prepareStatement(sqlPL)).thenReturn(preparedStatementPL);
            when(preparedStatementPL.executeQuery()).thenReturn(resultSetPL);
            when(resultSetPL.next()).thenReturn(true).thenReturn(false);

            when(connection.prepareStatement(sqlPLM)).thenReturn(preparedStatementPLM);
            when(preparedStatementPLM.executeQuery()).thenReturn(resultSetPLM);
            when(resultSetPLM.next()).thenReturn(true).thenReturn(false);

            PlaylistDAO playlistDAO = new PlaylistDAO();
            playlistDAO.setDataSource(dataSource);

            // Act
            ArrayList<Playlist> playlists = playlistDAO.addPlaylist(tokenToExpect, test_name);

            // Assert
            verify(connection).prepareStatement(sqlPL);
            verify(preparedStatementPL).setString(1, test_id);
            verify(preparedStatementPL).setString(2, test_users_id);
            verify(preparedStatementPL).executeUpdate();

            verify(connection).prepareStatement(sqlPLM);
            verify(preparedStatementPLM).setString(1, test_id);
            verify(preparedStatementPLM).setString(2, test_users_id);
            verify(preparedStatementPLM).executeUpdate();

            // dependency in PlaylistDAO moet weg gehaald worden voordat deze test kan werken.
            // miss werkt dit niet omdat er miss meerdere playlists zijn.
            assertEquals(test_id, playlists.get(0).getId());

        } catch ( Exception e ) {
            System.out.println("");
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void deletePlaylistTest() {
        try {
            String sql = "DELETE FROM Playlists WHERE id=?";

            String tokenToExpect = "1425-2565-5487";
            int expectedSize = 0;

            String test_id = "1";

            // setup mocks
            DataSource dataSource = mock(DataSource.class);
            Connection connection = mock(Connection.class);
            PreparedStatement preparedStatement = mock(PreparedStatement.class);
            ResultSet resultSet = mock(ResultSet.class);
            
            // instruct mocks
            when(dataSource.getConnection()).thenReturn(connection);

            when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true).thenReturn(false);

            PlaylistDAO playlistDAO = new PlaylistDAO();
            playlistDAO.setDataSource(dataSource);

            // Act
            ArrayList<Playlist> playlists = playlistDAO.deletePlaylist(tokenToExpect, test_id);

            // Assert
            verify(connection).prepareStatement(sql);
            verify(preparedStatement).setString(1, test_id);
            verify(preparedStatement).executeUpdate();

            // dependency in PlaylistDAO moet weg gehaald worden voordat deze test kan werken.
            // miss werkt dit niet omdat er miss meerdere playlists zijn.
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
            String sql = "DELETE FROM Playlists WHERE id=?";

            String tokenToExpect = "1";

            String test_id = "1";

            // setup mocks
            DataSource dataSource = mock(DataSource.class);
            Connection connection = mock(Connection.class);
            PreparedStatement preparedStatement = mock(PreparedStatement.class);
            ResultSet resultSet = mock(ResultSet.class);
            
            // instruct mocks
            when(dataSource.getConnection()).thenReturn(connection);

            when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true).thenReturn(false);

            PlaylistDAO playlistDAO = new PlaylistDAO();
            playlistDAO.setDataSource(dataSource);

            // Act
            ArrayList<Playlist> playlists = playlistDAO.deletePlaylist(tokenToExpect, test_id);

            // Assert
            verify(connection).prepareStatement(sql);
            verify(preparedStatement).setString(1, test_id);
            verify(preparedStatement).executeUpdate();

            // dependency in PlaylistDAO moet weg gehaald worden voordat deze test kan werken.
            // miss werkt dit niet omdat er miss meerdere playlists zijn.
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
            String sql = "UPDATE Playlists SET name = ? WHERE id = ?";

            String tokenToExpect = "1425-2565-5487";

            String test_id = "1";
            String test_name = "henk";

            // setup mocks
            DataSource dataSource = mock(DataSource.class);
            Connection connection = mock(Connection.class);
            PreparedStatement preparedStatement = mock(PreparedStatement.class);
            ResultSet resultSet = mock(ResultSet.class);
            
            // instruct mocks
            when(dataSource.getConnection()).thenReturn(connection);

            when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true).thenReturn(false);

            PlaylistDAO playlistDAO = new PlaylistDAO();
            playlistDAO.setDataSource(dataSource);

            // Act
            ArrayList<Playlist> playlists = playlistDAO.editPlaylist(tokenToExpect, test_id, test_name);

            // Assert
            verify(connection).prepareStatement(sql);
            verify(preparedStatement).setString(1, test_name);
            verify(preparedStatement).setString(2, test_id);
            verify(preparedStatement).executeUpdate();

            // dependency in PlaylistDAO moet weg gehaald worden voordat deze test kan werken.
            // miss werkt dit niet omdat er miss meerdere playlists zijn.
            assertEquals(test_name, playlists.get(0).getName());

        } catch ( Exception e ) {
            System.out.println("");
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void editPlaylistTestNotOwner() {
        try {
            String sql = "UPDATE Playlists SET name = ? WHERE id = ?";

            String tokenToExpect = "1425-2565-5487";

            String test_id = "1";
            String test_name = "henk";

            // setup mocks
            DataSource dataSource = mock(DataSource.class);
            Connection connection = mock(Connection.class);
            PreparedStatement preparedStatement = mock(PreparedStatement.class);
            ResultSet resultSet = mock(ResultSet.class);
            
            // instruct mocks
            when(dataSource.getConnection()).thenReturn(connection);

            when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true).thenReturn(false);

            PlaylistDAO playlistDAO = new PlaylistDAO();
            playlistDAO.setDataSource(dataSource);

            // Act
            ArrayList<Playlist> playlists = playlistDAO.editPlaylist(tokenToExpect, test_id, test_name);

            // Assert
            verify(connection).prepareStatement(sql);
            verify(preparedStatement).setString(1, test_name);
            verify(preparedStatement).setString(2, test_id);
            verify(preparedStatement).executeUpdate();

            // dependency in PlaylistDAO moet weg gehaald worden voordat deze test kan werken.
            assertNull(playlists);

        } catch ( Exception e ) {
            System.out.println("");
            e.printStackTrace();
            fail();
        }
    }
    
}
