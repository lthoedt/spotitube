package dao;

import java.util.ArrayList;

import domain.Playlist;
import service.dto.request.PlaylistReqDTO;

public interface IPlaylistDAO {
    public ArrayList<Playlist> getPlaylists( String token );
    public ArrayList<Playlist> deletePlaylist( String token, int id );
    public ArrayList<Playlist> addPlaylist( String token );
    public ArrayList<Playlist> editPlaylist( String token, PlaylistReqDTO playlistReqDTO );
}
