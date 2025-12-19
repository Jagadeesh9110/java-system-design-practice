package library;

import java.util.HashMap;
import java.util.Map;


// Library is the ORCHESTRATOR. It uses BookInventory to manage books and copies, and User to manage user interactions.

public class Library {

    // Data owned by Library
    // User Management
    // Book Inventory Management

    private final Map<String, User> users = new HashMap<>(); // Registers users by userId
    private final BookInventory inventory; // Inventory (delegated responsibility)

    public Library(BookInventory inventory) {
        if (inventory == null) {
            throw new IllegalArgumentException("BookInventory cannot be null");
        }
        this.inventory=inventory;
    }
   
    // Registers a new user into the library system.
    public void registerUser(String userId, String name){
        if(userId==null || userId.isBlank()){
            throw new IllegalArgumentException("User ID cannot be null or blank");
        }
        if(name==null || name.isBlank()){
            throw new IllegalArgumentException("User name cannot be null or blank");
        }
        if(users.containsKey(userId)){
            throw new IllegalStateException("User with ID " + userId + " already exists");
        }
        User user = new User(userId, name);
        users.put(userId, user);
    }

    // Borrow a book for a user
    public BookCopy borrowBook(String userId, String bookId){

        // Validate userId & bookId
        if(userId==null || userId.isBlank()){
            throw new IllegalArgumentException("User ID cannot be null or blank");
        }
        if(bookId==null || bookId.isBlank()){
            throw new IllegalArgumentException("Book ID cannot be null or blank");
        }

        // Fetch User
        User user= users.get(userId);
        if(user==null){
            throw new IllegalStateException("User with ID " + userId + " does not exist registered");
        }
        BookCopy copy = null;
        try{
        // Allocate copy from inventory
        copy = inventory.allocateCopy(bookId);
        // Let user borrow the allocated copy
        user.borrowCopy(copy);

        return copy;
        }catch(RuntimeException e){
            // Rollback inventory if user borrowing fails
             if (copy != null) {
                inventory.releaseCopy(copy);
            }
            throw e;
        }
        
    }
    
    // Return a previously borrowed book copy.
    public void returnBook(String userId, BookCopy copy){
        // Validate userId & copy
        if(userId==null || userId.isBlank()){
            throw new IllegalArgumentException("User ID cannot be null or blank");
        }
        if(copy==null){
            throw new IllegalArgumentException("Book copy cannot be null");
        }

        // Fetch User
        User user= users.get(userId);
        if(user==null){
            throw new IllegalStateException("User with ID " + userId + " does not exist registered");
        }

       // User returns copy
        user.returnCopy(copy);

       //  Inventory releases copy
        inventory.releaseCopy(copy);
           
    }

    
}
