
public class Order {

    private Customer customer;
    private String bookTitle;
    private int quantity;

    public Order(Customer customer, String bookTitle, int quantity) {
        this.customer = customer;
        this.bookTitle = bookTitle;
        this.quantity = quantity;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "Order{"
                + "customer=" + customer
                + ", bookTitle='" + bookTitle + '\''
                + ", quantity=" + quantity
                + '}';
    }
}
