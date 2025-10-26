package org.example.itemtypes;
import org.example.abstractions.LibraryItem;
import java.util.logging.Logger;
import java.util.logging.Level;

public class DVD extends LibraryItem {
    private static final Logger logger = Logger.getLogger(DVD.class.getName());
    private int duration; // in minutes
    private int TotalDVDs = 0;
    public DVD(String title, String author, String isbn, int publicationYear, int duration) {
        super(title, author, isbn, publicationYear);
        this.duration = duration;
        this.TotalDVDs = TotalDVDs +1;
    }
    public int getTotalDVDs() {
        return TotalDVDs;
    }

    @Override
    public void displayDetails() {
        logger.log(Level.INFO, "Displaying details for DVD: {0}", title);
        System.out.println("💿 [DVD]");
        System.out.println("Title: " + title);
        System.out.println("Duration: " + duration + " mins");
    }
}
