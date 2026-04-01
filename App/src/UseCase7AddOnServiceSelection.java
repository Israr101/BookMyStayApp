import java.util.*;

// Add-On Service Model
class AddOnService {
    private String serviceName;
    private double cost;

    public AddOnService(String serviceName, double cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return serviceName + " (₹" + cost + ")";
    }
}

// Reservation Model (from previous use case perspective)
class Reservation {
    private String reservationId;
    private String customerName;
    private String roomType;

    public Reservation(String reservationId, String customerName, String roomType) {
        this.reservationId = reservationId;
        this.customerName = customerName;
        this.roomType = roomType;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getRoomType() {
        return roomType;
    }
}

// Add-On Service Manager
class AddOnServiceManager {

    // Map<ReservationId, List<AddOnService>>
    private Map<String, List<AddOnService>> serviceMap;

    public AddOnServiceManager() {
        serviceMap = new HashMap<>();
    }

    // Attach service to reservation
    public void addService(String reservationId, AddOnService service) {
        serviceMap.putIfAbsent(reservationId, new ArrayList<>());
        serviceMap.get(reservationId).add(service);
    }

    // Get services for reservation
    public List<AddOnService> getServices(String reservationId) {
        return serviceMap.getOrDefault(reservationId, new ArrayList<>());
    }

    // Calculate total additional cost
    public double calculateTotalCost(String reservationId) {
        double total = 0.0;
        for (AddOnService service : getServices(reservationId)) {
            total += service.getCost();
        }
        return total;
    }

    // Display services
    public void displayServices(String reservationId) {
        List<AddOnService> services = getServices(reservationId);

        if (services.isEmpty()) {
            System.out.println("No add-on services selected.");
            return;
        }

        System.out.println("Add-On Services for Reservation " + reservationId + ":");
        for (AddOnService service : services) {
            System.out.println("- " + service);
        }

        System.out.println("Total Add-On Cost: ₹" + calculateTotalCost(reservationId));
    }
}

// Main Class
public class UseCase7AddOnServiceSelection {

    public static void main(String[] args) {

        // Sample Reservations (Assume already confirmed in Use Case 6)
        Reservation r1 = new Reservation("RES101", "Alice", "Deluxe");
        Reservation r2 = new Reservation("RES102", "Bob", "Standard");

        // Add-On Services
        AddOnService breakfast = new AddOnService("Breakfast", 500);
        AddOnService spa = new AddOnService("Spa Access", 1500);
        AddOnService airportPickup = new AddOnService("Airport Pickup", 800);

        // Manager
        AddOnServiceManager manager = new AddOnServiceManager();

        // Guest selects services
        manager.addService(r1.getReservationId(), breakfast);
        manager.addService(r1.getReservationId(), spa);

        manager.addService(r2.getReservationId(), airportPickup);

        // Display results
        System.out.println("\n--- Reservation Details ---");
        System.out.println("Customer: " + r1.getCustomerName());
        manager.displayServices(r1.getReservationId());

        System.out.println();

        System.out.println("Customer: " + r2.getCustomerName());
        manager.displayServices(r2.getReservationId());
    }
}