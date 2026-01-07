package com.skillfactory.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreationRequest {
    private Long roomId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean autoSelect = false;
}
