//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.LocalDate;

/**
 * LibraryManagementSystem.java
 *
 * Single-file demonstration of a Library Management System implementing
 * the 4 OOP pillars (Encapsulation, Inheritance, Polymorphism, Abstraction),
 * applying SOLID principles and using two design patterns:
 * - Observer (for reservation notifications)
 * - Strategy (for recommendation)
 * - Factory (for entity creation)
 *
 * Notes:
 * - This example is in-memory (no persistence) and focuses on design, clarity and testability.
 * - Use java.util.logging for logging important events.
 */
public class LibraryManagementSystem {
    private static final Logger logger = Logger.getLogger(LibraryManagementSystem.class.getName());

    public static void main(String[] args) {
        logger.setLevel(Level.INFO);

        // Create inventory and services
        LibraryInventory inventory = new LibraryInventory();
        ReservationService reservationService = new ReservationService();
        LendingService lendingService = new LendingService(inventory, reservationService);

        // Recommendation system using Strategy pattern
        RecommendationService recommendationService = new RecommendationService(new FrequencyRecommendationStrategy());

        // Create some books and patrons via Factory
        Book b1 = EntityFactory.createBook("978-0134685991", "Effective Java", "Joshua Bloch", 2018, "Programming");
        Book b2 = EntityFactory.createBook("978-0201633610", "Design Patterns", "Erich Gamma", 1994, "Programming");
        Book b3 = EntityFactory.createBook("978-0596009205", "Head First Java", "Kathy Sierra", 2005, "Programming");
        Book b4 = EntityFactory.createBook("978-0439139595", "Harry Potter and the Goblet of Fire", "J. K. Rowling", 2000, "Fantasy");

        inventory.addBook(b1, 3);
        inventory.addBook(b2, 2);
        inventory.addBook(b3, 1);
        inventory.addBook(b4, 2);

        Patron p1 = EntityFactory.createPatron("P001", "Alice");
        Patron p2 = EntityFactory.createPatron("P002", "Bob");

        // Subscribe patrons to reservation notifications
        reservationService.registerListener(p1);
        reservationService. registerListener(p2);

        // Demonstrate search
        logger.info("Search by title 'Java': " + inventory.searchByTitle("Java"));
        logger.info("Search by author 'Rowling': " + inventory.searchByAuthor("Rowling"));

        // Alice borrows Effective Java
        lendingService.checkoutBook(p1, "978-0134685991");
        lendingService.checkoutBook(p1, "978-0439139595");

        // Bob tries to borrow Head First Java (only 1 copy)
        lendingService.checkoutBook(p2, "978-0596009205");

        // Bob tries to borrow Head First Java again (none left) -> reservation
        lendingService.checkoutBook(p2, "978-0596009205");
        reservationService.reserveBook(p2, "978-0596009205");

        // Alice returns Head First Java -> should notify Bob
        lendingService.returnBook(p1, "978-0596009205");

        // Recommendation based on history
        List<Book> recsForAlice = recommendationService.recommend(p1, inventory);
        logger.info("Recommendations for Alice: " + recsForAlice);

        // Change recommendation strategy at runtime (polymorphism)
        recommendationService.setStrategy(new GenreBasedRecommendationStrategy());
        List<Book> recsForBob = recommendationService.recommend(p2, inventory);
        logger.info("Recommendations for Bob (genre-based): " + recsForBob);

        // Demonstrate updating book info
        Book found = inventory.findByISBN("978-0201633610");
        if (found != null) {
            found.setPublicationYear(1995); // update
            inventory.updateBook(found);
        }

        logger.info("Final inventory snapshot:\n" + inventory);
    }
}

/* ======================
   Domain & Support classes
   ====================== */

/**
 * Abstract Item — shows inheritance and abstraction: future items (Magazine, DVD) can extend this.
 */
abstract class Item {
    private final String id; // unique identifier (ISBN for book)
    private String title;

    protected Item(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", title, id);
    }
}

/**
 * org.example.itemtypes.Book class — encapsulates book fields with getters/setters.
 */
class Book extends Item {
    private String author;
    private int publicationYear;
    private String genre;

    public Book(String isbn, String title, String author, int publicationYear, String genre) {
        super(isbn, title);
        this.author = author;
        this.publicationYear = publicationYear;
        this.genre = genre;
    }

