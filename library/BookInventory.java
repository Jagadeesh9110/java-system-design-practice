package library;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BookInventory {
    private final Map<String, Book> books = new HashMap<>();
    private final Map<String, Set<BookCopy>> copiesByBookId = new HashMap<>();
    private final Map<String, Deque<BookCopy>> availableCopiesByBookId = new HashMap<>();

    // addBook
    // Registers a new book and creates N physical copies.
        public void addBook(Book book , int numberOfCopies){
            if(book == null){
                throw new IllegalArgumentException("Book or Book ID cannot be null");
            }
            
            if (numberOfCopies <= 0) {
                  throw new IllegalArgumentException("Number of copies must be positive");
            }
            String bookId =book.getBookId();
            if(books.containsKey(bookId)){
                throw new IllegalStateException("Book with ID " + bookId + " already exists in inventory");
            }

            books.put(bookId,book);
            copiesByBookId.put(bookId, new HashSet<>());
            availableCopiesByBookId.put(bookId, new ArrayDeque<>());

            for(int i=1;i<=numberOfCopies;i++){
                 String copyId = bookId + "-COPY-" + i;
                 BookCopy copy = new BookCopy(copyId, bookId);

                copiesByBookId.get(bookId).add(copy);
                availableCopiesByBookId.get(bookId).add(copy);
            }

        }
    // removeBook
    // Removes a book and all its copies from the inventory.
    public void removeBook(String bookId){
        if(bookId == null){
            throw new IllegalArgumentException("Book ID cannot be null");
        }
        if(!books.containsKey(bookId)){
            throw new IllegalStateException("Book with ID " + bookId + " does not exist in inventory");
        }
        
        Set<BookCopy> allCopies = copiesByBookId.get(bookId);
        Deque<BookCopy> availableCopies = availableCopiesByBookId.get(bookId);

        if(availableCopies.size() < allCopies.size()){
            throw new IllegalStateException("Cannot remove book with ID " + bookId + " as some copies are currently allocated");
        }


        books.remove(bookId);
        copiesByBookId.remove(bookId);
        availableCopiesByBookId.remove(bookId);

    }

    // allocateCopy
    // Allocates an available copy of the specified book(From inventory, give me one available physical copy of this book and mark it as issued).
    public BookCopy allocateCopy(String bookId){
       if(bookId==null){
         throw new IllegalArgumentException("Book ID cannot be null");
       }
       Deque<BookCopy> availableCopies= availableCopiesByBookId.get(bookId);
       if(availableCopies==null){
         throw new IllegalArgumentException("Book with ID " + bookId + " does not exist in inventory");

       }
       if(availableCopies.isEmpty()){
         throw new IllegalStateException("No available copies for book ID " + bookId);
       }



      BookCopy copyToAllocate = availableCopies.removeFirst();
        copyToAllocate.issue();
       return copyToAllocate;
    }

    // releaseCopy
    // Releases an issued copy back to the inventory (From inventory, return this physical copy of the book and mark it as available).
    public void releaseCopy(BookCopy copy){
         if(copy==null){
            throw new IllegalArgumentException("Book copy cannot be null");
         }

         String bookId=copy.getBookId();
         
          Set<BookCopy> allCopies = copiesByBookId.get(bookId);
          Deque<BookCopy> availableCopies = availableCopiesByBookId.get(bookId);
          if(allCopies==null || availableCopies==null){
            throw new IllegalStateException("Book with ID " + bookId + " does not exist in inventory");
          }
            if(!allCopies.contains(copy)){
                throw new IllegalStateException("This copy does not belong to book ID " + bookId);
            }
          copy.returnCopy();

          // Inventory restores availability
        availableCopies.addLast(copy);
    }
}
