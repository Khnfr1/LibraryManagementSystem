package org.example.itemtypes;
import org.example.abstractions.LibraryItem;
import java.util.logging.Logger;
import java.util.logging.Level;

public class Magazine extends LibraryItem {
    private int issueNumber;
    private static final Logger logger = Logger.getLogger(Magazine.class.getName());
    private int TotalMagazines;

    public Magazine(String title, String author, String isbn, int publicationYear, int issueNumber) {
        super(title, author, isbn, publicationYear);
        this.issueNumber = issueNumber;
        this.TotalMagazines = TotalMagazines +1;
    }
    public int getTotalMagazines() {
        return TotalMagazines;
    }

    public int getIssueNumber() {
        return issueNumber;
    }

    @Override
    public void displayDetails() {
        logger.log(Level.INFO, "Displaying details for magazine: {0}", title);
        System.out.println("📰 Magazine Details:");
        System.out.println("Title: " + title);
        System.out.println("Issue No: " + issueNumber);
        System.out.println("Published: " + publicationYear);
    }
}