    public String getIsbn() { return getId(); }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public int getPublicationYear() { return publicationYear; }
    public void setPublicationYear(int publicationYear) { this.publicationYear = publicationYear; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    @Override
    public String toString() {
        return String.format("org.example.itemtypes.Book[%s, %s, %s, %d, genre=%s]", getIsbn(), getTitle(), author, publicationYear, genre);
    }
}

/**
 * Patron class — tracks borrowing history and current borrowed books.
 */
class Patron implements NotificationListener {
    private final String id;
    private String name;

    // Borrow history and current borrowings
    private final List<BorrowRecord> borrowingHistory = new ArrayList<>();
    private final Set<String> borrowedIsbns = new HashSet<>();

    public Patron(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // Record a checkout
    public void recordCheckout(BorrowRecord r) {
        borrowedIsbns.add(r.getIsbn());
        borrowingHistory.add(r);
    }

    // Record a return
    public void recordReturn(String isbn) {
        borrowedIsbns.remove(isbn);
        // update last matching borrow record's return date
        for (int i = borrowingHistory.size() - 1; i >= 0; i--) {
            BorrowRecord br = borrowingHistory.get(i);
            if (br.getIsbn().equals(isbn) && br.getReturnDate() == null) {
                br.setReturnDate(LocalDate.now());
                break;
            }
        }
    }

    public List<BorrowRecord> getBorrowingHistory() { return Collections.unmodifiableList(borrowingHistory); }
    public Set<String> getBorrowedIsbns() { return Collections.unmodifiableSet(borrowedIsbns); }

    @Override
    public void notifyAvailable(String isbn) {
        // Simple console/log notification
        Logger.getLogger(Patron.class.getName()).info(String.format("[Notification] Patron %s: org.example.itemtypes.Book %s is now available!", name, isbn));
    }

    @Override
    public String toString() {
        return String.format("Patron[%s, %s]", id, name);
    }
}

/**
 * BorrowRecord — immutable-ish history record (mutates returnDate upon return).
 */
class BorrowRecord {
    private final String isbn;
    private final LocalDate checkoutDate;
    private LocalDate returnDate;

    public BorrowRecord(String isbn) {
        this.isbn = isbn;
        this.checkoutDate = LocalDate.now();
    }

    public String getIsbn() { return isbn; }
    public LocalDate getCheckoutDate() { return checkoutDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate d) { this.returnDate = d; }

    @Override
    public String toString() {
        return String.format("BorrowRecord[isbn=%s, out=%s, returned=%s]", isbn, checkoutDate, returnDate);
    }
}

/**
 * LibraryInventory — manages available copies and lookup. Demonstrates encapsulation and SRP.
 */
class LibraryInventory {
    private final Map<String, Book> bookByIsbn = new HashMap<>();
    private final Map<String, Integer> copiesAvailable = new HashMap<>();

    private static final Logger logger = Logger.getLogger(LibraryInventory.class.getName());

    // Add a new book with count
    public synchronized void addBook(Book book, int copies) {
        Objects.requireNonNull(book, "book");
        if (copies <= 0) throw new IllegalArgumentException("copies must be > 0");
        bookByIsbn.put(book.getIsbn(), book);
        copiesAvailable.merge(book.getIsbn(), copies, Integer::sum);
        logger.info(() -> "Added book " + book + " copies=" + copies);
    }

    // Remove a book completely
    public synchronized void removeBook(String isbn) {
        bookByIsbn.remove(isbn);
        copiesAvailable.remove(isbn);
        logger.info(() -> "Removed book isbn=" + isbn);
    }

    // Update metadata for a book
    public synchronized void updateBook(Book book) {
        if (!bookByIsbn.containsKey(book.getIsbn())) {
            throw new NoSuchElementException("org.example.itemtypes.Book not found: " + book.getIsbn());
        }
        bookByIsbn.put(book.getIsbn(), book);
        logger.info(() -> "Updated book " + book);
    }

    public Book findByISBN(String isbn) {
        return bookByIsbn.get(isbn);
    }

    public List<Book> searchByTitle(String q) {
        String lc = q.toLowerCase();
        List<Book> out = new ArrayList<>();
        for (Book b : bookByIsbn.values()) {
            if (b.getTitle().toLowerCase().contains(lc)) out.add(b);
        }
        return out;
    }

