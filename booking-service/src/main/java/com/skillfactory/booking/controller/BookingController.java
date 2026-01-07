package com.skillfactory.booking.controller;

import com.skillfactory.booking.dto.BookingCreationRequest;
import com.skillfactory.booking.entity.Booking;
import com.skillfactory.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // Assuming Gateway maps /api/bookings/** or similar.
    // Wait, Task says "79: POST /booking". "83: GET /booking/{id}". "80: GET
    // /bookings".
    // I will try to support these paths.

    @PostMapping("/booking")
    public ResponseEntity<Booking> createBooking(@RequestBody BookingCreationRequest request) {
        return new ResponseEntity<>(bookingService.createBooking(request), HttpStatus.CREATED);
    }

    @GetMapping("/bookings")
    public List<Booking> getUserBookings() {
        return bookingService.getUserBookings();
    }

    @GetMapping("/booking/{id}")
    public Booking getBooking(@PathVariable Long id) {
        return bookingService.getBooking(id);
    }

    @DeleteMapping("/booking/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.ok().build();
    }
}
