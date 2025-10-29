package com.staycation.Staycation.service;

import com.staycation.Staycation.dto.BookingDto;
import com.staycation.Staycation.dto.BookingRequest;
import com.staycation.Staycation.dto.GuestDto;
import com.staycation.Staycation.entity.enums.BookingStatus;
import com.stripe.model.Event;

import java.util.List;

public interface BookingService {

    BookingDto initialiseBooking(BookingRequest bookingRequest);

    BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList);

    String initiatePayements(Long bookingId);

    void capturePayment(Event event);

    void cancelBooking(Long bookingId);

    BookingStatus getBookingStatus(Long bookingId);
}
