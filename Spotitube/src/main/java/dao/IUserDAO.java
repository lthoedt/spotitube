package dao;

import domain.User;
import exceptions.UserNotFoundException;

public interface IUserDAO {
    User loginUser(String username, String password) throws UserNotFoundException;
    User createUser(String username, String password);
    String generateToken();
}
