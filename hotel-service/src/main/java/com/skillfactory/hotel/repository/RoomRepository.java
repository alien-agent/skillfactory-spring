package com.skillfactory.hotel.repository;

import com.skillfactory.hotel.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByAvailableTrue();

    // For sorting by timesBooked
    List<Room> findByAvailableTrueOrderByTimesBookedAscIdAsc();

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Room r LEFT JOIN FETCH r.occupiedDates WHERE r.id = :id")
    java.util.Optional<Room> findByIdWithLock(Long id);
}
