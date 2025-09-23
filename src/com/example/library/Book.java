package com.example.library;

public class Book {
    private int id;
    private String title;
    private String author;
    private String isbn;
    private String genre;
    private boolean available;

    public Book(int id, String title, String author, String isbn, String genre, boolean available) {
        this.id = id; this.title = title; this.author = author; this.isbn = isbn; this.genre = genre; this.available = available;
    }

    public Book(String title, String author, String isbn, String genre) {
        this(0, title, author, isbn, genre, true);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public String getGenre() { return genre; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return String.format("[%d] %s — %s (ISBN:%s) Genre:%s Status:%s",
                id, title, author, isbn == null ? "-" : isbn, genre == null ? "-" : genre, available ? "Available" : "Borrowed");
    }
}
