package library;

import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BookInventory {
    private final Map<String, Book> books = new HashMap<>();
    private final Map<String, Set<BookCopy>> copiesByBookId = new HashMap<>();
    private final Map<String, Deque<BookCopy>> availableCopiesByBookId = new HashMap<>();

    // addBook
    // removeBook
    // allocateCopy
    // releaseCopy
}
