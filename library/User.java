package library;

import java.util.Set;
import java.util.HashSet;

public class User {
    private final String userId;
    private final String name;
    private final Set<BookCopy> borrowedCopies=new HashSet<>();
    private static final int MAX_BORROW_LIMIT = 5;


    public User(String userId, String name){
        if(userId==null || userId.isBlank()){
            throw new IllegalArgumentException("User ID cannot be null or blank");
        }
        if(name==null || name.isBlank()){
            throw new IllegalArgumentException("User name cannot be null or blank");
        }
        this.userId=userId;
        this.name=name;

    }

    public String getUserId(){
        return userId;
    }

    public boolean canBorrow(){
        return borrowedCopies.size()<MAX_BORROW_LIMIT;
    }

    private boolean hasAlreadyBorrowed(BookCopy copy){
        return borrowedCopies.contains(copy);
    }

    public void borrowCopy(BookCopy copy){
        if(copy==null){
            throw new IllegalArgumentException("Book copy cannot be null");
        }
        if(!this.canBorrow()){
            throw new IllegalStateException("User cannot borrow more than " + MAX_BORROW_LIMIT + " copies");
        }
        if(hasAlreadyBorrowed(copy)){
            throw new IllegalStateException("User has already borrowed this copy");
        }
        borrowedCopies.add(copy);

    }

    public void returnCopy(BookCopy copy){
        if(copy==null){
            throw new IllegalArgumentException("Book copy cannot be null");
        }
        if (!borrowedCopies.remove(copy)) {
            throw new IllegalStateException("User does not have this book copy");
        }
    }

    public Set<BookCopy> getBorrowedCopies(){
        return new HashSet<>(borrowedCopies);
    }
}
