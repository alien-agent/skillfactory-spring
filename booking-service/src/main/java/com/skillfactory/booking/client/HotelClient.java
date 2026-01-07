package com.skillfactory.booking.client;

import com.skillfactory.booking.dto.BookingRequest;
import com.skillfactory.booking.dto.RoomDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

@FeignClient(name = "hotel-service")
public interface HotelClient {

    @GetMapping("/api/rooms/recommend")
    List<RoomDto> getRecommendedRooms();

    @PostMapping("/api/rooms/{id}/confirm-availability")
    ResponseEntity<Void> confirmAvailability(@PathVariable("id") Long id, @RequestBody BookingRequest request);

    @PostMapping("/api/rooms/{id}/release")
    ResponseEntity<Void> release(@PathVariable("id") Long id, @RequestBody BookingRequest request);
}
