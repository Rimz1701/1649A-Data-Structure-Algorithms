
public class Customer {

    private static int nextId = 1; // Static variable to keep track of the next ID
    private String name;
    private String email;
    private int phoneNumber;
    private int id;

    public Customer(String name, String email, int phoneNumber) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.id = nextId++; // Assign the current value of nextId and then increment it
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Customer{"
                + "name='" + name + '\''
                + ", email='" + email + '\''
                + ", phoneNumber=" + phoneNumber
                + ", id=" + id
                + '}';
    }
}
