import java.util.*;

// Booking Request Model
class BookingRequest {
    String customerName;
    String roomType;

    public BookingRequest(String customerName, String roomType) {
        this.customerName = customerName;
        this.roomType = roomType;
    }
}

// Inventory Service
class InventoryService {
    private Map<String, Integer> roomInventory;

    public InventoryService() {
        roomInventory = new HashMap<>();
        roomInventory.put("Deluxe", 2);
        roomInventory.put("Standard", 3);
        roomInventory.put("Suite", 1);
    }

    public boolean isAvailable(String roomType) {
        return roomInventory.getOrDefault(roomType, 0) > 0;
    }

    public void decrement(String roomType) {
        roomInventory.put(roomType, roomInventory.get(roomType) - 1);
    }

    public void displayInventory() {
        System.out.println("\nCurrent Inventory:");
        for (String type : roomInventory.keySet()) {
            System.out.println(type + " -> " + roomInventory.get(type));
        }
    }
}

// Booking Service
class BookingService {
    private Queue<BookingRequest> requestQueue;
    private InventoryService inventoryService;

    // Track all allocated room IDs globally
    private Set<String> allocatedRoomIds;

    // Map room type -> set of room IDs
    private Map<String, Set<String>> roomTypeMap;

    private int roomCounter = 1;

    public BookingService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
        this.requestQueue = new LinkedList<>();
        this.allocatedRoomIds = new HashSet<>();
        this.roomTypeMap = new HashMap<>();
    }

    public void addRequest(BookingRequest request) {
        requestQueue.offer(request);
    }

    private String generateRoomId(String roomType) {
        String roomId;
        do {
            roomId = roomType.substring(0, 2).toUpperCase() + roomCounter++;
        } while (allocatedRoomIds.contains(roomId));
        return roomId;
    }

    public void processBookings() {
        while (!requestQueue.isEmpty()) {
            BookingRequest request = requestQueue.poll();
            System.out.println("\nProcessing booking for: " + request.customerName);

            if (!inventoryService.isAvailable(request.roomType)) {
                System.out.println("No rooms available for type: " + request.roomType);
                continue;
            }

            // Atomic allocation logic
            String roomId = generateRoomId(request.roomType);

            // Ensure uniqueness
            allocatedRoomIds.add(roomId);

            // Map room type to allocated IDs
            roomTypeMap.putIfAbsent(request.roomType, new HashSet<>());
            roomTypeMap.get(request.roomType).add(roomId);

            // Update inventory immediately
            inventoryService.decrement(request.roomType);

            // Confirm reservation
            System.out.println("Booking Confirmed!");
            System.out.println("Customer: " + request.customerName);
            System.out.println("Room Type: " + request.roomType);
            System.out.println("Assigned Room ID: " + roomId);
        }
    }

    public void displayAllocations() {
        System.out.println("\nRoom Allocations:");
        for (String type : roomTypeMap.keySet()) {
            System.out.println(type + " -> " + roomTypeMap.get(type));
        }
    }
}

// Main Class
public class UseCase6RoomAllocationService {
    public static void main(String[] args) {

        InventoryService inventoryService = new InventoryService();
        BookingService bookingService = new BookingService(inventoryService);

        // Add booking requests (FIFO)
        bookingService.addRequest(new BookingRequest("Alice", "Deluxe"));
        bookingService.addRequest(new BookingRequest("Bob", "Standard"));
        bookingService.addRequest(new BookingRequest("Charlie", "Deluxe"));
        bookingService.addRequest(new BookingRequest("David", "Suite"));
        bookingService.addRequest(new BookingRequest("Eve", "Suite")); // should fail

        // Process bookings
        bookingService.processBookings();

        // Display results
        bookingService.displayAllocations();
        inventoryService.displayInventory();
    }
}