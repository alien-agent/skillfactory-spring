package com.skillfactory.hotel.controller;

import com.skillfactory.hotel.entity.Hotel;
import com.skillfactory.hotel.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
public class HotelController {
    private final HotelService hotelService;

    // ADMIN only (handled by Security config or Gateway)
    @PostMapping
    public ResponseEntity<Hotel> createHotel(@RequestBody Hotel hotel) {
        return new ResponseEntity<>(hotelService.createHotel(hotel), HttpStatus.CREATED);
    }

    // USER
    @GetMapping
    public List<Hotel> getHotels() {
        return hotelService.mapHotels();
    }
}
