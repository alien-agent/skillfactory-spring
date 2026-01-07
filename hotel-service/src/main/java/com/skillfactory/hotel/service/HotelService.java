package com.skillfactory.hotel.service;

import com.skillfactory.hotel.entity.Hotel;
import com.skillfactory.hotel.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService {
    private final HotelRepository hotelRepository;

    @Transactional
    public Hotel createHotel(Hotel hotel) {
        return hotelRepository.save(hotel);
    }

    @Transactional(readOnly = true)
    public List<Hotel> mapHotels() {
        return hotelRepository.findAll();
    }
}
