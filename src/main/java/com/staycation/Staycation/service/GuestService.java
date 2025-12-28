package com.staycation.Staycation.service;

import com.staycation.Staycation.dto.GuestDto;

import java.util.List;

public interface GuestService {
    GuestDto createGuest(GuestDto guestDto);

    GuestDto updateGuest(Long id, GuestDto guestDto);

    void deleteGuest(Long id);

    List<GuestDto> getAllMyGuests();
}
