package pharmacymanagement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
//Custom Exception Classes
class InvalidCustomerIdException extends Exception{
    public InvalidCustomerIdException(String message){
        super(message);
    }
}
class InvalidMedicineIdException extends Exception{
    public InvalidMedicineIdException(String message){
        super(message);
    }
}
class AuthenticationException extends Exception{
    public AuthenticationException(String message){
        super(message);
    }
}

class MedicineExpiryChecker extends Thread {
    private ArrayList<Medicine> inventory;

    public MedicineExpiryChecker(ArrayList<Medicine> inventory) {
        this.inventory = inventory;
    }

    @Override
    public void run() {
        while (true) {
            checkExpiredMedicines();
            try {
                Thread.sleep(20000); 
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted.");
            }
        }
    }
    private void checkExpiredMedicines() {
        System.out.println("\n--- Expiry Checker Running ---");
        Date currentDate = new Date();
        boolean foundExpired = false;

        for (Medicine medicine : inventory) {
            if (medicine.getExpiryDate().before(currentDate)) {
                System.out.println("Medicine ID: " + medicine.getId() + " (" + medicine.getName() + ") has expired!");
                foundExpired = true;
            }
        }

        if (!foundExpired) {
            System.out.println("No expired medicines found.");
        }
    }
}

//Abstract class for pharmacist and customer class
abstract class Person{
    private String name;
    private String contactInfo;
    public Person(String name,String contactInfo){
        this.name=name;
        this.contactInfo=contactInfo;
    }
    public String getName(){
        return name;
    }
    public String getContactInfo(){
        return contactInfo;
    }
    public abstract void displayDetails();
}
class Medicine{
    private double price;
    private String id;
    private String name;
    private int quantity;
    private Date expiryDate;

public Medicine(String id,String name,double price,int quantity,Date expiryDate){
this.id=id;
this.name=name;
this.price=price;
this.quantity=quantity;
this.expiryDate=expiryDate;
}
public String getId(){
    return id;
}
public String getName(){
    return name;
}
public double getPrice(){
    return price;
}
public int getQuantity(){
    return quantity;
}
public Date getExpiryDate(){
    return expiryDate;
}
public void setQuantity(int quantity){
    this.quantity=quantity;
}
public void updateQuantity(int newQuantity){
    if(newQuantity>=0){
        this.quantity=newQuantity;
        System.out.println("Quantity updated to:"+newQuantity);
    }
    else{
        System.out.println("Invalid quantity.Quantity must be a non-negative number");
    }
}
public boolean checkExpiry(){
    Date currentDate=new Date();
    return currentDate.after(this.expiryDate);
}

public String getDetails(){
    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
    return String.format("Medicine ID: %s\nName: %s\nPrice: %.2f\nQuantity: %d\nExpiryDate: %s\n",
    this.id,this.name,this.price,this.quantity,sdf.format(this.expiryDate));
}
public void displayMedicineDetails(){
    System.out.println(getDetails());
    if(checkExpiry()){
        System.out.println("The medicine is expired.");
    }
    else{
        System.out.println("The medicine is not expired.");
    }
}
}
class Purchase {
    private String id;
    private String name;
    private Medicine medicine;
    private int quantity;
    private Date purchaseDate;
    public Purchase(String purchaseId, Medicine medicine,int quantity,Date purchaseDate){
       this.id=purchaseId;
       this.name=name;
        this.medicine=medicine;
        this.quantity=quantity;
        this.purchaseDate=purchaseDate;
    }
    public String getDetails() {
        return String.format("Purchase ID: %s\nMedicine: %s\nQuantity: %d\nPurchase Date: %s\nTotal Cost: %.2f\n",
                this.id, this.medicine.getName(), this.quantity, this.purchaseDate.toString(),
                this.medicine.getPrice() * this.quantity);
    }
}
class Customer extends Person{
    private String customerId;
    private ArrayList<Purchase> purchaseHistory;
     public Customer(String name, String contactInfo, String customerId) { 
        super(name, contactInfo); 
        this.customerId = customerId;
        this.purchaseHistory = new ArrayList<>(); //initializing a new ArrayList and assigning it to the purchaseHistory variable
    }
     public String getCustomerId() {
        return customerId;
    }
      public void addPurchase(Purchase purchase) {
        purchaseHistory.add(purchase); // add purchase if anyone buys
    }
      public void displayPurchaseHistory() {
        System.out.println("Purchase History for Customer ID: " + customerId);
        for (Purchase purchase : purchaseHistory) {
            System.out.println(purchase.getDetails()); 
        }
    }
       @Override
    public void displayDetails() { // displays customer details
        System.out.println("Customer ID: " + customerId);
        System.out.println("Name: " + getName());
        System.out.println("Contact Info: " + getContactInfo());
    }
}
class Pharmacist extends Person{
 private String employeeId;
    private String password;
    private ArrayList<Medicine> inventory;
      public Pharmacist(String name, String contactInfo, String employeeId, String password) {
        super(name, contactInfo);
        this.employeeId = employeeId;
        this.password = password;
        this.inventory = PharmacyManagement.inventory;
    }
       public String getPassword() {
        return password;
    }
    public void addMedicine(Scanner scanner) {
        System.out.print("Enter Medicine ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter Medicine Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Price: ");
        double price = scanner.nextDouble();
        System.out.print("Enter Quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine(); 
        System.out.print("Enter Expiry Date (yyyy-MM-dd): ");
        String expiryDateString = scanner.nextLine();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date expiryDate = sdf.parse(expiryDateString);
            Medicine medicine = new Medicine(id, name, price, quantity, expiryDate);
            inventory.add(medicine);
            System.out.println("Medicine added successfully.");
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd.");
        }
    }
     public void updateMedicine(Scanner scanner) {
        System.out.print("Enter Medicine ID to update: ");
        String id = scanner.nextLine();
        try {
            Medicine medicineToUpdate = PharmacyManagement.findMedicineById(id);
            System.out.print("Enter new quantity: ");
            int newQuantity = scanner.nextInt();
            scanner.nextLine(); 
            medicineToUpdate.setQuantity(newQuantity);
            System.out.println("Medicine updated successfully.");
        } catch (InvalidMedicineIdException e) {
            System.out.println(e.getMessage());
        }
    }
     public void removeMedicine(Scanner scanner) {
        System.out.print("Enter Medicine ID to remove: ");
        String id = scanner.nextLine();
        try {
            Medicine medicineToRemove = PharmacyManagement.findMedicineById(id);
            inventory.remove(medicineToRemove);
            System.out.println("Medicine removed successfully.");
        } catch (InvalidMedicineIdException e) {
            System.out.println(e.getMessage());
        }
    }
  public void displayInventory() {
        System.out.println("Medicines managed by Pharmacist ID: " + employeeId);
        if (inventory.isEmpty()) {
            System.out.println("No medicines in inventory.");
        } else {
            for (Medicine medicine : inventory) {
                medicine.displayMedicineDetails();
            }
        }
    }
   @Override
    public void displayDetails() {
        System.out.println("Pharmacist Employee ID: " + employeeId);
        System.out.println("Name: " + getName());
        System.out.println("Contact Info: " + getContactInfo());
    }          
}
public class PharmacyManagement {
    public static ArrayList<Medicine> inventory = new ArrayList<>();
    public static ArrayList<Customer> customers = new ArrayList<>();
    public static Pharmacist pharmacist;
public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
          System.out.println("-------PHARMACY MANAGEMENT SYSTEM------");
 pharmacist = new Pharmacist("Maliha", "maliha@gmail.com", "EMP1", "pass123");
  // Sample medicines
        try {
            inventory.add(new Medicine("M1", "Napa", 1000, 100, sdf.parse("2028-09-30")));
            inventory.add(new Medicine("M2", "Rupa", 1500, 200, sdf.parse("2028-02-15")));
            inventory.add(new Medicine("M3", "Comet", 1000, 150, sdf.parse("2028-10-30")));
            inventory.add(new Medicine("M4", "Reticap", 1000, 72, sdf.parse("2028-12-30")));
            inventory.add(new Medicine("M5", "Osertil", 1000, 50, sdf.parse("2028-10-30")));
        } catch (ParseException e) {
            System.out.println("Error in parsing date format.");
        }
        // Sample customers
        customers.add(new Customer("Sayka", "sayka@gmail.com", "C1"));
        customers.add(new Customer("Sunjida", "sunjida@gmail.com", "C2"));
        customers.add(new Customer("Naima", "naima@gmail.com", "C3"));
        
