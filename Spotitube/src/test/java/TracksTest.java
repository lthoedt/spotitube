import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.ITrackDAO;
import domain.Track;
import service.Tracks;
import tests.Utils;

public class TracksTest {

    private Tracks Tracks;

    @BeforeEach
    public void setup() {
        this.Tracks = new Tracks();
    }
    
    @Test
    public void getTracksRegularTest() {
        // Arrange
        String testToken = "1425-2565-5487";
        String testForPlaylist = "RtUtzbPwzN1rds0qEGtSvsmcvtIT3Rpxg0";
        int expectedStatus = 200;

        ArrayList<Track> tracks = new ArrayList<>();

        Track track = Utils.getSampleTrack();
        tracks.add(track);


        // Act
        ITrackDAO trackDAOMock = mock(ITrackDAO.class);
        when(trackDAOMock.getTracks(testToken, testForPlaylist)).thenReturn(tracks);
        this.Tracks.setTrackDAO(trackDAOMock);

        // Assert
        Response response = this.Tracks.getTracks(testToken, testForPlaylist);

        assertEquals(response.getStatus(), expectedStatus);
    }
}
