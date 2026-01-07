package com.skillfactory.hotel.config;

import com.skillfactory.hotel.entity.Hotel;
import com.skillfactory.hotel.entity.Room;
import com.skillfactory.hotel.repository.HotelRepository;
import com.skillfactory.hotel.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.IntStream;

@Configuration
@RequiredArgsConstructor
public class DataLoader {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;

    @Bean
    public CommandLineRunner loadData() {
        return args -> {
            if (hotelRepository.count() == 0) {
                Hotel hotel = new Hotel();
                hotel.setName("Grand Hotel");
                hotel.setAddress("123 Main St");
                hotelRepository.save(hotel);

                // Create rooms
                IntStream.rangeClosed(101, 110).forEach(i -> {
                    Room room = new Room();
                    room.setHotel(hotel);
                    room.setNumber(String.valueOf(i));
                    room.setTimesBooked(0); // initial
                    roomRepository.save(room);
                });

                // Add some with bookings to test balancing
                Room busyRoom = new Room();
                busyRoom.setHotel(hotel);
                busyRoom.setNumber("111");
                busyRoom.setTimesBooked(10);
                roomRepository.save(busyRoom);
            }
        };
    }
}
