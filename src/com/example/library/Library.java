package com.example.library;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Library {
    private static final int MAX_BORROW = 3;

    public Library() { DatabaseHelper.init(); }

    /* ----- Books ----- */
    public Book addBook(Book b) {
        String sql = "INSERT INTO books(title,author,isbn,genre,available) VALUES(?,?,?,?,?)";
        try (Connection c = DatabaseHelper.getConnection();
             PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, b.getTitle());
            p.setString(2, b.getAuthor());
            p.setString(3, b.getIsbn());
            p.setString(4, b.getGenre());
            p.setInt(5, b.isAvailable() ? 1 : 0);
            p.executeUpdate();
            try (ResultSet keys = p.getGeneratedKeys()) {
                if (keys.next()) b.setId(keys.getInt(1));
            }
            return b;
        } catch (SQLException e) { System.err.println("addBook error: " + e.getMessage()); return null; }
    }

    public List<Book> getAllBooks() {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY id";
        try (Connection c = DatabaseHelper.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) list.add(new Book(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("isbn"),
                    rs.getString("genre"),
                    rs.getInt("available") == 1
            ));
        } catch (SQLException e) { System.err.println("getAllBooks error: " + e.getMessage()); }
        return list;
    }

    /* ----- Users ----- */
    public User registerUser(User u) {
        String sql = "INSERT INTO users(name,contact) VALUES(?,?)";
        try (Connection c = DatabaseHelper.getConnection();
             PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, u.getName());
            p.setString(2, u.getContact());
            p.executeUpdate();
            try (ResultSet keys = p.getGeneratedKeys()) {
                if (keys.next()) u.setId(keys.getInt(1));
            }
            return u;
        } catch (SQLException e) { System.err.println("registerUser error: " + e.getMessage()); return null; }
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id";
        try (Connection c = DatabaseHelper.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) list.add(new User(rs.getInt("id"), rs.getString("name"), rs.getString("contact")));
        } catch (SQLException e) { System.err.println("getAllUsers error: " + e.getMessage()); }
        return list;
    }

    /* ----- Borrow / Return ----- */
    private int activeBorrowCount(int userId) {
        String sql = "SELECT COUNT(*) AS cnt FROM transactions WHERE user_id = ? AND return_date IS NULL";
        try (Connection c = DatabaseHelper.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, userId);
            try (ResultSet rs = p.executeQuery()) { if (rs.next()) return rs.getInt("cnt"); }
        } catch (SQLException e) { System.err.println("activeBorrowCount error: " + e.getMessage()); }
        return 0;
    }

    public boolean borrowBook(int bookId, int userId) {
        try (Connection c = DatabaseHelper.getConnection()) {
            c.setAutoCommit(false);
            Book book = getBookById(bookId);
            if (book == null) { System.out.println("Book not found."); c.rollback(); c.setAutoCommit(true); return false; }
            if (!book.isAvailable()) { System.out.println("Book not available."); c.rollback(); c.setAutoCommit(true); return false; }
            User user = getUserById(userId);
            if (user == null) { System.out.println("User not found."); c.rollback(); c.setAutoCommit(true); return false; }
            if (activeBorrowCount(userId) >= MAX_BORROW) { System.out.println("User reached borrow limit."); c.rollback(); c.setAutoCommit(true); return false; }

            try (PreparedStatement insert = c.prepareStatement("INSERT INTO transactions(book_id,user_id,borrow_date) VALUES(?,?,?)");
                 PreparedStatement upd = c.prepareStatement("UPDATE books SET available = 0 WHERE id = ?")) {
                insert.setInt(1, bookId);
                insert.setInt(2, userId);
                insert.setString(3, LocalDate.now().toString());
                insert.executeUpdate();

                upd.setInt(1, bookId);
                upd.executeUpdate();

                c.commit();
                c.setAutoCommit(true);
                System.out.println("Borrow successful.");
                return true;
            } catch (SQLException ex) { c.rollback(); c.setAutoCommit(true); System.err.println("borrow error: " + ex.getMessage()); return false; }
        } catch (SQLException e) { System.err.println("borrow conn error: " + e.getMessage()); return false; }
    }

    public boolean returnBook(int bookId, int userId) {
        try (Connection c = DatabaseHelper.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement updTx = c.prepareStatement("UPDATE transactions SET return_date = ? WHERE book_id = ? AND user_id = ? AND return_date IS NULL");
                 PreparedStatement updBook = c.prepareStatement("UPDATE books SET available = 1 WHERE id = ?")) {
                updTx.setString(1, LocalDate.now().toString());
                updTx.setInt(2, bookId);
                updTx.setInt(3, userId);
                int affected = updTx.executeUpdate();
                if (affected == 0) { System.out.println("No active loan found."); c.rollback(); c.setAutoCommit(true); return false; }

                updBook.setInt(1, bookId);
                updBook.executeUpdate();
                c.commit();
                c.setAutoCommit(true);
                System.out.println("Return successful.");
                return true;
            } catch (SQLException ex) { c.rollback(); c.setAutoCommit(true); System.err.println("return error: " + ex.getMessage()); return false; }
        } catch (SQLException e) { System.err.println("return conn error: " + e.getMessage()); return false; }
    }

    /* ----- Helpers ----- */
    public Book getBookById(int id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        try (Connection c = DatabaseHelper.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, id);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) return new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"),
                        rs.getString("isbn"), rs.getString("genre"), rs.getInt("available") == 1);
            }
        } catch (SQLException e) { System.err.println("getBookById error: " + e.getMessage()); }
        return null;
    }

    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection c = DatabaseHelper.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, id);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) return new User(rs.getInt("id"), rs.getString("name"), rs.getString("contact"));
            }
        } catch (SQLException e) { System.err.println("getUserById error: " + e.getMessage()); }
        return null;
    }
}