    public List<Book> searchByAuthor(String q) {
        String lc = q.toLowerCase();
        List<Book> out = new ArrayList<>();
        for (Book b : bookByIsbn.values()) {
            if (b.getAuthor().toLowerCase().contains(lc)) out.add(b);
        }
        return out;
    }

    // Query and manipulate copies
    public synchronized boolean checkoutCopy(String isbn) {
        Integer avail = copiesAvailable.getOrDefault(isbn, 0);
        if (avail <= 0) return false;
        copiesAvailable.put(isbn, avail - 1);
        logger.info(() -> "Checked out one copy of " + isbn + ", remaining=" + (avail - 1));
        return true;
    }

    public synchronized void returnCopy(String isbn) {
        copiesAvailable.merge(isbn, 1, Integer::sum);
        logger.info(() -> "Returned one copy of " + isbn + ", now=" + copiesAvailable.get(isbn));
    }

    public int availableCopies(String isbn) {
        return copiesAvailable.getOrDefault(isbn, 0);
    }

    public Collection<Book> allBooks() { return Collections.unmodifiableCollection(bookByIsbn.values()); }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Book b : bookByIsbn.values()) {
            sb.append(b).append(" copiesAvailable=").append(availableCopies(b.getIsbn())).append("\n");
        }
        return sb.toString();
    }
}

/* ======================
   Lending & Reservation (Observer pattern)
   ====================== */

/**
 * NotificationListener — abstraction for observer pattern
 */
interface NotificationListener {
    void notifyAvailable(String isbn);
}

/**
 * ReservationService — manages reservations and notifies listeners when a book becomes available.
 * Demonstrates Observer pattern.
 */
class ReservationService {
    private final Map<String, Queue<String>> reservationQueues = new HashMap<>(); // isbn -> queue of patronIds
    private final Map<String, NotificationListener> listenersByPatronId = new HashMap<>();
    private final Logger logger = Logger.getLogger(ReservationService.class.getName());

    // In this example, we register Patron objects as listeners keyed by their ID
    public void registerListener(Patron patron) {
        listenersByPatronId.put(patron.getId(), patron);
    }

    public void unregisterListener(Patron patron) {
        listenersByPatronId.remove(patron.getId());
    }

    public void reserveBook(Patron patron, String isbn) {
        reservationQueues.computeIfAbsent(isbn, k -> new LinkedList<>()).add(patron.getId());
        logger.info(() -> "Patron " + patron.getId() + " reserved " + isbn);
    }

    // Called by lending service when a copy becomes available
    public void notifyAvailable(String isbn) {
        Queue<String> q = reservationQueues.get(isbn);
        if (q == null || q.isEmpty()) return;
        String nextPatronId = q.poll();
        NotificationListener nl = listenersByPatronId.get(nextPatronId);
        if (nl != null) {
            nl.notifyAvailable(isbn);
            logger.info(() -> "Notified patron " + nextPatronId + " about availability of " + isbn);
        }
    }
}

/**
 * LendingService — handles checkout and return. Keeps track of patron records.
 */
class LendingService {
    private final LibraryInventory inventory;
    private final ReservationService reservationService;
    private final Map<String, Patron> patronsById = new HashMap<>();
    private final Logger logger = Logger.getLogger(LendingService.class.getName());

    public LendingService(LibraryInventory inventory, ReservationService reservationService) {
        this.inventory = inventory;
        this.reservationService = reservationService;
    }

    // Register patron (in real app, PatronService would exist)
    public void registerPatron(Patron p) {
        patronsById.put(p.getId(), p);
    }

    public boolean checkoutBook(Patron patron, String isbn) {
        // Register patron if not present
        patronsById.putIfAbsent(patron.getId(), patron);

        Objects.requireNonNull(isbn);
        Book book = inventory.findByISBN(isbn);
        if (book == null) {
            logger.warning("org.example.itemtypes.Book not found: " + isbn);
            return false;
        }

        if (inventory.checkoutCopy(isbn)) {
            // record
            BorrowRecord br = new BorrowRecord(isbn);
            patron.recordCheckout(br);
            logger.info(() -> String.format("Patron %s checked out %s", patron.getId(), isbn));
            return true;
        } else {
            logger.info(() -> String.format("No copies available for %s. Consider reserving.", isbn));
            return false;
        }
    }

