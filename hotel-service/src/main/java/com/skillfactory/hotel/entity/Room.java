package com.skillfactory.hotel.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    private String number;
    private Boolean available = true; // Operational availability

    @Column(name = "times_booked")
    private Integer timesBooked = 0;

    // To handle simple locking for dates, we might need a separate table or a
    // collection.
    // Task 96-102 mentions minimal tables. 104 says "Availability by dates
    // determined by BOOKINGS table".
    // BUT Booking table is in Booking Service!
    // Hotel Service is responsible for "confirm availability".
    // If Booking Service has the Bookings, how does Hotel Service know if a room is
    // free on a specific date?
    // "Booking Service creates booking PENDING, calls Hotel Service to confirm
    // availability".
    // If Hotel Service does NOT store bookings, it can't know availability.
    // IMPLICATION: Hotel Service MUST store availability state somehow.
    // Line 104 says: "Field AVAILABLE (in Room) reflects operational
    // availability... emptiness determined by BOOKINGS table (START_DATE,
    // END_DATE)".
    // Line 94 says Booking Service has `bookings` table.
    // Line 54: "Hotel Service keeps statistics...".
    // Line 91: "POST /api/rooms/{id}/confirm-availability - confirm availability
    // ... (temporary slot lock)".
    // Typically in microservices, Booking Service owns the "Booking" domain. Hotel
    // Service owns "Room Inventory".
    // Ideally, Hotel Service should have a table like `RoomAvailability` or
    // `RoomReservation`.
    // OR, Booking Service asks Hotel Service "Is room X free?". Hotel Service
    // checks WHAT?
    // If Hotel Service doesn't know about bookings, it can't answer.
    // Therefore, Hotel Service MUST have a representation of occupied slots.
    // Let's add a `RoomBookedDate` entity or similar in Hotel Service to track
    // confirmed/locked dates.
    // Or simpler: `List<LocalDate> bookedDates` as ElementCollection?

    @ElementCollection
    @CollectionTable(name = "room_occupied_dates", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "occupied_date")
    private List<LocalDate> occupiedDates = new ArrayList<>();
}
