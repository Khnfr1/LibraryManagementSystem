package org.example.itemtypes;
import java.util.logging.Logger;
import java.util.logging.Level;


import org.example.abstractions.LibraryItem;

public class Book extends LibraryItem {
    private static final Logger logger = Logger.getLogger(Book.class.getName());
    private int TotalBooks = 0;
    private String genre;

    public Book(String title, String author, String isbn, int publicationYear, String genre) {
        // call parent constructor
        super(title, author, isbn, publicationYear);
        this.genre = genre;
        this.TotalBooks = TotalBooks +1;
    }

    public int getTotalBooks() {
        return TotalBooks;
    }
    public String getGenre() {
        return genre;
    }

    // Child-specific behavior
    @Override
    public void displayDetails() {
        logger.log(Level.SEVERE , "Displaying details for book: {0}", title);

        System.out.println("📘 Book Details:");
        System.out.println("Title: " + title);
        System.out.println("Author: " + author);
        System.out.println("ISBN: " + isbn);
        System.out.println("Year: " + publicationYear);
        System.out.println("Genre: " + genre);
    }
}