    public boolean returnBook(Patron patron, String isbn) {
        // basic validation
        if (!patron.getBorrowedIsbns().contains(isbn)) {
            logger.warning(() -> String.format("Patron %s did not have %s borrowed", patron.getId(), isbn));
            // still process return into inventory to keep counts correct if needed
        }
        inventory.returnCopy(isbn);
        patron.recordReturn(isbn);
        logger.info(() -> String.format("Patron %s returned %s", patron.getId(), isbn));

        // Notify reservation service so next in queue can be informed
        reservationService.notifyAvailable(isbn);
        return true;
    }
}

/* ======================
   Recommendation (Strategy pattern)
   ====================== */

/**
 * RecommendationStrategy — abstraction for recommendation algorithms (Strategy pattern)
 */
interface RecommendationStrategy {
    List<Book> recommend(Patron patron, LibraryInventory inventory);
}

/**
 * Simple frequency-based recommendation — recommends most-borrowed genres from history.
 */
class FrequencyRecommendationStrategy implements RecommendationStrategy {
    @Override
    public List<Book> recommend(Patron patron, LibraryInventory inventory) {
        // Count genres in patron history
        Map<String, Integer> genreCount = new HashMap<>();
        for (BorrowRecord br : patron.getBorrowingHistory()) {
            Book b = inventory.findByISBN(br.getIsbn());
            if (b != null && b.getGenre() != null) {
                genreCount.merge(b.getGenre(), 1, Integer::sum);
            }
        }
        // pick top genre
        String topGenre = genreCount.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);

        // if no history, recommend any available books
        List<Book> candidates = new ArrayList<>();
        for (Book b : inventory.allBooks()) {
            if (inventory.availableCopies(b.getIsbn()) > 0 && !patron.getBorrowedIsbns().contains(b.getIsbn())) {
                if (topGenre == null || topGenre.equals(b.getGenre())) candidates.add(b);
            }
        }
        // sort by publicationYear desc for variety
        candidates.sort(Comparator.comparingInt(Book::getPublicationYear).reversed());
        return candidates.size() > 5 ? candidates.subList(0, 5) : candidates;
    }
}

/**
 * Genre-based recommendation: recommend books sharing genres with patron's past borrows.
 */
class GenreBasedRecommendationStrategy implements RecommendationStrategy {
    @Override
    public List<Book> recommend(Patron patron, LibraryInventory inventory) {
        Set<String> likedGenres = new HashSet<>();
        for (BorrowRecord br : patron.getBorrowingHistory()) {
            Book b = inventory.findByISBN(br.getIsbn());
            if (b != null && b.getGenre() != null) likedGenres.add(b.getGenre());
        }
        List<Book> out = new ArrayList<>();
        for (Book b : inventory.allBooks()) {
            if (inventory.availableCopies(b.getIsbn()) > 0 && !patron.getBorrowedIsbns().contains(b.getIsbn())) {
                if (likedGenres.contains(b.getGenre())) out.add(b);
            }
        }
        // fallback
        if (out.isEmpty()) {
            for (Book b : inventory.allBooks()) {
                if (inventory.availableCopies(b.getIsbn()) > 0 && !patron.getBorrowedIsbns().contains(b.getIsbn())) out.add(b);
            }
        }
        return out.size() > 5 ? out.subList(0, 5) : out;
    }
}

/**
 * RecommendationService — holds a strategy and delegates recommendation.
 */
class RecommendationService {
    private RecommendationStrategy strategy;

    public RecommendationService(RecommendationStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(RecommendationStrategy strategy) {
        this.strategy = strategy;
    }

    public List<Book> recommend(Patron patron, LibraryInventory inventory) {
        return strategy.recommend(patron, inventory);
    }
}

/* ======================
   Factory for Entities
   ====================== */
class EntityFactory {
    public static Book createBook(String isbn, String title, String author, int pubYear, String genre) {
        return new Book(isbn, title, author, pubYear, genre);
    }

    public static Patron createPatron(String id, String name) {
        return new Patron(id, name);
    }
}
