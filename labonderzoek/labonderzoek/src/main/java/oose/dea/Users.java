package oose.dea;

public class Users extends IEntity {

    public void setUsername( String username ) {
        properties.put("username", username);
    }

    public void setPassword( String password ) {
        properties.put("password", password);
    }

    public void setToken( String token ) {
        properties.put("token", token);
    }

    // public void setPlaylist( IEntity playlist ) {
    //     this.relations.put("owns", playlist);
    // }

    public static String buildRelationPlaylist() {
        return "-[:owns]->";
    }

    @Override
    public String entity_name() {
        return "Users";
    }
}
