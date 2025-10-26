import org.example.abstractions.Borrowable;
import org.example.abstractions.LibraryItem;

import java.util.ArrayList;
import java.util.List;

public class Patron {
    private String name;
    private String memberId;
    private List<Borrowable> borrowedBooks;

    public Patron(String name, String memberId) {
        this.name = name;
        this.memberId = memberId;
        this.borrowedBooks = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void borrowItem(Borrowable item) {
        if (!item.isBorrowed()) {
            item.borrowItem();
            borrowedBooks.add(item);
            LibraryItem.setTotalItems(LibraryItem.getTotalItems() -1);
            System.out.println("Total items in library: " + LibraryItem.getTotalItems());

        } else {
            System.out.println("Sorry, " + item.getTitle() + " is already borrowed.");
        }
    }

    public void returnItem(LibraryItem item) {
        if (borrowedBooks.contains(item)) {
            item.returnItem();
            borrowedBooks.remove(item);
            LibraryItem.setTotalItems(LibraryItem.getTotalItems() +1);
            System.out.println("Total items in library: " + LibraryItem.getTotalItems());
        } else {
            System.out.println("You did not borrow " + item.getTitle());
        }
    }

    public void showBorrowedItems() {
        System.out.println("Books borrowed by " + name + ":");
        for ( Borrowable item : borrowedBooks) {
            System.out.println(" - " + item.getTitle());
        }
    }
}
