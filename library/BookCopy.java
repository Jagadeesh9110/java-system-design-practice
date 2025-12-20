package library;

public class BookCopy {

    private final String copyId;
    private final String bookId;
    private BookCopyStatus status;

    public BookCopy(String copyId, String bookId) {
        if (copyId == null || copyId.isBlank()) {
            throw new IllegalArgumentException("Copy ID cannot be null or empty");
        }
        if (bookId == null || bookId.isBlank()) {
            throw new IllegalArgumentException("Book ID cannot be null or empty");
        }
        this.copyId = copyId;
        this.bookId = bookId;
        this.status = BookCopyStatus.AVAILABLE;
    }

    public String getCopyId() {
        return copyId;
    }

    public String getBookId() {
        return bookId;
    }

    public BookCopyStatus getStatus() {
        return status;
    }

    public boolean isAvailable() {
        return status == BookCopyStatus.AVAILABLE;
    }

    public void issue() {
        if (status != BookCopyStatus.AVAILABLE) {
            throw new IllegalStateException("Book copy is not available");
        }
        status = BookCopyStatus.ISSUED;
    }

    public void returnCopy() {
        if (status != BookCopyStatus.ISSUED) {
            throw new IllegalStateException("Book copy is not issued");
        }
        status = BookCopyStatus.AVAILABLE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BookCopy)) return false;
        BookCopy bookCopy = (BookCopy) o;
        return copyId.equals(bookCopy.copyId);
    }

    @Override
    public int hashCode() {
        return copyId.hashCode();
    }
}
