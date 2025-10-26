package org.example.abstractions;

public abstract class LibraryItem implements Borrowable {
    protected String title;
    protected String author;
    protected String isbn;
    protected int publicationYear;
    protected boolean isBorrowed;
    protected static int totalItems = 0;  // Static counter for all items

    public LibraryItem(String title, String author, String isbn, int publicationYear) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
        this.isBorrowed = false;
        totalItems++;  // Increment counter when a new item is created
    }

    // Static method to get total count of all items
    public static int getTotalItems() {
        return totalItems;
    }
    public static void setTotalItems(int totalItems) {
        LibraryItem.totalItems = totalItems;
    }

    // concrete (fully defined) methods
    public boolean isBorrowed() {
        return isBorrowed;
    }

    // abstract (to be defined by child)
    public abstract void displayDetails();

    // shared logic for borrowing and returning
    public void borrowItem() {
        if (!isBorrowed) {
            isBorrowed = true;
            System.out.println(title + " has been borrowed.");
        } else {
            System.out.println(title + " is already borrowed.");
        }
    }

    public void returnItem() {
        if (isBorrowed) {
            isBorrowed = false;
            System.out.println(title + " has been returned.");
        } else {
            System.out.println(title + " was not borrowed.");
        }
    }

    public String getTitle() {
        return title;
    }
}
