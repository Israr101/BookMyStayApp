import java.util.*;

// Reservation Model
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

    @Override
    public String toString() {
        return reservationId + " | " + customerName + " | " + roomType;
    }
}

// Booking History (Data Storage)
class BookingHistory {
    // List to maintain insertion order
    private List<Reservation> reservations;

    public BookingHistory() {
        reservations = new ArrayList<>();
    }

    // Add confirmed reservation
    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
    }

    // Retrieve all reservations
    public List<Reservation> getAllReservations() {
        return reservations;
    }
}

// Booking Report Service (Read-only operations)
class BookingReportService {

    private BookingHistory history;

    public BookingReportService(BookingHistory history) {
        this.history = history;
    }

    // Display all bookings
    public void displayAllBookings() {
        System.out.println("\n--- Booking History ---");
        for (Reservation r : history.getAllReservations()) {
            System.out.println(r);
        }
    }

    // Generate summary report
    public void generateSummaryReport() {
        System.out.println("\n--- Booking Summary Report ---");

        Map<String, Integer> roomTypeCount = new HashMap<>();

        for (Reservation r : history.getAllReservations()) {
            roomTypeCount.put(
                    r.getRoomType(),
                    roomTypeCount.getOrDefault(r.getRoomType(), 0) + 1
            );
        }

        for (String type : roomTypeCount.keySet()) {
            System.out.println(type + " Rooms Booked: " + roomTypeCount.get(type));
        }

        System.out.println("Total Bookings: " + history.getAllReservations().size());
    }
}

// Main Class
public class UseCase8BookingHistoryReport {

    public static void main(String[] args) {

        // Booking history storage
        BookingHistory history = new BookingHistory();

        // Simulate confirmed bookings (from Use Case 6)
        Reservation r1 = new Reservation("RES101", "Alice", "Deluxe");
        Reservation r2 = new Reservation("RES102", "Bob", "Standard");
        Reservation r3 = new Reservation("RES103", "Charlie", "Deluxe");
        Reservation r4 = new Reservation("RES104", "David", "Suite");

        // Add to history (in order of confirmation)
        history.addReservation(r1);
        history.addReservation(r2);
        history.addReservation(r3);
        history.addReservation(r4);

        // Reporting service
        BookingReportService reportService = new BookingReportService(history);

        // Admin views booking history
        reportService.displayAllBookings();

        // Admin generates summary report
        reportService.generateSummaryReport();
    }
}