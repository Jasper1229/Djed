package jjs.djed.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class Database {
    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/appdb");
        config.setUsername("appuser");
        config.setPassword("apppass");

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);              // Keeps 2 idle connections warm in the pool
        config.setIdleTimeout(30000);          // Max time an idle connection can stay in pool (30s)
        config.setConnectionTimeout(30000);    // Max time to wait for a connection before throwing an error (30s)
        config.setMaxLifetime(1800000);        // Max lifetime of a connection in pool (30 minutes)

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static HikariDataSource getDataSource() {
        return dataSource;
    }

    public static void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}