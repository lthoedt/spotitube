package dao;

import javax.annotation.Resource;
import javax.sql.DataSource;

import domain.Playlist;

import java.sql.*;
import java.util.ArrayList;

import service.utils.DB;

public class PlaylistDAO implements IPlaylistDAO {

    @Resource(name = "jdbc/spotitube")
    DataSource dataSource;

    private String sqlUserJoin = "INNER JOIN PlaylistMappers ON Playlists.id=PlaylistMappers.playlist_id "
                                + "INNER JOIN Users ON PlaylistMappers.user_id=Users.id ";

    private ArrayList<Playlist> getPL(Connection connection, String token) throws SQLException {
        String sqlGetPlaylists = "SELECT Playlists.id, Playlists.name, (SELECT id FROM Users WHERE token = ?) AS users_id, PlaylistMappers.user_id, SUM(Tracks.duration) AS duration "
                                    + "FROM Playlists "
                                    + "LEFT JOIN PlaylistMappers ON PlaylistMappers.playlist_id=Playlists.id "
                                    + "INNER JOIN Users ON PlaylistMappers.user_id=Users.id "
                                    + "LEFT JOIN TrackMappers ON PlaylistMappers.playlist_id=TrackMappers.playlist_id "
                                    + "LEFT JOIN Tracks ON TrackMappers.track_id=Tracks.id "
                                    + "GROUP BY Playlists.id, Playlists.name, users_id, PlaylistMappers.user_id";

        PreparedStatement statementGetPlaylists = connection.prepareStatement(sqlGetPlaylists);
        statementGetPlaylists.setString(1, token);

        ResultSet resultSet = statementGetPlaylists.executeQuery();

        ArrayList<Playlist> playlists = new ArrayList<>();

        while ( resultSet.next() ) {
            Playlist playlist = new Playlist();

            playlist.setId(resultSet.getString("id"));
            playlist.setName(resultSet.getString("name"));
            // Wanneer users_id null is dan is de token niet goed dus is het niet de owner
            try {
                playlist.setOwner(resultSet.getString("users_id").equals(resultSet.getString("user_id")));
            } catch ( NullPointerException e ) {
                playlist.setOwner(false);
            }

            playlist.duration = resultSet.getInt("duration");

            playlists.add(playlist);
        }

        return playlists;
    }

    @Override
    public ArrayList<Playlist> getPlaylists(String token) {
        try ( Connection connection = this.dataSource.getConnection() ) {
            
            return this.getPL(connection, token);

        } catch ( SQLException e ) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<Playlist> deletePlaylist(String token, String playlist_id) {

        String sql = "DELETE Playlists "
                    + "FROM Playlists "
                    + sqlUserJoin
                    + "WHERE Playlists.id = ? "
                    + "AND Users.token = ?";

        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, playlist_id);
            statement.setString(2, token);
            int result = statement.executeUpdate();

            if (result != 1 ) return null;

            return this.getPL(connection, token);

        } catch ( SQLException e ) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public ArrayList<Playlist> addPlaylist(String token, String name) {
        String sqlPL = "INSERT INTO Playlists ( id, name ) VALUES ( ?, ? )";
        String sqlPLM = "INSERT INTO PlaylistMappers ( playlist_id, user_id, owner ) "
                        + "SELECT ?, id, 1 "
                        + "FROM Users "
                        + "WHERE token=?";
        
        try (Connection connection = this.dataSource.getConnection()) {

            String playlist_id = DB.genId();

            PreparedStatement statementPL = connection.prepareStatement(sqlPL);
            statementPL.setString(1, playlist_id);
            statementPL.setString(2, name);
            int resultPL = statementPL.executeUpdate();

            PreparedStatement statementPLM = connection.prepareStatement(sqlPLM);
            statementPLM.setString(1, playlist_id);
            statementPLM.setString(2, token);
            int resultPLM = statementPLM.executeUpdate();

            if ( resultPL != 1 || resultPLM != 1) return null;

            return this.getPL(connection, token);

        } catch ( SQLException e ) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public ArrayList<Playlist> editPlaylist(String token, String playlist_id, String playlist_name) {

        // TODO Validate name

        String sql = "UPDATE Playlists "
                    + sqlUserJoin
                    + "SET name = ? "
                    + "WHERE Playlists.id = ? "
                    + "AND Users.token = ?";

        try ( Connection connection = this.dataSource.getConnection() ) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, playlist_name);
            statement.setString(2, playlist_id);
            statement.setString(3, token);
            int result = statement.executeUpdate();

            if ( result != 1 ) return null;
            
            return this.getPL(connection, token);

        } catch ( SQLException e ) {
            e.printStackTrace();
        }

        return null;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
}
