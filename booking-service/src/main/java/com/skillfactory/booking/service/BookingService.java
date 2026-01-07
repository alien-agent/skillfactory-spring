package com.skillfactory.booking.service;

import com.skillfactory.booking.client.HotelClient;
import com.skillfactory.booking.dto.BookingCreationRequest;
import com.skillfactory.booking.dto.BookingRequest;
import com.skillfactory.booking.dto.RoomDto;
import com.skillfactory.booking.entity.Booking;
import com.skillfactory.booking.entity.BookingStatus;
import com.skillfactory.booking.entity.User;
import com.skillfactory.booking.repository.BookingRepository;
import com.skillfactory.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final HotelClient hotelClient;

    public Booking createBooking(BookingCreationRequest request) {
        // Get User
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        Long roomId = request.getRoomId();
        if (Boolean.TRUE.equals(request.getAutoSelect())) {
            List<RoomDto> recommendations = hotelClient.getRecommendedRooms();
            if (recommendations.isEmpty()) {
                throw new RuntimeException("No rooms available for auto-selection");
            }
            roomId = recommendations.get(0).getId();
        }

        if (roomId == null) {
            throw new RuntimeException("Room ID must be provided or autoSelect true");
        }

        // Create PENDING Booking
        Booking booking = new Booking();
        booking.setUserId(user.getId());
        booking.setRoomId(roomId);
        booking.setStartDate(request.getStartDate());
        booking.setEndDate(request.getEndDate());
        booking.setStatus(BookingStatus.PENDING);

        booking = bookingRepository.save(booking);

        // Saga Step 2: Call Hotel Service
        BookingRequest hotelRequest = new BookingRequest(request.getStartDate(), request.getEndDate());
        try {
            ResponseEntity<Void> response = hotelClient.confirmAvailability(roomId, hotelRequest);
            if (response.getStatusCode().is2xxSuccessful()) {
                booking.setStatus(BookingStatus.CONFIRMED);
            } else {
                throw new RuntimeException("Availability check failed");
            }
        } catch (Exception e) {
            log.error("Failed to confirm booking: {}", e.getMessage());
            booking.setStatus(BookingStatus.CANCELLED);
            // Compensating transaction
            try {
                hotelClient.release(roomId, hotelRequest);
            } catch (Exception ex) {
                log.error("Failed to release room during compensation: {}", ex.getMessage());
                // In real world: send to dead letter queue or retry later
            }
        }

        return bookingRepository.save(booking);
    }

    public List<Booking> getUserBookings() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        return bookingRepository.findByUserId(user.getId());
    }

    public Booking getBooking(Long id) {
        // Should add check that it belongs to user
        return bookingRepository.findById(id).orElseThrow();
    }

    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id).orElseThrow();
        // Typically user can only cancel if not past date? Or just cancel.
        // Compensation: release room.
        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            BookingRequest hotelRequest = new BookingRequest(booking.getStartDate(), booking.getEndDate());
            try {
                hotelClient.release(booking.getRoomId(), hotelRequest);
            } catch (Exception e) {
                log.error("Failed to release room on delete: {}", e.getMessage());
            }
        }
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        // Or actually delete from DB? Task says "DELETE /booking/{id} - cancel
        // booking".
        // Keeping it as CANCELLED is better for history.
    }
}
