package domain;

import java.util.ArrayList;

public class Playlist {
    private String id;
    private String name;
    private Boolean owner;
    private ArrayList<Track> tracks;
    public int duration;

    public String getId() {
        return this.id;
    }

    public void setId(String id ) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public Boolean getOwner() {
        return this.owner;
    }

    public void setOwner( Boolean owner ) {
        this.owner = owner;
    }

    public ArrayList<Track> getTracks() {
        return this.tracks;
    }

    public void setTracks( ArrayList<Track> tracks2 ) {
        this.tracks=tracks2;
    }

    public int getDuration() {
        int duration = 0;

        for ( Track track : this.tracks ) {
            duration += track.getDuration();
        }
        
        return duration;
    }
}
