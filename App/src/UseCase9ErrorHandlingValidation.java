import java.util.HashMap;
import java.util.Map;

// Custom Exception for Invalid Booking
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

// Room Inventory Manager
class RoomInventory {
    private Map<String, Integer> rooms = new HashMap<>();

    public RoomInventory() {
        rooms.put("Single", 5);
        rooms.put("Double", 3);
        rooms.put("Suite", 2);
    }

    public boolean isRoomTypeValid(String roomType) {
        return rooms.containsKey(roomType);
    }

    public int getAvailableRooms(String roomType) {
        return rooms.getOrDefault(roomType, 0);
    }

    public void bookRoom(String roomType, int count) throws InvalidBookingException {
        // Guard system state before modification
        int available = getAvailableRooms(roomType);

        if (available < count) {
            throw new InvalidBookingException("Not enough rooms available.");
        }

        rooms.put(roomType, available - count);
    }

    public void displayInventory() {
        System.out.println("\nCurrent Room Availability:");
        for (String type : rooms.keySet()) {
            System.out.println(type + ": " + rooms.get(type));
        }
    }
}

// Validator Class
class BookingValidator {

    public static void validate(String roomType, int count, RoomInventory inventory)
            throws InvalidBookingException {

        // Fail-fast validation

        if (roomType == null || roomType.trim().isEmpty()) {
            throw new InvalidBookingException("Room type cannot be empty.");
        }

        if (!inventory.isRoomTypeValid(roomType)) {
            throw new InvalidBookingException("Invalid room type selected.");
        }

        if (count <= 0) {
            throw new InvalidBookingException("Booking count must be greater than zero.");
        }

        if (inventory.getAvailableRooms(roomType) <= 0) {
            throw new InvalidBookingException("Selected room type is fully booked.");
        }
    }
}

// Main Class
public class UseCase9ErrorHandlingValidation {

    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();

        // Sample Inputs (Valid + Invalid cases)
        String[] roomTypes = {"Single", "Double", "Suite", "Deluxe"};
        int[] counts = {2, 4, -1, 1};

        for (int i = 0; i < roomTypes.length; i++) {
            try {
                System.out.println("\nProcessing booking: " +
                        roomTypes[i] + " x " + counts[i]);

                // Step 1: Validate Input
                BookingValidator.validate(roomTypes[i], counts[i], inventory);

                // Step 2: Perform Booking
                inventory.bookRoom(roomTypes[i], counts[i]);

                System.out.println("Booking successful!");

            } catch (InvalidBookingException e) {
                // Graceful failure handling
                System.out.println("Booking failed: " + e.getMessage());
            }
        }

        // Final Inventory State
        inventory.displayInventory();
    }
}