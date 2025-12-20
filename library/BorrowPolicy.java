package library;

public interface BorrowPolicy {

    boolean canBorrow(User user);
    int getMaxLimit();
}


