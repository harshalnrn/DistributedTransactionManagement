package practise;
// This is about distributed transaction system in a springboot application, where we deal with multiple databases, and understand under the hood implementation of libraries like: native jdbc, spring-jdbc, JPA/Spring-data-JPA, JTA/Spring-transactions for connection/crud/transaction mgt.
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
// be very careful on where you place the main class.

//disabling auto configuration(datasource, entityManager, PlatformTransactionManager, where we manualy establish connection

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,
       DataSourceTransactionManagerAutoConfiguration.class})
        //HibernateJpaAutoConfiguration.class*/})

      //if using spring data jpa starters, Spring boot app requires JPA relevent beans to be available during app start.
// Hence if you not supplying them manually, then disabling them would throw app start errors
public class Main /*extends SpringBootServletInitializer*/ {
 /*   @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Main.class);
    }
*/
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
