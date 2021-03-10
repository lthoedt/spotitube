package domain;

public class Song extends Track {
    private Album album;

    public Song(int id) {
        super(id);
    }

    public Album getAlbum(){
        return this.album;
    };

    public void setAlbum(Album album){
        this.album = album;
    };
}
