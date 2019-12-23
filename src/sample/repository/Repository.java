package sample.repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class Repository {
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    //TODO Wniosek - fajne to
    static {
        config.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
        config.setJdbcUrl("jdbc:hsqldb:file:lib/db/baza");
        config.setUsername("admin");
        config.setPassword("admin");
        config.setMaximumPoolSize(1);
        ds = new HikariDataSource(config);
    }

    private Repository() {}

        public static Connection getConnection() throws SQLException{
                return ds.getConnection();
        }
}