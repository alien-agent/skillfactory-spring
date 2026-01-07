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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private HotelClient hotelClient;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
    }

    @Test
    void createBooking_Success() {
        BookingCreationRequest request = new BookingCreationRequest();
        request.setRoomId(101L);
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(2));

        Booking booking = new Booking();
        booking.setStatus(BookingStatus.PENDING);

        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArguments()[0]);
        when(hotelClient.confirmAvailability(anyLong(), any(BookingRequest.class)))
                .thenReturn(ResponseEntity.ok().build());

        Booking result = bookingService.createBooking(request);

        assertEquals(BookingStatus.CONFIRMED, result.getStatus());
        verify(hotelClient).confirmAvailability(eq(101L), any(BookingRequest.class));
    }

    @Test
    void createBooking_Failure_Compensation() {
        BookingCreationRequest request = new BookingCreationRequest();
        request.setRoomId(101L);
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(2));

        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArguments()[0]);
        // Simulate failure (exception or non-2xx)
        when(hotelClient.confirmAvailability(anyLong(), any(BookingRequest.class)))
                .thenThrow(new RuntimeException("Service unavailable"));

        Booking result = bookingService.createBooking(request);

        assertEquals(BookingStatus.CANCELLED, result.getStatus());
        verify(hotelClient).release(eq(101L), any(BookingRequest.class));
    }
}
