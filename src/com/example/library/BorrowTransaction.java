package com.example.library;

public class BorrowTransaction {
    private int id;
    private int bookId;
    private int userId;
    private String borrowDate;
    private String returnDate;

    public BorrowTransaction(int id, int bookId, int userId, String borrowDate, String returnDate) {
        this.id = id; this.bookId = bookId; this.userId = userId; this.borrowDate = borrowDate; this.returnDate = returnDate;
    }

    public int getId() { return id; }
    public int getBookId() { return bookId; }
    public int getUserId() { return userId; }
    public String getBorrowDate() { return borrowDate; }
    public String getReturnDate() { return returnDate; }

    @Override
    public String toString() {
        return String.format("Tx[%d] Book:%d User:%d Borrowed:%s Returned:%s",
                id, bookId, userId, borrowDate, returnDate == null ? "-" : returnDate);
    }
}
