import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.IPlaylistDAO;
import service.Playlist;
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
        int expectedPlaylistCount = 1;

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

}
