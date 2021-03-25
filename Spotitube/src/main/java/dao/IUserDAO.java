package dao;

import javax.sql.DataSource;

import domain.User;


// eventueel zou deze class een abstract class kunnen worden om generateToken() een body te geven.
public interface IUserDAO {
    User loginUser(String username, String password);
    User createUser(String username, String password);
    public void setDataSource(DataSource dataSource);
    String generateToken();
}
