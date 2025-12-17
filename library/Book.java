package library;

public final class Book {
    private final String bookId;
    private final String title;
    private final String author;
    private final String isbn;
    private final String publisher;

    public Book(String bookId, String title, String author, String isbn, String publisher) {
        if(bookId == null || bookId.isBlank()){
            throw new IllegalArgumentException("Book ID cannot be null or empty");  
        }
        if(title == null || title.isBlank()){
            throw new IllegalArgumentException("Title cannot be null or empty");  
        }
        if(author == null || author.isBlank()){
            throw new IllegalArgumentException("Author cannot be null or empty");  
        }
        if(isbn == null || isbn.isBlank()){
            throw new IllegalArgumentException("ISBN cannot be null or empty");  
        }
        if(publisher == null || publisher.isBlank()){
            throw new IllegalArgumentException("Publisher cannot be null or empty");  
        }
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publisher = publisher;
    }
    public String getBookId(){
        return bookId;
    }
    public String getTitle(){
        return title;
    }
    public String getAuthor(){
        return author;
    }
    public String getIsbn(){
        return isbn;
    }
    public String getPublisher(){
        return publisher;
    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        return bookId.equals(book.bookId);
    }

    @Override
    public int hashCode(){
        return bookId.hashCode();
    }

    @Override
    public String toString(){
        return "Book{" +
                "bookId='" + bookId + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", publisher='" + publisher + '\'' +
                '}';
    }

}