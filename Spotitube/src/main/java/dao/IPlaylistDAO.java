package dao;

import java.util.ArrayList;

import domain.Playlist;
import service.dto.response.PlaylistDTO;

public interface IPlaylistDAO {
    public ArrayList<Playlist> getPlaylists( String token );
    public ArrayList<Playlist> deletePlaylist( String token, String playlist_id );
    public ArrayList<Playlist> addPlaylist( String token, String name );
    public ArrayList<Playlist> editPlaylist( String token, String playlist_id, String playlist_name );
    public boolean ownsPlaylist( String token, String playlist_id );
}
