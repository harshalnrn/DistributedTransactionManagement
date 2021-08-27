package practise.jdbc_dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;
import practise.entity.UserEntity;
import practise.dto.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class User1Dao {
    Logger logger = LoggerFactory.getLogger(User1Dao.class);
    //decoupling transaction management from connection, and via transaction manager, which was brought it majorly for distributed transactions


    //PlatformTransactionManager
    //TransactionTemplate
    //TransactionStatus


    JdbcTemplate jdbcTemplate = null;
    @Qualifier("dataSource1TransactionManager")
    @Autowired
    PlatformTransactionManager transactionManager; //   JTA transaction manager corresponding to dataSource.

    @Qualifier("dataSource2")
    @Autowired
    DataSource dataSource;

    @PersistenceContext
    EntityManager entityManager;

    public TransactionStatus getTransaction() {
                /*
        note three prerequisites:
        PlatformTransactionManager(I)
                TransactionTemplate(C) // specifications of transaction present here
                TransactionStatus(I)*/

        //template design pattern
//below steps: we create a logical transaction via transaction-manager, which in inturn will take care of commit/rollback of 1/many physical db transactions
        //transaction template has transaction properties

        /*1*/

        /*2*/    //  transactionDefinition.setIsolationLevel();
        /*3*/   // transactionDefinition.setPropagationBehavior();
        /*4*/  // transactionDefinition.setTimeout();
        System.out.println(transactionManager);

        TransactionTemplate transactionDefinition = new TransactionTemplate();
        transactionDefinition.setName("transaction1");
        transactionDefinition.setTransactionManager(transactionManager);
        transactionDefinition.setIsolationLevel(TransactionDefinition.ISOLATION_DEFAULT);
        transactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        //transactionDefinition.setReadOnly(false);
        TransactionStatus status = transactionManager.getTransaction(transactionDefinition);


        return status;
    }






    /**
     * Spring jdbc(connection)   +native jdbc(crud)  + native jdbc(transaction management)
     *
     * @param userName
     * @return
     */
    public User getUserFromDbWithJdbc(String userName) {

        User user = new User();
        Connection connection = null;


        try {
            connection = dataSource.getConnection(); // getting new connection on the fly     // understand mechanism without connection pool and with server connection pool
            connection.setAutoCommit(false); // not set on pool level
            //do we not require to set auto commit as false here??

            PreparedStatement statement = connection.prepareStatement("select email, firstname, lastname from users where email=?");
            statement.setString(1, userName);
            ResultSet set = statement.executeQuery();

            while (set.next()) {
                user.setEmail(set.getString(1));
                user.setFirstName(set.getString(2));
                user.setLastName(set.getString(3));
            }


            //commit
            connection.commit();
            //rollback
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException e1) {
                System.out.println(e1.getMessage());
            }

        }
        return user;

    }

    /**
     * Spring jdbc(connection) + Spring jdbc(crud) + Spring transactions(JTA implementation)
     *
     * @param email
     * @return
     */
    public boolean updateUserEmail(String email) {
        User user = new User();

        TransactionStatus transaction = null;
        boolean result = false;


        try {
//DOUBT: not sure, whether this is getting auto-committed or happening as per our transaction control.


            transaction = getTransaction(); // doBegin() get called internally, where connection object created and autocommit of corresponding connection object set to false, and transaction started

            System.out.println("transaction status:" + transaction.isNewTransaction());

            String updateSql = "update users set firstname=? where email=?";

            jdbcTemplate.setDataSource(dataSource);
            //question: can jdbctemplate be set for a specific connection object, than datasource
            // pass the parameter values
            jdbcTemplate.update(updateSql, "harshal6", email);
            transactionManager.commit(transaction); //doCommit gets called internally which inturn calls connection.commit()

        } catch (Exception e) {
            logger.error("error before commiting" + e.getMessage());
            transactionManager.rollback(transaction); //doRollback gets called internally, which internnaly calls connection.rollback()
        }

        return true;
    }


    /**
     * spring jdbc(connection) + JPA(Entity-Manager) (crud) + Spring transactions (trans mgt)
     *
     * @param userName
     * @return
     */
    public boolean updateUser(String userName) {
        User user = new User();

        TransactionStatus transaction = null;
        boolean flag;
        try {
            transaction = getTransaction();
            System.out.println("transaction status:" + transaction.isNewTransaction());

            TypedQuery<UserEntity> query = entityManager.createNamedQuery("findByUsername", UserEntity.class);
            query.setParameter("userByUserName", userName);
            UserEntity updatedEntity = query.getSingleResult();
            updatedEntity.setEmail("harshal8");
            entityManager.persist(updatedEntity);
            transactionManager.commit(transaction);
            flag = true;
        } catch (Exception e) {
            transactionManager.rollback(transaction);
            flag = false;
        }
        return flag;
    }
}
