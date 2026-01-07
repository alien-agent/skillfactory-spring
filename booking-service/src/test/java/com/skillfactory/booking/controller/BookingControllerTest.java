package com.skillfactory.booking.controller;

import com.skillfactory.booking.dto.BookingCreationRequest;
import com.skillfactory.booking.entity.Booking;
import com.skillfactory.booking.entity.BookingStatus;
import com.skillfactory.booking.service.BookingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void testCreateBooking_Success() throws Exception {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.CONFIRMED);

        Mockito.when(bookingService.createBooking(any(BookingCreationRequest.class))).thenReturn(booking);

        String jsonInfo = "{\"startDate\": \"2025-01-01\", \"endDate\": \"2025-01-05\", \"autoSelect\": true}";

        mockMvc.perform(post("/booking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonInfo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void testGetUserBookings() throws Exception {
        Mockito.when(bookingService.getUserBookings()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN") // Endpoint expects USER
    public void testCreateBooking_WrongRole() throws Exception {
        String jsonInfo = "{\"startDate\": \"2025-01-01\", \"endDate\": \"2025-01-05\", \"autoSelect\": true}";
        // According to SecurityConfig, /booking is hasRole("USER"). Admin usually
        // doesn't have USER role unless inherited.
        // In our DataLoader they are separate. So this should fail with 403.

        mockMvc.perform(post("/booking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonInfo))
                .andExpect(status().isForbidden());
    }
}
