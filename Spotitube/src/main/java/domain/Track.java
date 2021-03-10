package domain;

public abstract class Track {
    private int id;
    private String performer;
    private String title;
    private String url;
    private int duration;
    private Boolean offline_available;

    public Track( int id ) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public String getPerformer() {
        return this.performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Boolean getOfflineAvailable() {
        return this.offline_available;
    }

    public void setOfflineAvailable(Boolean offline_available) {
        this.offline_available = offline_available;
    }

    public String getPublicationDate(){return null;};
    public void setPublicationDate(String publication_date){};

    public String getDescription(){return null;};
    public void setDescription(String description){};

    public int getPlaycount() {return -1;};
    public void setPlaycount(int playcount) {};

    public Album getAlbum(){return null;};
    public void setAlbum(Album album){};

}
