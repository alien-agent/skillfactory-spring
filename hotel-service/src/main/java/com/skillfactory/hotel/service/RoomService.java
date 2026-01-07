package com.skillfactory.hotel.service;

import com.skillfactory.hotel.dto.BookingRequest;
import com.skillfactory.hotel.entity.Room;
import com.skillfactory.hotel.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;

    @Transactional
    public Room createRoom(Room room) {
        return roomRepository.save(room);
    }

    @Transactional(readOnly = true)
    public List<Room> getAllRooms() {
        return roomRepository.findByAvailableTrue();
    }

    @Transactional(readOnly = true)
    public List<Room> getRecommendedRooms() {
        return roomRepository.findByAvailableTrueOrderByTimesBookedAscIdAsc();
    }

    @Transactional
    public boolean confirmAvailability(Long roomId, BookingRequest request) {
        Room room = roomRepository.findByIdWithLock(roomId).orElseThrow();
        if (!room.getAvailable())
            return false;

        List<LocalDate> requestedDates = request.getStartDate()
                .datesUntil(request.getEndDate())
                .collect(Collectors.toList());

        // Check overlap
        for (LocalDate date : requestedDates) {
            if (room.getOccupiedDates().contains(date)) {
                return false; // Already booked
            }
        }

        // Lock it
        room.getOccupiedDates().addAll(requestedDates);
        room.setTimesBooked(room.getTimesBooked() + 1);
        roomRepository.save(room);
        return true;
    }

    @Transactional
    public void release(Long roomId, BookingRequest request) {
        Room room = roomRepository.findByIdWithLock(roomId).orElseThrow();
        List<LocalDate> requestedDates = request.getStartDate()
                .datesUntil(request.getEndDate())
                .collect(Collectors.toList());

        boolean changed = room.getOccupiedDates().removeAll(requestedDates);

        // Only decrement if we actually removed something (meaning it WAS booked)
        if (changed && room.getTimesBooked() > 0) {
            room.setTimesBooked(room.getTimesBooked() - 1);
        }
        roomRepository.save(room);
    }
}
