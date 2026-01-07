package com.skillfactory.hotel.controller;

import com.skillfactory.hotel.dto.BookingRequest;
import com.skillfactory.hotel.entity.Room;
import com.skillfactory.hotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    // ADMIN
    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody Room room) {
        return new ResponseEntity<>(roomService.createRoom(room), HttpStatus.CREATED);
    }

    // USER
    @GetMapping
    public List<Room> getRooms() {
        return roomService.getAllRooms();
    }

    // USER
    @GetMapping("/recommend")
    public List<Room> getRecommendedRooms() {
        return roomService.getRecommendedRooms();
    }

    // INTERNAL
    @PostMapping("/{id}/confirm-availability")
    public ResponseEntity<Void> confirmAvailability(@PathVariable Long id, @RequestBody BookingRequest request) {
        boolean success = roomService.confirmAvailability(id, request);
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    // INTERNAL
    @PostMapping("/{id}/release")
    public ResponseEntity<Void> release(@PathVariable Long id, @RequestBody BookingRequest request) {
        roomService.release(id, request);
        return ResponseEntity.ok().build();
    }
}
