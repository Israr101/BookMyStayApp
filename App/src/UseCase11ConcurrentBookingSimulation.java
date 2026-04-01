import java.util.*;
import java.util.concurrent.*;

class UseCase11ConcurrentBookingSimulation {

    // Represents a hotel room
    static class HotelRoom {
        String roomId;
        String roomType;

        HotelRoom(String roomId, String roomType) {
            this.roomId = roomId;
            this.roomType = roomType;
        }
    }

    // Thread-safe hotel inventory
    static class SafeHotelInventory {
        private final Map<String, Stack<String>> availableRooms = new HashMap<>();
        private final Map<String, Integer> roomCount = new HashMap<>();

        // Add rooms
        synchronized void addRoom(String roomType, String roomId) {
            availableRooms.putIfAbsent(roomType, new Stack<>());
            availableRooms.get(roomType).push(roomId);
            roomCount.put(roomType, roomCount.getOrDefault(roomType, 0) + 1);
        }

        // Allocate room safely
        synchronized String allocateRoom(String roomType) {
            Stack<String> rooms = availableRooms.get(roomType);
            if (rooms != null && !rooms.isEmpty()) {
                roomCount.put(roomType, roomCount.get(roomType) - 1);
                return rooms.pop();
            }
            return null;
        }

        // Release room safely
        synchronized void releaseRoom(String roomType, String roomId) {
            availableRooms.get(roomType).push(roomId);
            roomCount.put(roomType, roomCount.getOrDefault(roomType, 0) + 1);
        }

        synchronized void printInventory() {
            System.out.println("Current Inventory:");
            for (String type : roomCount.keySet()) {
                System.out.println(" - " + type + ": " + roomCount.get(type) + " available");
            }
        }
    }

    // Represents a booking
    static class BookingRecord {
        String bookingId;
        String guestName;
        String roomType;
        String roomId;

        BookingRecord(String bookingId, String guestName, String roomType, String roomId) {
            this.bookingId = bookingId;
            this.guestName = guestName;
            this.roomType = roomType;
            this.roomId = roomId;
        }
    }

    // Booking processor for concurrent requests
    static class ConcurrentBookingProcessor implements Runnable {
        private final SafeHotelInventory inventory;
        private final String guestName;
        private final String roomType;
        private final Map<String, BookingRecord> bookingHistory;

        ConcurrentBookingProcessor(SafeHotelInventory inventory, Map<String, BookingRecord> bookingHistory,
                                   String guestName, String roomType) {
            this.inventory = inventory;
            this.guestName = guestName;
            this.roomType = roomType;
            this.bookingHistory = bookingHistory;
        }

        @Override
        public void run() {
            String roomId = inventory.allocateRoom(roomType);
            if (roomId != null) {
                String bookingId = "BKG" + (bookingHistory.size() + 1);
                BookingRecord record = new BookingRecord(bookingId, guestName, roomType, roomId);
                synchronized (bookingHistory) { // protect shared booking history
                    bookingHistory.put(bookingId, record);
                }
                System.out.println("Booking successful for " + guestName + ": " + roomId);
            } else {
                System.out.println("No available rooms for " + guestName + " (" + roomType + ")");
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        SafeHotelInventory inventory = new SafeHotelInventory();

        // Initialize inventory
        inventory.addRoom("Deluxe", "D101");
        inventory.addRoom("Deluxe", "D102");
        inventory.addRoom("Standard", "S201");
        inventory.addRoom("Standard", "S202");

        Map<String, BookingRecord> bookingHistory = new HashMap<>();

        // Create guest threads
        String[] guests = {"Alice", "Bob", "Charlie", "Diana", "Eve"};
        String[] roomPreferences = {"Deluxe", "Deluxe", "Standard", "Deluxe", "Standard"};

        ExecutorService executor = Executors.newFixedThreadPool(5);

        for (int i = 0; i < guests.length; i++) {
            executor.submit(new ConcurrentBookingProcessor(inventory, bookingHistory, guests[i], roomPreferences[i]));
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        System.out.println("\n--- Final Booking History ---");
        for (BookingRecord record : bookingHistory.values()) {
            System.out.println(" - " + record.bookingId + ": " + record.guestName +
                    ", Room Type: " + record.roomType + ", Room ID: " + record.roomId);
        }

        System.out.println("\n--- Final Inventory ---");
        inventory.printInventory();
    }
}