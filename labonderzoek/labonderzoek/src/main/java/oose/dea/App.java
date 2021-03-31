package oose.dea;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class App {

    private App() {
    }

    public static void main(String[] args) {

        // Connecting
        try (Connection con = DriverManager.getConnection("jdbc:neo4j:bolt://localhost/?database=spotitube", "neo4j", "password")) {

            Users user = new Users();
            user.setUsername("henk");;
            user.setPassword("henk");
            user.setToken("1234-1234-1234");

            Playlists playlist = new Playlists();
            playlist.setName("SRV");

            // user.setPlaylist(playlist);

            // Querying
            String queryInsert = "CREATE " + user.build()
                        + Users.buildRelationPlaylist() + playlist.build()
                        + "RETURN users, playlists";
            PreparedStatement stmt = con.prepareStatement(queryInsert);
            stmt.executeUpdate();

            Playlists playlist2 = new Playlists();
            playlist2.setName("kaas");

            String queryAddPlaylist = "MATCH (user:Users) x"
                                    + "WHERE user.username='henk' "
                                    + "CREATE (user)" + Users.buildRelationPlaylist() + playlist2.build()
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

            String queryRetrieve = "MATCH (user:Users)" + Users.buildRelationPlaylist() + "(playlists:Playlists) "
                                    + "WHERE user.username='henk' "
                                    + "RETURN user.username, playlists.name";
            PreparedStatement stmtRetrieve = con.prepareStatement(queryRetrieve);
            ResultSet resultSet = stmtRetrieve.executeQuery();
            while (resultSet.next()) {
                System.out.println(resultSet.getString("user.username"));
                    System.out.println(resultSet.getString("playlists.name"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
