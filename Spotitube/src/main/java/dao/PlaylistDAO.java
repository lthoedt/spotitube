package dao;

import javax.annotation.Resource;
import javax.sql.DataSource;

import domain.Playlist;
import domain.Track;

import java.sql.*;
import java.util.ArrayList;

import service.dto.response.PlaylistDTO;
import service.utils.DB;

public class PlaylistDAO implements IPlaylistDAO {

    @Resource(name = "jdbc/spotitube")
    DataSource dataSource;

    @Override
    public ArrayList<Playlist> getPlaylists(String token) {
        String sql = "SELECT Playlists.id, Playlists.name, PlaylistMappers.owner "
                        + "FROM PlaylistMappers "
                        + "INNER JOIN Playlists ON PlaylistMappers.playlist_id=Playlists.id "
                        + "INNER JOIN Users ON PlaylistMappers.user_id=Users.id "
                        + "WHERE Users.token=?";

        try ( Connection connection = this.dataSource.getConnection() ) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, token);

            ResultSet resultSet = statement.executeQuery();

            ArrayList<Playlist> playlists = new ArrayList<>();

            while ( resultSet.next() ) {
                Playlist playlist = new Playlist();

                playlist.setId(resultSet.getString("id"));
                playlist.setName(resultSet.getString("name"));
                playlist.setOwner(resultSet.getBoolean("owner"));

                // Misschien is dit niet mooi maar duration moet uit tracks gehaald worden
                TrackDAO trackDAO = new TrackDAO();
                trackDAO.setDataSource(this.dataSource);
                ArrayList<Track> tracks = trackDAO.getTracksFromPlaylist( token, playlist.getId() );
                
                playlist.setTracks(tracks);

                playlists.add(playlist);
            }

            return playlists;

        } catch ( SQLException e ) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public ArrayList<Playlist> deletePlaylist(String token, String playlist_id) {

        boolean owns = this.ownsPlaylist(token, playlist_id);

        if (!owns) return null;

        String sql = "DELETE FROM Playlists WHERE id=?";

        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, playlist_id);
            int result = statement.executeUpdate();

            if (result != 1 ) return null;

            return this.getPlaylists(token);

        } catch ( SQLException e ) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public ArrayList<Playlist> addPlaylist(String token, String name) {
        String sqlPL = "INSERT INTO Playlists ( id, name ) VALUES ( ?, ? )";
        String sqlPLM = "INSERT INTO PlaylistMappers ( playlist_id, user_id, owner ) VALUES ( ?, ?, 1 )";

        String playlist_id = DB.genId();
        int user_id = DB.getUser(this.dataSource, token).getId();

        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement statementPL = connection.prepareStatement(sqlPL);
            statementPL.setString(1, playlist_id);
            statementPL.setString(2, name);
            int resultPL = statementPL.executeUpdate();

            PreparedStatement statementPLM = connection.prepareStatement(sqlPLM);
            statementPLM.setString(1, playlist_id);
            statementPLM.setInt(2, user_id);
            int resultPLM = statementPLM.executeUpdate();

            // TODO Error handling
            if ( resultPL != 1 || resultPLM != 1) return null;

            return this.getPlaylists(token);

        } catch ( SQLException e ) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public ArrayList<Playlist> editPlaylist(String token, String playlist_id, String playlist_name) {

        boolean owns = this.ownsPlaylist(token, playlist_id);

        if (!owns) return null;

        // TODO Validate name

        String sql = "UPDATE Playlists SET name = ? WHERE id = ?";

        try ( Connection connection = this.dataSource.getConnection() ) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, playlist_name);
            statement.setString(2, playlist_id);
            int result = statement.executeUpdate();

            // TODO
            if ( result != 1 ) return null;
            
            return this.getPlaylists(token);
        } catch ( SQLException e ) {
            // TODO
            e.printStackTrace();
        }

        return null;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
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
