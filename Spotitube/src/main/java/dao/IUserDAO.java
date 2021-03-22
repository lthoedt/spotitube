package dao;

import javax.sql.DataSource;

import domain.User;

public interface IUserDAO {
    User loginUser(String username, String password);
    User createUser(String username, String password);
    public void setDataSource(DataSource dataSource);
    String generateToken();
}
