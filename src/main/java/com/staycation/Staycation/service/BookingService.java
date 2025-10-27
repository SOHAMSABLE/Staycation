package com.staycation.Staycation.service;

import com.staycation.Staycation.dto.BookingDto;
import com.staycation.Staycation.dto.BookingRequest;
import com.staycation.Staycation.dto.GuestDto;

import java.util.List;

public interface BookingService {

    BookingDto initialiseBooking(BookingRequest bookingRequest);

    BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList);
}
