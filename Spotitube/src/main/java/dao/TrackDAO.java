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

    @Override
    public ArrayList<Track> getTracks() {
        // String sql = "SELECT * FROM Tracks LEFT JOIN Videos ON Videos.track_id=Tracks.id LEFT JOIN Songs ON Songs.track_id=Tracks.id";
        String sqlVideo = "SELECT Tracks.id, Tracks.performer, Tracks.title, Tracks.url, Tracks.duration, Videos.publication_date, Videos.description, Videos.playcount FROM Videos INNER JOIN Tracks ON Videos.track_id=Tracks.id";
        String sqlSong  = "SELECT Tracks.id, Tracks.performer, Tracks.title, Tracks.url, Tracks.duration, Albums.name AS album_name FROM Songs INNER JOIN Tracks ON Songs.track_id=Tracks.id LEFT JOIN Albums ON Songs.album_id=Albums.id";

        try {
            Connection connection = this.dataSource.getConnection();
            PreparedStatement statementVideo = connection.prepareStatement(sqlVideo);
            ResultSet resultSetVideo = statementVideo.executeQuery();

            PreparedStatement statementSong = connection.prepareStatement(sqlSong);
            ResultSet resultSetSong = statementSong.executeQuery();

            ArrayList<Track> tracks = new ArrayList<>();

            // TODO Change this garbage to not be done twice
            while( resultSetVideo.next() ) {
                Track track = new Video(resultSetVideo.getInt("id"));
                track.setPerformer(resultSetVideo.getString("performer"));
                track.setTitle    (resultSetVideo.getString("title"));
                track.setUrl(resultSetVideo.getString("url"));
                track.setDuration(resultSetVideo.getInt("duration"));
                
                track.setPublicationDate(resultSetVideo.getString("publication_date"));
                track.setDescription(resultSetVideo.getString("description"));
                track.setPlaycount(resultSetVideo.getInt("playcount"));

                tracks.add(track);
            }

            while( resultSetSong.next() ) {
                Track track = new Song(resultSetSong.getInt("id"));
                track.setPerformer(resultSetSong.getString("performer"));
                track.setTitle    (resultSetSong.getString("title"));
                track.setUrl(resultSetSong.getString("url"));
                track.setDuration(resultSetSong.getInt("duration"));
                
                Album album = new Album(resultSetSong.getString("album_name"));
                track.setAlbum(album);

                tracks.add(track);
            }

            return tracks;
            
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<Track> getTracksFromPlaylist(int playlist_id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ArrayList<Track> addTrackFromPlaylist(int playlist_id, int track_id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ArrayList<Track> deleteTrackFromPlaylist(int playlist_id, int track_id) {
        // TODO Auto-generated method stub
        return null;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
