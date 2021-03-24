package tests.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.IPlaylistDAO;
import dao.ITrackDAO;
import domain.Track;
import exceptions.InvalidTokenException;
import exceptions.NotOwnerException;
import service.Playlist;
import service.dto.request.PlaylistReqDTO;
import service.dto.request.TrackReqDTO;
import service.dto.response.PlaylistDTO;
import service.dto.response.PlaylistsDTO;
import tests.Utils;

public class PlaylistTest {

    private Playlist playlist;
    private String testToken = "1425-2565-5487";
    private String testTokenWrong = "1";
    String playlist_id_to_test = "5NOVKXx5r_66y42IIK61th-PT9hU6C4hts";

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
        try {
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
        } catch (InvalidTokenException e) {
            fail();
        }
    }

    @Test
    public void addPlaylistTestInvalidToken() {
        int expectedStatus = 403;

        // get sample playlistDTO
        PlaylistReqDTO playlistReqDTOToTest = Utils.getSamplePlaylistReqDTO();

        // Act
        IPlaylistDAO playlistDAO = mock(IPlaylistDAO.class);
        when(playlistDAO.addPlaylist(testToken, playlistReqDTOToTest.name)).thenReturn(null);
        this.playlist.setPlaylistDAO(playlistDAO);

        assertThrows(InvalidTokenException.class, () -> {
            Response response = this.playlist.addPlaylist(testToken, playlistReqDTOToTest);
            assertEquals(expectedStatus, response.getStatus());
        });            
    }

    @Test
    public void deletePlaylistTestRegular() {
        int expectedStatus = 200;
        int expectedLength = 0;

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
            e.printStackTrace();
        }
        PlaylistsDTO playlistsDTO = (PlaylistsDTO) response.getEntity(); 

        // Assert
        assertEquals(expectedStatus, response.getStatus());
        assertEquals(expectedName, playlistsDTO.playlists.get(0).name);
    }

    @Test
    public void editPlaylistTestNotOwner() {
        int expectedStatus = 403;
        String expectedName = "henk";

        PlaylistDTO playlistDTOToTest = Utils.getSamplePlaylistDTO(expectedName);

        // Act
        IPlaylistDAO playlistDAO = mock(IPlaylistDAO.class);
        when(playlistDAO.editPlaylist(testTokenWrong, playlist_id_to_test, expectedName)).thenReturn(null);
        this.playlist.setPlaylistDAO(playlistDAO);
        
        // Assert
        assertThrows(NotOwnerException.class, () -> {
            Response response = this.playlist.editPlaylist(testTokenWrong, playlistDTOToTest);
            assertEquals(expectedStatus, response.getStatus());
        });
    }

    @Test
    public void getTracksFromPlaylistTest() {        
        // Arrange
        int expectedStatus = 200;

        // Act
        ITrackDAO trackDAO = mock(ITrackDAO.class);
        when(trackDAO.getTracksFromPlaylist(testToken, playlist_id_to_test)).thenReturn(Utils.getSamplePlaylist().getTracks());
        this.playlist.setTrackDAO(trackDAO);
        
        Response response = this.playlist.getTracks(testToken, playlist_id_to_test);
        
        // Assert
        assertEquals(expectedStatus, response.getStatus());
    }

    @Test
    public void addTrackToPlaylistTestRegular() {
        // Arrange
        int expectedStatus = 201;

        TrackReqDTO trackReqDTO = Utils.getSampleTrackReqDTO();

        // Act
        ITrackDAO trackDAO = mock(ITrackDAO.class);
        when(trackDAO.addTrackToPlaylist(testToken, playlist_id_to_test, trackReqDTO.id, trackReqDTO.offlineAvailable)).thenReturn(new ArrayList<Track>());
        this.playlist.setTrackDAO(trackDAO);
        
        Response response = null;
        try {
            response = this.playlist.addTrack(testToken, playlist_id_to_test, trackReqDTO);
        } catch (NotOwnerException e) {
            e.printStackTrace();
        }
        
        // Assert
        assertEquals(expectedStatus, response.getStatus());
    }

    @Test
    public void addTrackToPlaylistTestNotOwner() {
        // Arrange
        int expectedStatus = 403;

        TrackReqDTO trackReqDTO = Utils.getSampleTrackReqDTO();

        // Act
        ITrackDAO trackDAO = mock(ITrackDAO.class);
        when(trackDAO.addTrackToPlaylist("1", playlist_id_to_test, trackReqDTO.id, trackReqDTO.offlineAvailable)).thenReturn(null);
        this.playlist.setTrackDAO(trackDAO);
        
        // Assert
        assertThrows(NotOwnerException.class, () -> {
            Response response = this.playlist.addTrack("1", playlist_id_to_test, trackReqDTO);
            assertEquals(expectedStatus, response.getStatus());
        });
    }

    @Test
    public void deleteTrackFromPlaylistTestRegular() {
        // Arrange
        int expectedStatus = 200;
        String track_id_to_test = "1";

        // Act
        ITrackDAO trackDAO = mock(ITrackDAO.class);
        when(trackDAO.deleteTrackFromPlaylist(testToken, playlist_id_to_test, track_id_to_test)).thenReturn(new ArrayList<Track>());
        this.playlist.setTrackDAO(trackDAO);

        Response response = null;
        try {
            response = this.playlist.deleteTrack(testToken, playlist_id_to_test, track_id_to_test);
        } catch (NotOwnerException e) {
            e.printStackTrace();
        }

        // Assert
        assertEquals(expectedStatus, response.getStatus());
    }

    @Test
    public void deleteTrackFromPlaylistTestNotOwner() {
        // Arrange
        int expectedStatus = 403;
        String track_id_to_test = "1";
        String wrong_token = "1";

        // Act
        ITrackDAO trackDAO = mock(ITrackDAO.class);
        when(trackDAO.deleteTrackFromPlaylist(wrong_token, playlist_id_to_test, track_id_to_test)).thenReturn(null);
        this.playlist.setTrackDAO(trackDAO);
        
        // Assert
        assertThrows(NotOwnerException.class, () -> {
            Response response = this.playlist.deleteTrack(wrong_token, playlist_id_to_test, track_id_to_test);
            assertEquals(expectedStatus, response.getStatus());
        });

    }

}
