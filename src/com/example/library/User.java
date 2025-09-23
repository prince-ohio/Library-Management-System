package com.example.library;

public class User {
    private int id;
    private String name;
    private String contact;

    public User(int id, String name, String contact) { this.id = id; this.name = name; this.contact = contact; }
    public User(String name, String contact) { this(0, name, contact); }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public String getContact() { return contact; }

    @Override
    public String toString() { return String.format("[%d] %s <%s>", id, name, contact == null ? "" : contact); }
}
