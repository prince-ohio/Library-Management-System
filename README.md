# 📚 Library Management System

A **Java-based desktop application** for managing library operations efficiently.  
This project demonstrates **Object-Oriented Programming (OOP)**, **Swing GUI**, and **SQLite database connectivity**.

---

## 🚀 Features
- **Book Management** – Add, view, and track books (Title, Author, ISBN, Genre, Availability).  
- **User Management** – Register and manage library users.  
- **Circulation Control** – Borrow and return books with real-time updates.  
- **Borrowing Rules** – Enforces maximum borrowing limits (3 books per user).  
- **Transaction History** – Maintains borrowing and returning records.  
- **Database-Driven** – Uses **SQLite** for lightweight, persistent storage.  

---

## 🏗️ Project Structure
LibraryProject/
│── Main.java # Entry point
│── Book.java # Book entity
│── BorrowTransaction.java # Borrow/Return records
│── DatabaseHelper.java # Handles SQLite connection
│── Librarian.java # Librarian role (extends User)
│── Library.java # Business logic
│── User.java # User entity
│── LibrarySwingUI.java # Swing-based GUI


---

## ⚙️ Technologies Used
- **Java** (JDK 8 or higher)  
- **Swing** (Graphical User Interface)  
- **SQLite** (Database)  
- **JDBC** (Database connectivity)  

---

## 🛠️ How to Run
1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/Library-Management-System.git
   cd Library-Management-System

2. Compile project:
   ```bash
   javac -d out -cp "sqlite-jdbc-3.50.3.0.jar" src/com/example/library/.java src/com/example/ui/.java src/com/example/app/*.java

3. Run project:
   ```bash
   java -cp "out;sqlite-jdbc-3.50.3.0.jar" com.example.app.Main
