package com.hasan.note.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Database configuration and connection pool management class.
 * Uses HikariCP for efficient connection pooling.
 */
public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static HikariDataSource dataSource;

    static {
        try {
            initialize();
            createTablesIfNotExist();
        } catch (IOException e) {
            logger.error("Failed to initialize database connection pool", e);
        } catch (SQLException e) {
            logger.error("Failed to create database tables", e);
        }
    }

    /**
     * Initializes the database connection pool
     */
    private static void initialize() throws IOException {
        Properties props = loadDatabaseProperties();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(props.getProperty("db.url"));
        config.setUsername(props.getProperty("db.username"));
        config.setPassword(props.getProperty("db.password"));
        config.setDriverClassName(props.getProperty("db.driver"));

        // Connection pool settings
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(20000);

        // Testing connectivity
        config.setConnectionTestQuery("SELECT 1");

        dataSource = new HikariDataSource(config);

        logger.info("Database connection pool initialized successfully");
    }

    /**
     * Creates database tables if they don't exist
     */
    private static void createTablesIfNotExist() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Create users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "username VARCHAR(50) NOT NULL UNIQUE," +
                    "email VARCHAR(100) NOT NULL UNIQUE," +
                    "password_hash VARCHAR(255) NOT NULL," +
                    "full_name VARCHAR(100)," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                    ")");

            // Create notes table
            stmt.execute("CREATE TABLE IF NOT EXISTS notes (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "user_id INT NOT NULL," +
                    "title VARCHAR(255) NOT NULL," +
                    "content TEXT," +
                    "is_pinned BOOLEAN DEFAULT FALSE," +
                    "is_archived BOOLEAN DEFAULT FALSE," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                    ")");

            // Create tags table
            stmt.execute("CREATE TABLE IF NOT EXISTS tags (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "name VARCHAR(50) NOT NULL," +
                    "user_id INT NOT NULL," +
                    "color VARCHAR(7) DEFAULT '#607D8B'," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "UNIQUE KEY unique_tag_per_user (name, user_id)," +
                    "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                    ")");

            // Create note_tags junction table
            stmt.execute("CREATE TABLE IF NOT EXISTS note_tags (" +
                    "note_id INT NOT NULL," +
                    "tag_id INT NOT NULL," +
                    "PRIMARY KEY (note_id, tag_id)," +
                    "FOREIGN KEY (note_id) REFERENCES notes(id) ON DELETE CASCADE," +
                    "FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE" +
                    ")");

            // Create reminders table
            stmt.execute("CREATE TABLE IF NOT EXISTS reminders (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "note_id INT NOT NULL," +
                    "reminder_time DATETIME NOT NULL," +
                    "is_completed BOOLEAN DEFAULT FALSE," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (note_id) REFERENCES notes(id) ON DELETE CASCADE" +
                    ")");

            // Create a test user if none exists
            stmt.execute("INSERT IGNORE INTO users (id, username, email, password_hash, full_name) " +
                    "VALUES (1, 'testuser', 'test@example.com', " +
                    "'$2a$10$XgNEHAr1E3JWAXjmQGfnZOEUZojLImJY8djrR2S8QglyK1ZhNO5Y.', 'Test User')");

            logger.info("Database tables created successfully");
        }
    }

    /**
     * Loads database connection properties from config file
     */
    private static Properties loadDatabaseProperties() throws IOException {
        Properties props = new Properties();

        // First try to load from config.properties
        try (InputStream in = DatabaseConfig.class.getResourceAsStream("/config.properties")) {
            if (in != null) {
                props.load(in);
                return props;
            }
        } catch (Exception e) {
            logger.warn("Failed to load config.properties", e);
        }

        // If not found, use default values for development
        logger.info("Using default database configuration");
        props.setProperty("db.url", "jdbc:mysql://localhost:3307/notedb");
        props.setProperty("db.username", "noteuser");
        props.setProperty("db.password", "notepassword");
        props.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");

        return props;
    }

    /**
     * Gets a database connection from the pool
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Connection pool is not initialized");
        }
        return dataSource.getConnection();
    }

    /**
     * Closes the connection pool
     */
    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Database connection pool closed");
        }
    }
}