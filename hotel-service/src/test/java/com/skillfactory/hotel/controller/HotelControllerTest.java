package com.skillfactory.hotel.controller;

import com.skillfactory.hotel.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class HotelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoomRepository roomRepository;

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void testGetHotels_Success() throws Exception {
        mockMvc.perform(get("/api/hotels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Grand Hotel"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testCreateRoom_Admin() throws Exception {
        String roomJson = "{\"hotelId\": 1, \"number\": \"200\", \"timesBooked\": 0}";
        // Note: In real app we might need valid Hotel ID. DataLoader puts ID 1 usually.

        mockMvc.perform(post("/api/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(roomJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void testCreateRoom_UserForbidden() throws Exception {
        String roomJson = "{\"hotelId\": 1, \"number\": \"201\", \"timesBooked\": 0}";

        mockMvc.perform(post("/api/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(roomJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void testGetRecommendedRooms() throws Exception {
        mockMvc.perform(get("/api/rooms/recommend"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
