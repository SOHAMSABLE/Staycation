package com.staycation.Staycation.service;

import com.staycation.Staycation.dto.BookingDto;
import com.staycation.Staycation.dto.BookingRequest;

public interface BookingService {

    BookingDto initialiseBooking(BookingRequest bookingRequest);
}
