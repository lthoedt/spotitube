package service.dto.request;

import service.dto.response.TrackDTO;

public class PlaylistReqDTO {
    public int id;
    public String name;
    public Boolean owner;
    public TrackDTO[] tracks;
}
