package oose.dea;

public class Playlists extends IEntity {

    public void setName( String name ) {
        properties.put("name", name);
    }

    @Override
    public String entity_name() {
        return "Playlists";
    }
}
