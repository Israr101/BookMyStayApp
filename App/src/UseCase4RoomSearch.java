import java.util.*;

// Room Domain Model
class Room {
    private String type;
    private double price;
    private List<String> amenities;

    public Room(String type, double price, List<String> amenities) {
        this.type = type;
        this.price = price;
        this.amenities = amenities;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void displayDetails() {
        System.out.println("Room Type: " + type);
        System.out.println("Price: ₹" + price);
        System.out.println("Amenities: " + amenities);
        System.out.println("----------------------------");
    }
}

// Inventory (State Holder)
class Inventory {
    private Map<String, Integer> roomAvailability;

    public Inventory() {
        roomAvailability = new HashMap<>();
    }

    public void addRoom(String type, int count) {
        roomAvailability.put(type, count);
    }

    // Read-only access
    public int getAvailability(String type) {
        return roomAvailability.getOrDefault(type, 0);
    }

    public Map<String, Integer> getAllAvailability() {
        // Return a copy (defensive programming)
        return new HashMap<>(roomAvailability);
    }
}

// Search Service (Read-only logic)
class SearchService {
    private Inventory inventory;
    private Map<String, Room> roomCatalog;

    public SearchService(Inventory inventory, Map<String, Room> roomCatalog) {
        this.inventory = inventory;
        this.roomCatalog = roomCatalog;
    }

    public void searchAvailableRooms() {
        System.out.println("\nAvailable Rooms:\n");

        Map<String, Integer> availabilityMap = inventory.getAllAvailability();

        boolean found = false;

        for (String roomType : availabilityMap.keySet()) {
            int availableCount = availabilityMap.get(roomType);

            // Validation: Only show available rooms
            if (availableCount > 0) {
                Room room = roomCatalog.get(roomType);

                if (room != null) { // Defensive check
                    room.displayDetails();
                    System.out.println("Available Units: " + availableCount);
                    System.out.println("============================");
                    found = true;
                }
            }
        }

        if (!found) {
            System.out.println(" No rooms available at the moment.");
        }
    }
}

// Main Class
public class UseCase4RoomSearch {
    public static void main(String[] args) {

        // Step 1: Create Room Catalog (Domain Objects)
        Map<String, Room> roomCatalog = new HashMap<>();

        roomCatalog.put("Single", new Room(
                "Single",
                2000,
                Arrays.asList("WiFi", "TV", "AC")
        ));

        roomCatalog.put("Double", new Room(
                "Double",
                3500,
                Arrays.asList("WiFi", "TV", "AC", "Mini Bar")
        ));

        roomCatalog.put("Suite", new Room(
                "Suite",
                6000,
                Arrays.asList("WiFi", "TV", "AC", "Mini Bar", "Jacuzzi")
        ));

        // Step 2: Setup Inventory (State Holder)
        Inventory inventory = new Inventory();
        inventory.addRoom("Single", 5);
        inventory.addRoom("Double", 0); // Will be filtered out
        inventory.addRoom("Suite", 2);

        // Step 3: Create Search Service
        SearchService searchService = new SearchService(inventory, roomCatalog);

        // Step 4: Guest searches rooms (Read-only operation)
        searchService.searchAvailableRooms();
    }
}