package library;

public class Main {
    public static void main(String[] args) {
        BookInventory inventory = new BookInventory();
    BorrowPolicyFactory policyFactory = new BorrowPolicyFactory();
    Library library = new Library(inventory, policyFactory);

    // add books
    // Example : inventory.addBook(new Book("4", "Computer Networks", "Andrew S. Tanenbaum", "978-0132126953", "Pearson"), 3);
   Book book1 = new Book( "B1", "Effective Java", "Joshua Bloch", "978-0134685991", "Addison-Wesley");

    inventory.addBook(book1,2); // adding 2 copies of book1

    // register users
    // Example: library.registerUser("u1", "Alice", UserType.STUDENT);
    library.registerUser("U1", "Jagadeeswar",UserType.STUDENT);
    library.registerUser("U2", "Alice", UserType.FACULTY);


    // borrow books
    // Example: library.borrowBook("u1", "1");

    // STUDENT borrowing
    BookCopy copy1= library.borrowBook("U1", "B1");
    BookCopy copy2= library.borrowBook("U1", "B1");

     // This should FAIL (only 2 copies exist)
    try {
        library.borrowBook("U1", "B1");
    } catch (Exception e) {
        System.out.println("Expected failure: " + e.getMessage());
    }
    // Return one copy
    library.returnBook("U1", copy1);
    
    // FACULTY borrowing
    BookCopy copy3= library.borrowBook("U2", "B1");
    System.out.println("System working as expected.");
    
    }
}
