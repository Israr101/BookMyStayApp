import java.io.*;
import java.util.*;

// Main class
public class UseCase12DataPersistenceRecovery {

    // Serializable class for Hotel Room
    static class PersistableRoom implements Serializable {
        private static final long serialVersionUID = 1L;
        String roomId;
        String roomType;

        PersistableRoom(String roomId, String roomType) {
            this.roomId = roomId;
            this.roomType = roomType;
        }
    }

    // Serializable Inventory class
    static class PersistableInventory implements Serializable {
        private static final long serialVersionUID = 1L;
        Map<String, Stack<String>> availableRooms = new HashMap<>();
        Map<String, Integer> roomCount = new HashMap<>();

        void addRoom(String roomType, String roomId) {
            availableRooms.putIfAbsent(roomType, new Stack<>());
            availableRooms.get(roomType).push(roomId);
            roomCount.put(roomType, roomCount.getOrDefault(roomType, 0) + 1);
        }

        String allocateRoom(String roomType) {
            Stack<String> rooms = availableRooms.get(roomType);
            if (rooms != null && !rooms.isEmpty()) {
                roomCount.put(roomType, roomCount.get(roomType) - 1);
                return rooms.pop();
            }
            return null;
        }

        void releaseRoom(String roomType, String roomId) {
            availableRooms.get(roomType).push(roomId);
            roomCount.put(roomType, roomCount.getOrDefault(roomType, 0) + 1);
        }

        void printInventory() {
            System.out.println("Current Inventory:");
            for (String type : roomCount.keySet()) {
                System.out.println(" - " + type + ": " + roomCount.get(type) + " available");
            }
        }
    }

    // Serializable Booking record
    static class PersistableBooking implements Serializable {
        private static final long serialVersionUID = 1L;
        String bookingId;
        String guestName;
        String roomType;
        String roomId;
        boolean isCancelled;

        PersistableBooking(String bookingId, String guestName, String roomType, String roomId) {
            this.bookingId = bookingId;
            this.guestName = guestName;
            this.roomType = roomType;
            this.roomId = roomId;
            this.isCancelled = false;
        }
    }

    // Service to handle bookings and persistence
    static class PersistableBookingService {
        PersistableInventory inventory;
        Map<String, PersistableBooking> bookingHistory;
        private static final String DATA_FILE = "hotel_system_state.dat";

        PersistableBookingService() {
            bookingHistory = new HashMap<>();
            inventory = new PersistableInventory();
        }

        // Confirm booking
        String confirmBooking(String guestName, String roomType) {
            String roomId = inventory.allocateRoom(roomType);
            if (roomId == null) {
                System.out.println("No rooms available for type: " + roomType);
                return null;
            }
            String bookingId = "BKG" + (bookingHistory.size() + 1);
            PersistableBooking record = new PersistableBooking(bookingId, guestName, roomType, roomId);
            bookingHistory.put(bookingId, record);
            System.out.println("Booking confirmed: " + bookingId + " for " + guestName + " in room " + roomId);
            return bookingId;
        }

        // Cancel booking
        void cancelBooking(String bookingId) {
            PersistableBooking record = bookingHistory.get(bookingId);
            if (record == null || record.isCancelled) {
                System.out.println("Invalid or duplicate cancellation: " + bookingId);
                return;
            }
            inventory.releaseRoom(record.roomType, record.roomId);
            record.isCancelled = true;
            System.out.println("Booking cancelled: " + bookingId + ", room " + record.roomId + " restored.");
        }

        void printBookingHistory() {
            System.out.println("\nBooking History:");
            for (PersistableBooking record : bookingHistory.values()) {
                System.out.println(" - " + record.bookingId + ": " + record.guestName +
                        ", Room Type: " + record.roomType +
                        ", Room ID: " + record.roomId +
                        ", Cancelled: " + record.isCancelled);
            }
        }

        // Save system state
        void saveState() {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
                out.writeObject(inventory);
                out.writeObject(bookingHistory);
                System.out.println("\nSystem state saved successfully to " + DATA_FILE);
            } catch (IOException e) {
                System.out.println("Error saving system state: " + e.getMessage());
            }
        }

        // Load system state
        void loadState() {
            File file = new File(DATA_FILE);
            if (!file.exists()) {
                System.out.println("No previous state found. Starting fresh.");
                return;
            }
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
                inventory = (PersistableInventory) in.readObject();
                bookingHistory = (Map<String, PersistableBooking>) in.readObject();
                System.out.println("System state loaded successfully from " + DATA_FILE);
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error loading system state: " + e.getMessage());
            }
        }
    }

    // Main method to demonstrate persistence and recovery
    public static void main(String[] args) {
        PersistableBookingService bookingService = new PersistableBookingService();

        // Load previous state
        bookingService.loadState();

        // Initialize inventory if empty
        if (bookingService.inventory.roomCount.isEmpty()) {
            bookingService.inventory.addRoom("Deluxe", "D101");
            bookingService.inventory.addRoom("Deluxe", "D102");
            bookingService.inventory.addRoom("Standard", "S201");
            bookingService.inventory.addRoom("Standard", "S202");
        }

        // Simulate bookings
        String b1 = bookingService.confirmBooking("Alice", "Deluxe");
        String b2 = bookingService.confirmBooking("Bob", "Standard");

        bookingService.inventory.printInventory();
        bookingService.printBookingHistory();

        System.out.println("\n--- Simulate cancellation ---");
        bookingService.cancelBooking(b1);

        bookingService.inventory.printInventory();
        bookingService.printBookingHistory();

        // Save state before shutdown
        bookingService.saveState();

        System.out.println("\nSystem ready for next startup. Run the program again to recover state.");
    }
}