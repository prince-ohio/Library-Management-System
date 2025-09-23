package com.example.library;

// Librarian extends User; for now it does not add DB fields, but demonstrates inheritance.
public class Librarian extends User {
    public Librarian(int id, String name, String contact) { super(id,name,contact); }
    public Librarian(String name, String contact) { super(name,contact); }

    // All librarian actions are implemented in Library class. This class exists to show inheritance.
}
