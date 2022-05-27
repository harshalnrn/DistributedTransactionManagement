package practise.jdbc_dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import practise.dto.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository

public class UserDao {

    Logger logger = LoggerFactory.getLogger(UserDao.class); //slf4j
    @Autowired
    @Qualifier("dataSource1")
    private DataSource dataSource;




    public User getUserFromDbWithJdbc(String userName) {
        // validate which jdbc pool is being used.

        //Spring boot <2 : default jdbc pool mechanism: tomcat
        //            2+ : default jdbc pool mechanism: Hikari CP
        System.out.println(dataSource.getClass().getName());


        User user = new User();
        Connection connection = null;
        try {
            connection = dataSource.getConnection(); // getting connection from jdbc pool
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement("select email, first_name, last_name from proman.users where email=?");
            statement.setString(1, userName);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                user.setEmail(set.getString(1));
                user.setFirstName(set.getString(2));
                user.setLastName(set.getString(3));
            }
            connection.commit();
        } catch (SQLException e) {
logger.error("exception while commiting"+e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException e1) {
logger.error("exception while rolling back"+e1.getMessage());
            }
        } finally {
            try {
                connection.close();
            } catch (SQLException e2) {

            }
        }
        return user;
    }

    //replace above piece of code with try-with resource, this making it more concise and small.

}
