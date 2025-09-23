package com.example.ui;

import com.example.library.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LibrarySwingUI {
    private final Library library = new Library();
    private JFrame frame;
    private DefaultTableModel booksModel;
    private DefaultTableModel usersModel;

    public void show() {
        frame = new JFrame("📚 Library Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);

        // Modern flat look
        UIManager.put("TabbedPane.selected", new Color(220, 235, 245));
        UIManager.put("Panel.background", new Color(245, 250, 255));
        UIManager.put("Button.background", new Color(220, 235, 245));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));

        tabs.addTab("Books", createBooksPanel());
        tabs.addTab("Users", createUsersPanel());
        tabs.addTab("Circulation", createCirculationPanel());

        frame.add(tabs);
        frame.setVisible(true);
    }

    /* ----- Books Tab ----- */
    private JPanel createBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel heading = new JLabel("📖 Manage Books");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 16));
        heading.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(heading, BorderLayout.NORTH);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JTextField titleField = new JTextField(12);
        JTextField authorField = new JTextField(12);
        JTextField isbnField = new JTextField(10);
        JTextField genreField = new JTextField(10);
        JButton addBtn = new JButton("Add Book");
        JButton refreshBtn = new JButton("Refresh");

        top.add(new JLabel("Title:")); top.add(titleField);
        top.add(new JLabel("Author:")); top.add(authorField);
        top.add(new JLabel("ISBN:")); top.add(isbnField);
        top.add(new JLabel("Genre:")); top.add(genreField);
        top.add(addBtn); top.add(refreshBtn);

        booksModel = new DefaultTableModel(new Object[]{"ID","Title","Author","ISBN","Genre","Available"}, 0) {
            public boolean isCellEditable(int r,int c){return false;}
        };
        JTable booksTable = new JTable(booksModel);
        booksTable.setFillsViewportHeight(true);
        booksTable.setRowHeight(22);
        JScrollPane scroll = new JScrollPane(booksTable);

        panel.add(top, BorderLayout.CENTER);
        panel.add(scroll, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> {
            String t = titleField.getText().trim();
            String a = authorField.getText().trim();
            String i = isbnField.getText().trim();
            String g = genreField.getText().trim();
            if (t.isEmpty() || a.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Title & Author required.");
                return;
            }
            Book b = library.addBook(new Book(t,a,i,g));
            if (b != null) {
                JOptionPane.showMessageDialog(frame, "Book added with ID: " + b.getId());
                titleField.setText(""); authorField.setText(""); isbnField.setText(""); genreField.setText("");
                loadBooks();
            } else JOptionPane.showMessageDialog(frame, "Failed to add book.");
        });

        refreshBtn.addActionListener(e -> loadBooks());
        loadBooks();
        return panel;
    }

    private void loadBooks() {
        booksModel.setRowCount(0);
        List<Book> books = library.getAllBooks();
        for (Book b : books) {
            booksModel.addRow(new Object[]{
                    b.getId(), b.getTitle(), b.getAuthor(), b.getIsbn(), b.getGenre(),
                    b.isAvailable() ? "Available" : "Borrowed"
            });
        }
    }

    /* ----- Users Tab ----- */
    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JLabel heading = new JLabel("👤 Manage Users");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 16));
        heading.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(heading, BorderLayout.NORTH);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JTextField nameField = new JTextField(15);
        JTextField contactField = new JTextField(12);
        JButton addUserBtn = new JButton("Add User");
        JButton refreshBtn = new JButton("Refresh");

        top.add(new JLabel("Name:")); top.add(nameField);
        top.add(new JLabel("Contact:")); top.add(contactField);
        top.add(addUserBtn); top.add(refreshBtn);

        usersModel = new DefaultTableModel(new Object[]{"ID","Name","Contact"}, 0) {
            public boolean isCellEditable(int r,int c){return false;}
        };
        JTable usersTable = new JTable(usersModel);
        usersTable.setFillsViewportHeight(true);
        usersTable.setRowHeight(22);
        JScrollPane scroll = new JScrollPane(usersTable);

        panel.add(top, BorderLayout.CENTER);
        panel.add(scroll, BorderLayout.SOUTH);

        addUserBtn.addActionListener(e -> {
            String n = nameField.getText().trim();
            String c = contactField.getText().trim();
            if (n.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Name required.");
                return;
            }
            User u = library.registerUser(new User(n, c));
            if (u != null) {
                JOptionPane.showMessageDialog(frame, "User registered. ID: " + u.getId());
                nameField.setText(""); contactField.setText("");
                loadUsers();
            } else JOptionPane.showMessageDialog(frame, "Failed to register user.");
        });

        refreshBtn.addActionListener(e -> loadUsers());
        loadUsers();
        return panel;
    }

    private void loadUsers() {
        usersModel.setRowCount(0);
        List<User> users = library.getAllUsers();
        for (User u : users)
            usersModel.addRow(new Object[]{u.getId(), u.getName(), u.getContact()});
    }

    /* ----- Circulation Tab ----- */
    private JPanel createCirculationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JLabel heading = new JLabel("🔄 Circulation (Borrow / Return)");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 16));
        heading.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(heading, BorderLayout.NORTH);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JTextField bookIdField = new JTextField(6);
        JTextField userIdField = new JTextField(6);
        JButton borrowBtn = new JButton("Borrow");
        JButton returnBtn = new JButton("Return");
        JButton refreshBooksBtn = new JButton("Refresh Books");

        top.add(new JLabel("Book ID:")); top.add(bookIdField);
        top.add(new JLabel("User ID:")); top.add(userIdField);
        top.add(borrowBtn); top.add(returnBtn); top.add(refreshBooksBtn);

        JTextArea log = new JTextArea(12, 70);
        log.setEditable(false);
        log.setFont(new Font("Consolas", Font.PLAIN, 12));
        JScrollPane logScroll = new JScrollPane(log);

        panel.add(top, BorderLayout.CENTER);
        panel.add(logScroll, BorderLayout.SOUTH);

        borrowBtn.addActionListener(e -> {
            try {
                int bid = Integer.parseInt(bookIdField.getText().trim());
                int uid = Integer.parseInt(userIdField.getText().trim());
                boolean ok = library.borrowBook(bid, uid);
                log.append(ok ? "Borrowed book " + bid + " to user " + uid + "\n"
                              : "Borrow failed for book " + bid + "\n");
                loadBooks();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Enter valid numeric IDs.");
            }
        });

        returnBtn.addActionListener(e -> {
            try {
                int bid = Integer.parseInt(bookIdField.getText().trim());
                int uid = Integer.parseInt(userIdField.getText().trim());
                boolean ok = library.returnBook(bid, uid);
                log.append(ok ? "Returned book " + bid + " from user " + uid + "\n"
                              : "Return failed for book " + bid + "\n");
                loadBooks();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Enter valid numeric IDs.");
            }
        });

        refreshBooksBtn.addActionListener(e -> {
            loadBooks();
            log.append("Books refreshed.\n");
        });

        return panel;
    }
}
