import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.IPlaylistDAO;
import service.Playlist;
import service.dto.request.PlaylistReqDTO;
import service.dto.response.PlaylistsDTO;
import utils.TestUtils;

public class PlaylistTest {

    private Playlist playlist;
    private String testToken = "1425-2565-5487";

    @BeforeEach
    public void setup() {
        this.playlist = new Playlist();
    }

    @Test
    public void getplayListTestRegular() {
        int expectedStatus = 200;

        ArrayList<domain.Playlist> playlists = new ArrayList<>();

        domain.Playlist playlist = TestUtils.getSamplePlaylist();
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
        domain.Playlist playlist = TestUtils.getSamplePlaylist();
        playlists.add(playlist);

        // get sample playlistDTO
        PlaylistReqDTO playlistReqDTOToTest = TestUtils.getSamplePlaylistReqDTO();

        // convert to domain and add to playlists
        playlists.add(TestUtils.convertPlaylistDTOToPlaylist(playlistReqDTOToTest));

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
        domain.Playlist playlist = TestUtils.getSamplePlaylist();
        playlists.add(playlist);

        // Act
        IPlaylistDAO playlistDAO = mock(IPlaylistDAO.class);
        when(playlistDAO.deletePlaylist(testToken, playlist_id_to_test)).thenReturn(new ArrayList<domain.Playlist>());
        this.playlist.setPlaylistDAO(playlistDAO);
        
        Response response = this.playlist.deletePlaylist(testToken, playlist_id_to_test);
        PlaylistsDTO playlistsDTO = (PlaylistsDTO) response.getEntity(); 

        // Assert
        assertEquals(expectedStatus, response.getStatus());
        assertEquals(expectedLength, playlistsDTO.playlists.size());
    }

}
