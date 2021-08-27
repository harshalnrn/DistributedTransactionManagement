

package practise.autoconfiguration_config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
public class DbJdbcConfig {

//using Spring jdbc api


    // Question: cant we configure autocommit on pool level for spring.jdbc like we do for tomcat/hikari, instead of doing it for each connection
    //factory method.
    @Bean("dataSource1")
    @Primary
    public DataSource configureDataSource1() {
        //Spring jdbc implementation of DataSource. (Question: why not use hikaricp/ tomcat here while explictly populating) ?
        // note here have explicit dataSource implementation, where we have used Spring jdbc implementation of DataSource (i..e overriding default (i.e tomcat/hikari))
        //or you can also use DataSourceBuilder pf spring jdbc which also provides implementation of DataSource interface.

        // here auto-commit is not a property of dataSource implementation DriverManagerDataSource class, to be able to set.
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();   // org.springframework.jdbc  //implementation of DataSource   // DataSource vs DriverManager
        driverManagerDataSource.setDriverClassName("org.postgresql.Driver");
        driverManagerDataSource.setUrl("jdbc:postgresql://localhost:5432/pmaddb");
        driverManagerDataSource.setUsername("postgres");
        driverManagerDataSource.setPassword("Kvihar@24");
        driverManagerDataSource.setSchema("proman");

        //for all connection objects created by this datasource: i want to set auto-commit as false. how to do that??
        //question: how to also explicitly set jdbc pool properties here.
        return driverManagerDataSource;
    }

    @Bean("dataSource2")

    public DataSource configureDataSource2() {

        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setDriverClassName("org.postgresql.Driver");
        driverManagerDataSource.setUrl("jdbc:postgresql://localhost:5432/quora");
        driverManagerDataSource.setUsername("postgres");
        driverManagerDataSource.setPassword("Kvihar@24");


        //by default public schema
        return driverManagerDataSource;
    }

    @Bean("dataSource3")
    public DataSource configureDataSource3() {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setDriverClassName("org.postgresql.Driver");
        driverManagerDataSource.setUrl("jdbc:postgresql://localhost:5432/Shopping");
        driverManagerDataSource.setUsername("postgres");
        driverManagerDataSource.setPassword("Kvihar@24");
        //by default public schema
        return driverManagerDataSource;
    }

    //enable this to demo distrbuted transaction, else disable, where you take single datasource configurations from application.properties

    @Bean("dataSource1TransactionManager")
    public PlatformTransactionManager configureJdbcTransactionManager() {
        //note different implementations of PlatformTransactionManager.   (ex: JpaTransations/ HibernateTransactionManager)
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(configureDataSource2()); // setting DataSource object
        return dataSourceTransactionManager;
    }

      @Bean
    public EntityManagerFactory getEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean enfb = new LocalContainerEntityManagerFactoryBean();
        enfb.setPersistenceXmlLocation("classpath:META-INF/jpa_hibernate_persistance.xml");
        enfb.afterPropertiesSet();
        return enfb.getObject();
    }


    // Note:  in case of distributed transactions : each datasource shall need above 3 explicit bean configurations and appropriate qualifer.
}


//Question: in case of distributed transction involving multiple dataSource : can this be done with single transaction-manager, or each data-source needs a transaction manager??
