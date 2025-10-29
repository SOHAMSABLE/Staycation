package com.staycation.Staycation.dto;

import com.staycation.Staycation.entity.Hotel;
import com.staycation.Staycation.entity.Room;
import com.staycation.Staycation.entity.User;
import com.staycation.Staycation.entity.enums.BookingStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
@Data
public class BookingDto {

    private Long id;

    private Integer roomsCount;

    private LocalDateTime checkInDate;

    private LocalDateTime checkOutDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private BookingStatus bookingStatus;

    private Set<GuestDto> guests;

    private BigDecimal amount;
}
