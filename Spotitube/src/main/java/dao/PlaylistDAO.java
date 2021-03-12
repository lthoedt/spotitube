package dao;

import javax.annotation.Resource;
import javax.sql.DataSource;

import domain.Playlist;
import domain.Track;

import java.sql.*;
import java.util.ArrayList;

import service.dto.request.PlaylistReqDTO;

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

                ArrayList<Track> tracks = new ArrayList<>();
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
    public ArrayList<Playlist> deletePlaylist(String token, int id) {
        // TODO Auto-generated method stub
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
    public ArrayList<Playlist> editPlaylist(String token, PlaylistReqDTO playlistReqDTO) {
        // TODO Auto-generated method stub
        return null;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
}
