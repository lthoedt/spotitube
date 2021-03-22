package service.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import domain.User;

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
}
