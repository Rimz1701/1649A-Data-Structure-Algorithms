import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class Main {
    private static Stack<Book> bookStack = new Stack<>();
    private static List<Order> currentOrders = new ArrayList<>();
    private static List<Order> allOrders = new ArrayList<>(); // List of all orders stored

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            try {
                // Display menu
                System.out.println("\nMenu:");
                System.out.println("1. Add a new book");
                System.out.println("2. Buy books");
                System.out.println("3. Display all books");
                System.out.println("4. Sort books");
                System.out.println("5. Show current order");
                System.out.println("6. Search order");
                System.out.println("7. Exit");
                System.out.print("Enter your choice (1-7): ");
                String input = scanner.nextLine().trim(); // Trim the input
                int choice = Integer.parseInt(input);

                // Get and validate input
                try {
                    choice = Integer.parseInt(input);
                    if (choice < 1 || choice > 7) {
                        System.out.println("Please enter a number between 1-7");
                        continue;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number between 1-7");
                    continue;
                }

                switch (choice) {
                    case 1:
                        // Add a new book
                        addNewBook(scanner);
                        break;
                    case 2:
                        // Buy books
                        buyBooks(scanner);
                        break;
                    case 3:
                        // Display all books
                        displayAllBooks();
                        break;
                    case 4:
                        // Sort books
                        System.out.println("Sort by: 1. Title 2. ID");
                        int sortChoice = Integer.parseInt(scanner.nextLine().trim());
                        if (sortChoice == 1) {
                            sortBooks("DSA.csv", "title");
                        } else if (sortChoice == 2) {
                            sortBooks("DSA.csv", "ID");
                        } else {
                            System.out.println("Invalid choice. Please enter 1 or 2.");
                        }
                        break;
                    case 5:
                        // Show current order
                        showOrder(scanner);
                        break;
                    case 6:
                        // Search order
                        searchOrder(scanner); // Call Search Function
                        break;
                    case 7:
                        exit = true;
                        System.out.println("Exiting...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        scanner.close();
    }

    // Add New Book
    private static void addNewBook(Scanner scanner) {
        // Find maximum ID from existing books
        int maxId = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader("DSA.csv"))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    try {
                        int currentId = Integer.parseInt(parts[3]);
                        maxId = Math.max(maxId, currentId);
                    } catch (NumberFormatException e) {
                        continue;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV: " + e.getMessage());
        }

        // Input for Book
        while (true) {
            String title;
            while (true) {
                System.out.println("Enter book title:");
                title = scanner.nextLine();
                if (!title.isEmpty()) {
                    break;
                } else {
                    System.out.println("Book title cannot be empty. Please enter a valid title.");
                }
            }

            // author
            String author;
            while (true) {
                System.out.println("Enter book author:");
                author = scanner.nextLine();
                if (!author.isEmpty()) {
                    break;
                } else {
                    System.out.println("Book author cannot be empty. Please enter a valid author.");
                }
            }

            // Year
            int publicationYear;
            while (true) {
                System.out.println("Enter book publication year:");
                if (scanner.hasNextInt()) {
                    publicationYear = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    if (publicationYear > 0 && publicationYear <= Calendar.getInstance().get(Calendar.YEAR)) {
                        break;
                    } else {
                        System.out.println("Please enter a valid year (1-" + Calendar.getInstance().get(Calendar.YEAR) + ")");
                    }
                } else {
                    System.out.println("Invalid publication year. Please enter a valid year.");
                    scanner.nextLine(); // Consume invalid input
                }
            }

            // Quantity
            int quantity;
            while (true) {
                System.out.println("Enter book quantity:");
                if (scanner.hasNextInt()) {
                    quantity = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    if (quantity >= 0) {
                        break;
                    } else {
                        System.out.println("Quantity cannot be negative. Please enter a valid number.");
                    }
                } else {
                    System.out.println("Invalid quantity. Please enter a valid number.");
                    scanner.nextLine(); // Consume invalid input
                }
            }

            maxId++; // Increment ID for new book
            Book book = new Book(title, author, publicationYear, quantity);
            book.setId(maxId);
            bookStack.push(book);
            System.out.println("Book added with ID: " + maxId);

            System.out.println("Do you want to add another book? (yes/no)");
            String anotherBook = scanner.nextLine();
            if (!anotherBook.equalsIgnoreCase("yes")) {
                break;
            }
        }

        // Read existing books
        List<String> existingBooks = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("DSA.csv"))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                if (!line.trim().isEmpty()) {
                    existingBooks.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading existing books: " + e.getMessage());
        }

        // Write back to CSV (LIFO)
        try (FileWriter writer = new FileWriter("DSA.csv")) {
            writer.write("Title,Author,Year,ID,Quantity\n");

            // Write new books first (LIFO)
            Stack<Book> tempStack = new Stack<>();
            while (!bookStack.isEmpty()) {
                tempStack.push(bookStack.pop());
            }
            while (!tempStack.isEmpty()) {
                Book b = tempStack.pop();
                writer.write(String.format("%s,%s,%d,%d,%d%n",
                    b.getTitle(),
                    b.getAuthor(),
                    b.getPublicationYear(),
                    b.getId(),
                    b.getQuantity()));
            }

            // Write existing books
            for (String book : existingBooks) {
                writer.write(book + "\n");
            }
            System.out.println("Book data successfully saved to DSA.csv");
        } catch (IOException e) {
            System.err.println("Error writing to CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Order Book
    private static void buyBooks(Scanner scanner) {
        // Input for Order
        System.out.println("Enter customer name:");
        String customerName = scanner.nextLine();
        System.out.println("Enter customer email:");
        String customerEmail = scanner.nextLine();

        int customerPhoneNumber;
        while (true) {
            System.out.println("Enter customer phone number:");
            if (scanner.hasNextInt()) {
                customerPhoneNumber = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                break;
            } else {
                System.out.println("Invalid phone number. Please enter a valid phone number.");
                scanner.nextLine(); // Consume invalid input
            }
        }

        Customer customer = new Customer(customerName, customerEmail, customerPhoneNumber);

        while (true) {
            String bookTitle;
            int orderQuantity = 0; // Initialize orderQuantity
            while (true) {
                System.out.println("Enter book title:");
                bookTitle = scanner.nextLine();

                // Check if the book exists and has enough quantity
                boolean bookExists = false;
                int quantityInFile = 0;
                try (BufferedReader reader = new BufferedReader(new FileReader("DSA.csv"))) {
                    String line;
                    boolean isFirstLine = true;
                    while ((line = reader.readLine()) != null) {
                        if (isFirstLine) {
                            isFirstLine = false;
                            continue;
                        }
                        String[] columns = line.split(",");
                        String bookTitleInFile = columns[0];
                        quantityInFile = Integer.parseInt(columns[4]);

                        if (bookTitleInFile.equals(bookTitle)) {
                            bookExists = true;
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (bookExists) {
                    if (quantityInFile == 0) {
                        System.out.println("Book is sold out. Please choose another book.");
                    } else {
                        while (true) {
                            System.out.println("Enter quantity:");
                            if (scanner.hasNextInt()) {
                                orderQuantity = scanner.nextInt();
                                scanner.nextLine(); // Consume newline

                                if (orderQuantity > quantityInFile) {
                                    System.out.println("Quantity not enough. Please select one of the following options:");
                                    System.out.println("1. Re-enter quantity");
                                    System.out.println("2. Choose another book");
                                    int subChoice = scanner.nextInt();
                                    scanner.nextLine(); // Consume newline

                                    if (subChoice == 1) {
                                        continue; // Go back and re-enter quantity
                                    } else if (subChoice == 2) {
                                        break; // Go back and re-enter Name of book
                                    } else {
                                        System.out.println("Invalid option. Please select again.");
                                    }
                                } else {
                                    break;
                                }
                            } else {
                                System.out.println("Invalid quantity. Please enter a valid quantity.");
                                scanner.nextLine(); // Consume invalid input
                            }
                        }
                    }
                } else {
                    System.out.println("This book does not exist. Please re-enter.");
                }

                if (bookExists && quantityInFile > 0 && orderQuantity <= quantityInFile) {
                    break;
                }
            }

            currentOrders.add(new Order(customer, bookTitle, orderQuantity));

            System.out.println("Do you want to order another book? (yes/no)");
            String anotherBook = scanner.nextLine();
            if (!anotherBook.equalsIgnoreCase("yes")) {
                break;
            }
        }

        // Add current orders to all orders
        allOrders.addAll(currentOrders);
    }

    // Show book in CSV
    private static void displayAllBooks() {
        // Display all books
        System.out.println("\nBooks in DSA.csv:");
        try (BufferedReader reader = new BufferedReader(new FileReader("DSA.csv"))) {
            String line;
            boolean isFirstLine = true;
            int lineNumber = 0;
            // Print table header
            System.out.printf("%-30s %-20s %-20s %-10s %-10s%n",
                    "Title", "Author", "Year", "ID", "Quantity");
            System.out.println("-".repeat(90));  // Separator line
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] columns = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                // Validate line
                if (columns.length < 5) {
                    System.out.printf("Warning: Skipping malformed line %d: '%s' (Expected 5 columns, found %d)%n",
                            lineNumber, line, columns.length);
                    continue;
                }

                try {
                    System.out.printf("%-30s %-20s %-20s %-10s %-10s%n",
                            columns[0].trim(),
                            columns[1].trim(),
                            columns[2].trim(),
                            columns[3].trim(),
                            columns[4].trim());
                } catch (Exception e) {
                    System.out.printf("Error processing line %d: %s%n", lineNumber, line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }
    }

    // Merge Sort
    private static void sortBooks(String filePath, String criteria) {
        List<String[]> books = new ArrayList<>();
        String header = null;

        // Read the CSV file
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    header = line;
                    isFirstLine = false;
                    continue;
                }
                String[] columns = line.split(",");
                books.add(columns);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Sort the books by the given criteria
        if (criteria.equals("title")) {
            books.sort(Comparator.comparing(o -> o[0]));
        } else if (criteria.equals("ID")) {
            books.sort(Comparator.comparingInt(o -> Integer.parseInt(o[3])));
        }

        // Write the sorted books back to the CSV file
        try (FileWriter writer = new FileWriter(filePath)) {
            if (header != null) {
                writer.write(header + "\n");
            }
            for (String[] book : books) {
                writer.write(String.join(",", book) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Books sorted by " + criteria + " and saved to " + filePath);
    }


    // Show order.csv ( FIFO )
    private static void showOrder(Scanner scanner) {
        if (currentOrders.isEmpty()) {
            System.out.println("No current orders.");
            return;
        }
    
        System.out.println("\nCurrent Orders:");
    
        // Iterate through each customer in the currentOrders
        for (int i = 0; i < currentOrders.size(); i++) {
            Order order = currentOrders.get(i);
            Customer customer = order.getCustomer();
    
            System.out.println("\nCustomer " + (i + 1) + ":");
            System.out.println("Name: " + customer.getName());
            System.out.println("Email: " + customer.getEmail());
            System.out.println("Phone: " + customer.getPhoneNumber());
            System.out.printf("%-30s %-10s%n", "Book Title", "Quantity");
            System.out.println("-".repeat(40));
            System.out.printf("%-30s %-10d%n", order.getBookTitle(), order.getQuantity());
    
            System.out.print("Do you want to save this order? (yes/no): ");
            String confirmation = scanner.nextLine();
            if (confirmation.equalsIgnoreCase("yes")) {
                List<Order> singleOrder = new ArrayList<>(); 
                singleOrder.add(order);
    
                saveSingleOrder(singleOrder); // Save current customer orders
                updateBookQuantity(singleOrder); // Update book quantity
            } 
            scanner.nextLine();
    
            // Continue processing next order
        }
    
        // Notify if there are unsaved orders
        if (!currentOrders.isEmpty()) {
            System.out.println("All remaining orders are not saved.");
        }
    }

    // Save order into order.csv
    private static void saveOrders() {
        // Write order data to order.csv outside src folder
        try {
            File orderFile = new File("order.csv");
            FileWriter orderWriter = new FileWriter(orderFile, true); // Open file in append mode

            // Check if the file exists or not
            if (orderFile.length() == 0) {
                // If the file does not exist, write the title line.
                orderWriter.append("Customer Name,Email,Phone Number,Book Title,Quantity\n");
            }

            for (Order order : allOrders) {
                orderWriter.append(order.getCustomer().getName()).append(',')
                        .append(order.getCustomer().getEmail()).append(',')
                        .append(String.valueOf(order.getCustomer().getPhoneNumber())).append(',')
                        .append(order.getBookTitle()).append(',')
                        .append(String.valueOf(order.getQuantity())).append('\n');
            }

            orderWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Order data saved to order.csv");

        // Update book quantity in DSA.csv
        List<Book> books = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("DSA.csv"))) {
            String line;
            boolean isFirstLine = true; // Variable to check first line
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // Mark first line read
                    continue; // Skip header line
                }
                String[] columns = line.split(",");
                String bookTitleInFile = columns[0];
                String authorInFile = columns[1];
                int publicationYearInFile;
                try {
                    publicationYearInFile = Integer.parseInt(columns[2]);
                } catch (NumberFormatException e) {
                    publicationYearInFile = -1; // Handle non-integer publication year
                }
                int idInFile = Integer.parseInt(columns[3]);
                int quantityInFileUpdated = Integer.parseInt(columns[4]);

                for (Order order : allOrders) {
                    if (bookTitleInFile.equals(order.getBookTitle())) {
                        quantityInFileUpdated -= order.getQuantity();
                    }
                }

                books.add(new Book(bookTitleInFile, authorInFile, publicationYearInFile, quantityInFileUpdated));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Write updated book data back to DSA.csv
        try (FileWriter bookWriter = new FileWriter("DSA.csv")) {
            bookWriter.append("Book Title,Author,Publication Year,ID,Quantity\n"); // Re-write the subject line
            for (Book b : books) {
                bookWriter.append(b.getTitle()).append(',')
                        .append(b.getAuthor()).append(',')
                        .append(String.valueOf(b.getPublicationYear())).append(',')
                        .append(String.valueOf(b.getId())).append(',')
                        .append(String.valueOf(b.getQuantity())).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Book quantity updated in DSA.csv");

        // Clear all orders after saving
    }


    // Save new ord into order.csv
    private static void saveSingleOrder(List<Order> orders) {
        // Write order data to order.csv outside src folder
        try {
            File orderFile = new File("order.csv");
            FileWriter orderWriter = new FileWriter(orderFile, true); // Open file in append mode

            // Check if the file exists or not
            if (orderFile.length() == 0) {
                // If the file does not exist, write the title line
                orderWriter.append("Customer Name,Email,Phone Number,Book Title,Quantity\n");
            }

            for (Order order : orders) {
                orderWriter.append(order.getCustomer().getName()).append(',')
                        .append(order.getCustomer().getEmail()).append(',')
                        .append(String.valueOf(order.getCustomer().getPhoneNumber())).append(',')
                        .append(order.getBookTitle()).append(',')
                        .append(String.valueOf(order.getQuantity())).append('\n');
            }

            orderWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Order data saved to order.csv");
    }


    private static void updateBookQuantity(List<Order> orders) {
        // Update book quantity in DSA.csv
        List<Book> books = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("DSA.csv"))) {
            String line;
            boolean isFirstLine = true; // Variable to check first line
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // Mark first line read
                    continue; // Skip header line
                }
                String[] columns = line.split(",");
                String bookTitleInFile = columns[0];
                String authorInFile = columns[1];
                int publicationYearInFile;
                try {
                    publicationYearInFile = Integer.parseInt(columns[2]);
                } catch (NumberFormatException e) {
                    System.err.println("Publication year conversion error:" + e.getMessage());
                    publicationYearInFile = -1; // Handle non-integer publication year
                }
                int idInFile;
                try {
                    idInFile = Integer.parseInt(columns[3]);
                } catch (NumberFormatException e) {
                    System.err.println("ID conversion error: " + e.getMessage());
                    idInFile = -1; // Handle non-integer ID
                }
                int quantityInFileUpdated;
                try {
                    quantityInFileUpdated = Integer.parseInt(columns[4]);
                } catch (NumberFormatException e) {
                    System.err.println("Quantity conversion error: " + e.getMessage());
                    quantityInFileUpdated = 0; // Handle non-integer quantity
                }
    
                for (Order order : orders) {
                    if (bookTitleInFile.equals(order.getBookTitle())) {
                        quantityInFileUpdated -= order.getQuantity();
                    }
                }
    
                books.add(new Book(bookTitleInFile, authorInFile, publicationYearInFile, quantityInFileUpdated));
            }
    
            // Write updated book data back to DSA.csv
            try (FileWriter bookWriter = new FileWriter("DSA.csv")) {
                bookWriter.append("Book Title,Author,Publication Year,ID,Quantity\n"); //Re-write the subject line
                for (Book b : books) {
                    bookWriter.append(b.getTitle()).append(',')
                            .append(b.getAuthor()).append(',')
                            .append(String.valueOf(b.getPublicationYear())).append(',')
                            .append(String.valueOf(b.getId())).append(',')
                            .append(String.valueOf(b.getQuantity())).append('\n');
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    
            System.out.println("Book quantity updated in DSA.csv"); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Linear Search
    private static void searchOrder(Scanner scanner) {
        System.out.println("Search by: 1. Customer Name 2. Book Title");
        int searchChoice;
        try {
            searchChoice = Integer.parseInt(scanner.nextLine().trim());
            if (searchChoice == 1) {
                System.out.println("Enter customer name:");
                String customerName = scanner.nextLine();
                searchOrderByName(customerName);
            } else if (searchChoice == 2) {
                System.out.println("Enter book title:");
                String bookTitle = scanner.nextLine();
                searchOrderByTitle(bookTitle);
            } else {
                System.out.println("Invalid choice. Please enter 1 or 2.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
    
    private static void searchOrderByName(String customerName) {
        boolean found = false;
        try (BufferedReader reader = new BufferedReader(new FileReader("order.csv"))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] columns = line.split(",");
                if (columns.length >= 5 && columns[0].equalsIgnoreCase(customerName)) {
                    String customer = columns[0];
                    String bookTitle = columns[3];
                    int quantity = Integer.parseInt(columns[4]);
                    System.out.printf("Customer: %s, Books: %s, Quantity: %d%n", customer, bookTitle, quantity);
                    found = true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading order.csv: " + e.getMessage());
        }
        if (!found) {
            System.out.println("No orders found for customer: " + customerName);
        }
    }
    
    private static void searchOrderByTitle(String bookTitle) {
        boolean found = false;
        try (BufferedReader reader = new BufferedReader(new FileReader("order.csv"))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] columns = line.split(",");
                if (columns.length >= 5 && columns[3].equalsIgnoreCase(bookTitle)) {
                    String customer = columns[0];
                    String book = columns[3];
                    int quantity = Integer.parseInt(columns[4]);
                    System.out.printf("Customer: %s, Books: %s, Quantity: %d%n", customer, book, quantity);
                    found = true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading order.csv: " + e.getMessage());
        }
        if (!found) {
            System.out.println("No orders found for book: " + bookTitle);
        }
    }
}