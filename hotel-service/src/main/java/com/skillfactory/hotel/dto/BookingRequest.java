package com.skillfactory.hotel.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class BookingRequest {
    private LocalDate startDate;
    private LocalDate endDate;
}
