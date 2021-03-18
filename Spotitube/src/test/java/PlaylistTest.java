import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.IPlaylistDAO;
import exceptions.NotOwnerException;
import service.Playlist;
import service.dto.request.PlaylistReqDTO;
import service.dto.response.PlaylistDTO;
import service.dto.response.PlaylistsDTO;
import tests.Utils;

public class PlaylistTest {

    private Playlist playlist;
    private String testToken = "1425-2565-5487";
    private String testTokenWrong = "1";

    @BeforeEach
    public void setup() {
        this.playlist = new Playlist();
    }

    @Test
    public void getplayListTestRegular() {
        int expectedStatus = 200;

        ArrayList<domain.Playlist> playlists = new ArrayList<>();

        domain.Playlist playlist = Utils.getSamplePlaylist();
        playlists.add(playlist);

        // Act
        IPlaylistDAO playlistDAO = mock(IPlaylistDAO.class);
        when(playlistDAO.getPlaylists(testToken)).thenReturn(playlists);
        this.playlist.setPlaylistDAO(playlistDAO);
        
        Response response = this.playlist.getPlaylist(testToken);

        // Assert
        assertEquals(expectedStatus, response.getStatus());
    }

    @Test
    public void addPlaylistTestRegular() {
        int expectedStatus = 200;

        // create playlists
        ArrayList<domain.Playlist> playlists = new ArrayList<>();
        // add sample playlist
        domain.Playlist playlist = Utils.getSamplePlaylist();
        playlists.add(playlist);

        // get sample playlistDTO
        PlaylistReqDTO playlistReqDTOToTest = Utils.getSamplePlaylistReqDTO();

        // convert to domain and add to playlists
        playlists.add(Utils.convertPlaylistDTOToPlaylist(playlistReqDTOToTest));

        // Act
        IPlaylistDAO playlistDAO = mock(IPlaylistDAO.class);
        when(playlistDAO.addPlaylist(testToken, playlistReqDTOToTest.name)).thenReturn(playlists);
        this.playlist.setPlaylistDAO(playlistDAO);
        
        Response response = this.playlist.addPlaylist(testToken, playlistReqDTOToTest);
        PlaylistsDTO playlistsDTO = (PlaylistsDTO) response.getEntity(); 

        // Assert
        assertEquals(expectedStatus, response.getStatus());
        assertEquals(playlistReqDTOToTest.name, playlistsDTO.playlists.get(1).name);
    }

    @Test
    public void deletePlaylistTestRegular() {
        int expectedStatus = 200;
        int expectedLength = 0;
        String playlist_id_to_test = "5NOVKXx5r_66y42IIK61th-PT9hU6C4hts";

        // create playlists
        ArrayList<domain.Playlist> playlists = new ArrayList<>();
        // add sample playlist
        domain.Playlist playlist = Utils.getSamplePlaylist();
        playlists.add(playlist);

        // Act
        IPlaylistDAO playlistDAO = mock(IPlaylistDAO.class);
        when(playlistDAO.deletePlaylist(testToken, playlist_id_to_test)).thenReturn(new ArrayList<domain.Playlist>());
        this.playlist.setPlaylistDAO(playlistDAO);
        
        Response response = null;
        try {
            response = this.playlist.deletePlaylist(testToken, playlist_id_to_test);
        } catch (NotOwnerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        PlaylistsDTO playlistsDTO = (PlaylistsDTO) response.getEntity(); 

        // Assert
        assertEquals(expectedStatus, response.getStatus());
        assertEquals(expectedLength, playlistsDTO.playlists.size());
    }

    @Test
    public void deletePlaylistTestNotOwner() {
        int expectedStatus = 403;
        String playlist_id_to_test = "5NOVKXx5r_66y42IIK61th-PT9hU6C4hts";

        // create playlists
        ArrayList<domain.Playlist> playlists = new ArrayList<>();
        // add sample playlist
        domain.Playlist playlist = Utils.getSamplePlaylist();
        playlists.add(playlist);

        // Act
        IPlaylistDAO playlistDAO = mock(IPlaylistDAO.class);
        when(playlistDAO.deletePlaylist(testTokenWrong, playlist_id_to_test)).thenReturn(null);
        this.playlist.setPlaylistDAO(playlistDAO);
        
        // Assert
        assertThrows(NotOwnerException.class, () -> {
            Response response = this.playlist.deletePlaylist(testTokenWrong, playlist_id_to_test);
            assertEquals(expectedStatus, response.getStatus());
        });

    }

    @Test
    public void editPlaylistTestRegular() {
        int expectedStatus = 200;
        String expectedName = "henk";
        String playlist_id_to_test = "5NOVKXx5r_66y42IIK61th-PT9hU6C4hts";

        PlaylistDTO playlistDTOToTest = Utils.getSamplePlaylistDTO(expectedName);

        // create playlists
        ArrayList<domain.Playlist> playlists = new ArrayList<>();
        // add sample playlist
        domain.Playlist playlist = Utils.getSamplePlaylist();
        playlist.setName(expectedName);
        playlists.add(playlist);

        // Act
        IPlaylistDAO playlistDAO = mock(IPlaylistDAO.class);
        when(playlistDAO.editPlaylist(testToken, playlist_id_to_test, expectedName)).thenReturn(playlists);
        this.playlist.setPlaylistDAO(playlistDAO);
        
        Response response = null;
        try {
            response = this.playlist.editPlaylist(testToken, playlistDTOToTest);
        } catch (NotOwnerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        PlaylistsDTO playlistsDTO = (PlaylistsDTO) response.getEntity(); 

        // Assert
        assertEquals(expectedStatus, response.getStatus());
        assertEquals(expectedName, playlistsDTO.playlists.get(0).name);
    }

}
