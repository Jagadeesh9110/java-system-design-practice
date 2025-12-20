package library;

public class StudentBorrowPolicy implements BorrowPolicy {
    private static final int MAX_BORROW_LIMIT = 5;

    @Override
    public boolean canBorrow(User user) {
        return user.getBorrowedCount() < MAX_BORROW_LIMIT;
    }

    @Override
    public int getMaxLimit() {
        return MAX_BORROW_LIMIT;
    }
}