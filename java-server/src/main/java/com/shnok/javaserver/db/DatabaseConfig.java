package com.shnok.javaserver.db;

import com.shnok.javaserver.db.entity.*;
import com.shnok.javaserver.db.entity.DBCharacter;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DatabaseConfig {
    private static SessionFactory sessionFactory;

    public static SessionFactory buildSessionFactory() {
        Configuration configuration = new Configuration();

        // Hibernate HikariCP configuration
        configuration.setProperty("hibernate.connection.provider_class", "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");
        configuration.setProperty("hibernate.hbm2ddl.auto", "none");
        configuration.setProperty("hibernate.show_sql", "false");
        configuration.setProperty("hibernate.format_sql", "true");

        // HikariCP properties
        configuration.setProperty("hibernate.hikari.dataSourceClassName", "org.h2.jdbcx.JdbcDataSource");
        configuration.setProperty("hibernate.hikari.dataSource.url", "jdbc:h2:file:./db/l2-unity");
        configuration.setProperty("hibernate.hikari.dataSource.user", "h2");
        configuration.setProperty("hibernate.hikari.dataSource.password", "");

        // Connection pool properties
        configuration.setProperty("hibernate.hikari.maximumPoolSize", "5");
        configuration.setProperty("hibernate.hikari.minimumIdle", "2");

        // Timeout-related properties
        configuration.setProperty("hibernate.hikari.connectionTimeout", "2000"); // in milliseconds
        configuration.setProperty("hibernate.hikari.idleTimeout", "30000"); // in milliseconds
        configuration.setProperty("hibernate.hikari.maxLifetime", "60000"); // in milliseconds

        // Add entity classes to configuration
        configuration.addAnnotatedClass(DBSpawnList.class);
        configuration.addAnnotatedClass(DBNpc.class);
        configuration.addAnnotatedClass(DBZoneList.class);
        configuration.addAnnotatedClass(DBCharTemplate.class);
        configuration.addAnnotatedClass(DBPlayerItem.class);
        configuration.addAnnotatedClass(DBCharacter.class);
        configuration.addAnnotatedClass(DBEtcItem.class);
        configuration.addAnnotatedClass(DBWeapon.class);
        configuration.addAnnotatedClass(DBArmor.class);

        return configuration.buildSessionFactory();
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = buildSessionFactory();
        }

        return sessionFactory;
    }

    /*public static HikariDataSource buildDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/your_database");
        config.setUsername("your_username");
        config.setPassword("your_password");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        return new HikariDataSource(config);
    }

    public static HikariDataSource getDataSource() {
        if (dataSource == null) {
            dataSource = buildDataSource();
        }

        return dataSource;
    }*/

}
