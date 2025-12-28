package com.staycation.Staycation.service;

import com.staycation.Staycation.dto.GuestDto;
import com.staycation.Staycation.entity.Guest;
import com.staycation.Staycation.entity.User;
import com.staycation.Staycation.exception.ResourceNotFoundException;
import com.staycation.Staycation.repository.GuestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.staycation.Staycation.utils.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuestServiceImpl implements GuestService{

    private final GuestRepository guestRepository;
    private final ModelMapper modelMapper;

    @Override
    public GuestDto createGuest(GuestDto guestDto) {
        User user = getCurrentUser();
        log.info("Creating guest for user: {}", user.getName());

        Guest guest = modelMapper.map(guestDto, Guest.class);
        guest.setUser(user);

        Guest savedGuest = guestRepository.save(guest);
        return modelMapper.map(savedGuest, GuestDto.class);
    }

    @Override
    public GuestDto updateGuest(Long guestId, GuestDto guestDto) {
        Guest existingGuest = guestRepository.findById(guestId).orElseThrow(() -> new ResourceNotFoundException("Guest not found with id: " + guestId));

        validateOwnership(existingGuest);

        User owner = existingGuest.getUser();

        modelMapper.map(guestDto, existingGuest);
        existingGuest.setId(guestId);
        existingGuest.setUser(owner);

        Guest updatedGuest = guestRepository.save(existingGuest);
        return modelMapper.map(updatedGuest, GuestDto.class);
    }

    @Override
    public void deleteGuest(Long guestId) {
        Guest guest = guestRepository.findById(guestId).orElseThrow(() -> new ResourceNotFoundException("Guest not found with id"+guestId));

        validateOwnership(guest);
        try {
            guestRepository.delete(guest);
            // Important: Flush the change within the try-catch to catch the DB error here
            guestRepository.flush();
        } catch (Exception e) {
            // If it's linked to a booking, provide a better error message
            throw new RuntimeException("Cannot delete guest because they are linked to an existing booking history.");
        }
    }

    @Override
    public List<GuestDto> getAllMyGuests() {
        User user = getCurrentUser();
        return guestRepository.findByUser(user)
                .stream()
                .map(element ->modelMapper.map(element, GuestDto.class))
                .collect(Collectors.toList());
    }


    private void validateOwnership(Guest guest){
        User user = getCurrentUser();
        if(!guest.getUser().getId().equals(user.getId())){
            throw new AccessDeniedException("You do not have permission to manage this guest");
        }
    }
}
