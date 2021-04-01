package service.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Random;

public class DB {
    public static String genId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-";
        int length = 34;

        String code = "";

        for (int i = 0; i < length; i++) {
            int index = new Random().nextInt(chars.length());
            code += chars.charAt(index);
        }

        return code;
    }

    public static Connection getNoSQLURL() throws SQLException {
        try {
            Class.forName("org.neo4j.jdbc.bolt.BoltDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection("jdbc:neo4j:bolt://localhost/?database=spotitube", "neo4j", "password");
    }
}
