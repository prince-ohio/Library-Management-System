package com.example.library;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class DatabaseHelper {
    private static final String URL = "jdbc:sqlite:library.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /**
     * Initialize DB: create tables if missing and add missing columns if necessary.
     */
    public static void init() {
        try (Connection c = getConnection(); Statement s = c.createStatement()) {

            // Enable foreign keys
            s.execute("PRAGMA foreign_keys = ON");

            // Create books table with ALL required columns
            s.execute("CREATE TABLE IF NOT EXISTS books (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "title TEXT NOT NULL, " +
                    "author TEXT NOT NULL, " +
                    "isbn TEXT, " +
                    "genre TEXT, " +
                    "available INTEGER DEFAULT 1" +  // This is the missing column!
                    ");");

            s.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "contact TEXT" +
                    ");");

            s.execute("CREATE TABLE IF NOT EXISTS transactions (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "book_id INTEGER NOT NULL, " +
                    "user_id INTEGER NOT NULL, " +
                    "borrow_date TEXT, " +
                    "return_date TEXT, " +
                    "FOREIGN KEY (book_id) REFERENCES books(id), " +
                    "FOREIGN KEY (user_id) REFERENCES users(id)" +
                    ");");

            // Add the 'available' column if it doesn't exist (migration for existing databases)
            ensureColumnExists(c, "books", "available", 
                    "ALTER TABLE books ADD COLUMN available INTEGER DEFAULT 1");

            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("DB init error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Check PRAGMA table_info(table) to see columns, add column using alterSql if missing.
     */
    private static void ensureColumnExists(Connection c, String table, String column, String alterSql) throws SQLException {
        Set<String> cols = new HashSet<>();
        String pragma = "PRAGMA table_info(" + table + ")";
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(pragma)) {
            while (rs.next()) {
                cols.add(rs.getString("name").toLowerCase());
            }
        }

        if (!cols.contains(column.toLowerCase())) {
            try (Statement st = c.createStatement()) {
                st.execute(alterSql);
                System.out.println("Added column '" + column + "' to table '" + table + "'.");
            }
        }
    }
}