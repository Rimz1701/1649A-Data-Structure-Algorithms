public class Book {
    private String title;
    private String author;
    private int publicationYear;
    private int id;
    private int quantity;

    // Constructor
    public Book(String title, String author, int publicationYear, int quantity) {
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.quantity = quantity;
    }

    // ID getter/setter
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    // Existing getters/setters
    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}