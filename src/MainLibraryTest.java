//import org.example.itemtypes.Libraryitems;
import org.example.itemtypes.Book;
import org.example.itemtypes.Magazine;
import org.example.itemtypes.DVD;
import org.example.abstractions.Borrowable;
import org.example.abstractions.LibraryItem;
import java.util.ArrayList;
import java.util.List;


public class MainLibraryTest {
    public static void main(String[] args) {
        LibraryItem book1 = new Book("Clean Code", "Robert C. Martin", "1111", 2008, "Programming");
        LibraryItem mag1 = new Magazine("Science Today", "Editorial", "2222", 2024, 58);
        LibraryItem dvd = new DVD("Inception", "Christopher Nolan", "3333", 2010, 148);
        System.out.println("Total items in library: " + LibraryItem.getTotalItems());

        List<LibraryItem> items = new ArrayList<>();
        items.add(book1);
        items.add(mag1);
        items.add(dvd);
        for (LibraryItem item : items) {
            item.displayDetails(); // <- Polymorphism in action
            System.out.println();
        }



        Patron patron = new Patron("Farhan", "P001");

        patron.borrowItem(book1);
        patron.borrowItem(mag1);
        patron.showBorrowedItems();

        System.out.println("\n-- Details of each item --");
        book1.displayDetails();
        mag1.displayDetails();

        patron.returnItem(book1);
        patron.returnItem(mag1);


    }
}