        // Start the expiry checking thread
        MedicineExpiryChecker expiryChecker = new MedicineExpiryChecker(inventory);
        expiryChecker.start();  // Start the background thread for checking expired medicines

   
        boolean running = true;
        while (running) {
            System.out.println("\nChoose Role: ");
            System.out.println("1. Customer");
            System.out.println("2. Pharmacist");
            System.out.println("3. Exit");
            System.out.print("Select option: ");
            int roleChoice = scanner.nextInt();
            scanner.nextLine(); // newline
            switch (roleChoice) {
                case 1:
                    customerMenu(scanner);
                    break;
                case 2:
                    System.out.print("Enter pharmacist password: ");
                    String enteredPassword = scanner.nextLine();
                    try {
                        if (pharmacist.getPassword().equals(enteredPassword)) {
                            pharmacistMenu(scanner);
                        } else {
                            throw new AuthenticationException("Incorrect password. Access denied.");
                        }
                    } catch (AuthenticationException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case 3:
                    System.out.println("Exiting...");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
        scanner.close();
    }
    public static void customerMenu(Scanner scanner) {
        System.out.println("\n--- Customer Menu---");
        System.out.print("Enter Customer ID: ");
        String customerId = scanner.nextLine();
        try {
            Customer customer = findCustomerById(customerId);
            boolean customerRunning = true;
            while (customerRunning) {
                System.out.println("\n1. View Purchase History");
                System.out.println("2. Buy Medicine");
                System.out.println("3. Go Back");
                System.out.print("Choose option: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // newline
                switch (choice) {
                    case 1:
                        customer.displayPurchaseHistory();
                        break;
                    case 2:
                        addPurchaseToCustomer(scanner, customer);
                        break;
                    case 3:
                        customerRunning = false;
                        break;
                    default:
                        System.out.println("Invalid option. Try again.");
                }
            }
        } catch (InvalidCustomerIdException e) {
            System.out.println(e.getMessage());
        }
    }
     public static void pharmacistMenu(Scanner scanner) {
        System.out.println("\n--- Pharmacist Menu---");
        boolean pharmacistRunning = true;
        while (pharmacistRunning) {
            System.out.println("\n1. Add Medicine");
            System.out.println("2. View Inventory");
            System.out.println("3. Find Medicine by Name");
            System.out.println("4. Update Medicine Details");
            System.out.println("5. Remove Medicine");
            System.out.println("6. View Pharmacist Info");
            System.out.println("7. Go Back");
            System.out.print("Choose option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            switch (choice) {
                case 1:
                    pharmacist.addMedicine(scanner);
                    break;
                case 2:
                    pharmacist.displayInventory();
                    break;
                case 3:
                    findMedicineByName(scanner);
                    break;
                case 4:
                    pharmacist.updateMedicine(scanner);
                    break;
                case 5:
                    pharmacist.removeMedicine(scanner);
                    break;
                case 6:
                    pharmacist.displayDetails();
                    break;
                case 7:
                    pharmacistRunning = false;
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }
     private static void addPurchaseToCustomer(Scanner scanner, Customer customer) {
        System.out.print("Enter Medicine ID for purchase: ");
        String medicineId = scanner.nextLine();
        try {
            Medicine medicine = findMedicineById(medicineId);
            System.out.print("Enter quantity to purchase: ");
            int quantity = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            if (medicine.getQuantity() < quantity) {
                System.out.println("Insufficient quantity in stock.");
                return;
            }
            medicine.updateQuantity(medicine.getQuantity() - quantity);
            System.out.print("Enter Purchase ID: ");
            String purchaseId = scanner.nextLine();
            Purchase purchase = new Purchase(purchaseId, medicine, quantity, new Date());
            customer.addPurchase(purchase);
            System.out.println("Purchase added successfully.");
        } catch (InvalidMedicineIdException e) {
            System.out.println(e.getMessage());
        }
    }
      public static Medicine findMedicineById(String medicineId) throws InvalidMedicineIdException {
        for (Medicine medicine : inventory) {
            if (medicineId.equals(medicine.getId())) {
                return medicine;
            }
        }
        throw new InvalidMedicineIdException("Medicine ID " + medicineId + " not found.");
    }
 public static void findMedicineByName(Scanner scanner) {
        System.out.print("Enter Medicine Name: ");
        String name = scanner.nextLine();
        boolean found = false;
        for (Medicine medicine : inventory) {
            if (medicine.getName().equalsIgnoreCase(name)) {
                medicine.displayMedicineDetails();
                found = true;
            }
        }
        if (!found) {
            System.out.println("Medicine not found.");
        }
    }
  public static Customer findCustomerById(String customerId) throws InvalidCustomerIdException {
        for (Customer customer : customers) {
            if (customerId.equals(customer.getCustomerId())) {
                return customer;
            }
        }
        throw new InvalidCustomerIdException("Customer ID " + customerId + " not found.");
    }
        
    }
    
