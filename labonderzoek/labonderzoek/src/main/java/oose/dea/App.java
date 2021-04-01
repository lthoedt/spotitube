package oose.dea;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.json.*;

public class App {

    private App() {
    }

    public static void main(String[] args) {

        // Connecting
        try (Connection con = DriverManager.getConnection("jdbc:neo4j:bolt://localhost/?database=spotitube", "neo4j", "password")) {

            // Querying
            String queryInsert = "CREATE (user:Users {username:\"henk\", password: \"henk\", token: \"1234-1234-1234\"} ) "
                                + "-[:owns]-> (playlist:Playlists {name: \"SRV\"}) "
                                + "RETURN user, playlist";
            PreparedStatement stmt = con.prepareStatement(queryInsert);
            stmt.executeUpdate();

            String queryAddPlaylist = "MATCH (user:Users) "
                                    + "WHERE user.username='henk' "
                                    + "CREATE (user)-[:owns]->(playlist:Playlists {name: \"kaas\"}) "
                                    + "RETURN user";
            PreparedStatement stmt2 = con.prepareStatement(queryAddPlaylist);
            stmt2.executeUpdate();

            for ( int i = 0; i < 10; i++ ) {
                String sql = "MATCH (pl:Playlists) "
                            + "WHERE pl.name='kaas' "
                            + "CREATE (pl)-[:contains]->(track:Tracks {name: \"track" + i + "\"}) "
                            + "RETURN pl";
                PreparedStatement s = con.prepareStatement(sql);
                s.executeUpdate();
            }

            String queryRetrieve = "MATCH (user:Users)-[:owns]->(playlists:Playlists) "
                                    + "OPTIONAL MATCH (playlists)-[:contains]->(tracks:Tracks) "
                                    + "with user, playlists, collect(tracks) AS ts "
                                    + "with user, collect({name: playlists.name, tracks: ts}) AS playlists "
                                    + "WHERE user.username='henk' "
                                    + "RETURN user.username, {playlists: playlists} AS playlists";
            PreparedStatement stmtRetrieve = con.prepareStatement(queryRetrieve);
            ResultSet resultSet = stmtRetrieve.executeQuery();
            while (resultSet.next()) {
                System.out.println(resultSet.getString("user.username"));
                String jsonString = resultSet.getString("playlists");

                JSONArray playlists = new JSONObject(jsonString).getJSONArray("playlists");
                
                for ( int i = 0; i < playlists.length(); i++) {
                    JSONObject playlist = playlists.getJSONObject(i);
                    JSONArray tracks = playlist.getJSONArray("tracks");

                    System.out.println( playlist.getString("name") );
                    
                    for ( int j = 0; j < tracks.length(); j++) {
                        JSONObject track = tracks.getJSONObject(j);
                        System.out.println(track.getString("name"));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
