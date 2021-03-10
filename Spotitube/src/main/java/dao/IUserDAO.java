package dao;

import domain.User;

public interface IUserDAO {
    User loginUser(String username, String password);
    User createUser(String username, String password);
    String generateToken();
}
