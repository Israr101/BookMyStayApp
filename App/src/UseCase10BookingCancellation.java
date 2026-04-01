import java.util.*;

class UseCase10BookingCancellation {

    // Represents a hotel room
    static class HotelRoom {
        String roomId;
        String roomType;

        HotelRoom(String roomId, String roomType) {
            this.roomId = roomId;
            this.roomType = roomType;
        }
    }

    // Manages inventory for each room type
    static class HotelInventory {
        Map<String, Integer> roomTypeCount = new HashMap<>();
        Map<String, Stack<String>> availableRooms = new HashMap<>();

        // Add rooms to inventory
        void addRoom(String roomType, String roomId) {
            roomTypeCount.put(roomType, roomTypeCount.getOrDefault(roomType, 0) + 1);
            availableRooms.putIfAbsent(roomType, new Stack<>());
            availableRooms.get(roomType).push(roomId);
        }

        // Allocate room
        String allocateRoom(String roomType) {
            Stack<String> rooms = availableRooms.get(roomType);
            if (rooms != null && !rooms.isEmpty()) {
                roomTypeCount.put(roomType, roomTypeCount.get(roomType) - 1);
                return rooms.pop();
            }
            return null; // no rooms available
        }

        // Release room back to inventory
        void releaseRoom(String roomType, String roomId) {
            roomTypeCount.put(roomType, roomTypeCount.getOrDefault(roomType, 0) + 1);
            availableRooms.get(roomType).push(roomId);
        }

        void printInventory() {
            System.out.println("Current Inventory:");
            for (String type : roomTypeCount.keySet()) {
                System.out.println(" - " + type + ": " + roomTypeCount.get(type) + " available");
            }
        }
    }

    // Represents a booking record
    static class BookingRecord {
        String bookingId;
        String guestName;
        String roomType;
        String roomId;
        boolean isCancelled;

        BookingRecord(String bookingId, String guestName, String roomType, String roomId) {
            this.bookingId = bookingId;
            this.guestName = guestName;
            this.roomType = roomType;
            this.roomId = roomId;
            this.isCancelled = false;
        }
    }

    // Service to manage bookings and cancellations
    static class BookingService {
        Map<String, BookingRecord> bookingHistory = new HashMap<>();
        HotelInventory inventory;
        Stack<String> rollbackStack = new Stack<>();

        BookingService(HotelInventory inventory) {
            this.inventory = inventory;
        }

        // Confirm a new booking
        String confirmBooking(String guestName, String roomType) {
            String roomId = inventory.allocateRoom(roomType);
            if (roomId == null) {
                System.out.println("No rooms available for type: " + roomType);
                return null;
            }
            String bookingId = "BKG" + (bookingHistory.size() + 1);
            BookingRecord record = new BookingRecord(bookingId, guestName, roomType, roomId);
            bookingHistory.put(bookingId, record);
            System.out.println("Booking confirmed: " + bookingId + " for " + guestName + " in room " + roomId);
            return bookingId;
        }

        // Cancel an existing booking
        void cancelBooking(String bookingId) {
            BookingRecord record = bookingHistory.get(bookingId);
            if (record == null) {
                System.out.println("Booking ID not found: " + bookingId);
                return;
            }
            if (record.isCancelled) {
                System.out.println("Booking already cancelled: " + bookingId);
                return;
            }

            // Rollback logic
            rollbackStack.push(record.roomId); // track room being released
            inventory.releaseRoom(record.roomType, record.roomId);
            record.isCancelled = true;

            System.out.println("Booking cancelled successfully: " + bookingId);
            System.out.println("Room " + record.roomId + " restored to inventory.");
        }

        void printBookingHistory() {
            System.out.println("\nBooking History:");
            for (BookingRecord record : bookingHistory.values()) {
                System.out.println(" - " + record.bookingId + ": " + record.guestName +
                        ", Room Type: " + record.roomType +
                        ", Room ID: " + record.roomId +
                        ", Cancelled: " + record.isCancelled);
            }
        }
    }

    // Main program
    public static void main(String[] args) {
        HotelInventory inventory = new HotelInventory();

        // Initialize inventory
        inventory.addRoom("Deluxe", "D101");
        inventory.addRoom("Deluxe", "D102");
        inventory.addRoom("Standard", "S201");
        inventory.addRoom("Standard", "S202");

        BookingService bookingService = new BookingService(inventory);

        // Make bookings
        String booking1 = bookingService.confirmBooking("Alice", "Deluxe");
        String booking2 = bookingService.confirmBooking("Bob", "Standard");
        String booking3 = bookingService.confirmBooking("Charlie", "Deluxe");

        inventory.printInventory();
        bookingService.printBookingHistory();

        System.out.println("\n--- Cancellation ---");
        bookingService.cancelBooking(booking1); // cancel Alice's booking
        bookingService.cancelBooking("BKG999"); // non-existent booking
        bookingService.cancelBooking(booking1); // duplicate cancellation

        inventory.printInventory();
        bookingService.printBookingHistory();
    }
}