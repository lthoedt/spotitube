package service.dto.request;

import service.dto.response.TrackDTO;

public class PlaylistReqDTO {
    public String id;
    public String name;
    public Boolean owner;
    public TrackDTO[] tracks;
}
