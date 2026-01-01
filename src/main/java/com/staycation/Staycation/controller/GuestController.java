package com.staycation.Staycation.controller;

import com.staycation.Staycation.advice.ApiResponse;
import com.staycation.Staycation.dto.GuestDto;
import com.staycation.Staycation.repository.GuestRepository;
import com.staycation.Staycation.service.GuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("guests")
@RequiredArgsConstructor
public class GuestController {

    private final GuestService guestService;

    @PostMapping
    public ResponseEntity<GuestDto> createGuest(@RequestBody GuestDto guestDto){
        return ResponseEntity.ok(guestService.createGuest(guestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GuestDto> updateGuest(@PathVariable Long id, @RequestBody GuestDto guestDto){
        return ResponseEntity.ok(guestService.updateGuest(id, guestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteGuest(@PathVariable Long id){
        guestService.deleteGuest(id);
        ApiResponse<String> response = new ApiResponse<>("Guest deleted Successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<GuestDto>> getAllMyGuests(){
        return ResponseEntity.ok(guestService.getAllMyGuests());
    }
}
