package domain;

public class Video extends Track {
    private String publication_date;
    private String description;
    private int playcount;

    public Video(String id) {
        super(id);
    }

    @Override
    public String getPublicationDate() {
        return this.publication_date;
    }

    @Override
    public void setPublicationDate(String publication_date) {
        this.publication_date = publication_date;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int getPlaycount() {
        return this.playcount;
    }
    
    @Override
    public void setPlaycount(int playcount) {
        this.playcount = playcount;
    }
}
