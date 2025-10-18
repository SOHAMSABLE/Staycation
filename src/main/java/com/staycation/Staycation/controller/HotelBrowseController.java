package com.staycation.Staycation.controller;

import com.staycation.Staycation.dto.HotelDto;
import com.staycation.Staycation.dto.HotelSearchRequest;
import com.staycation.Staycation.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor

public class HotelBrowseController {

    private final InventoryService inventoryService;

    @GetMapping("/search")
    public ResponseEntity<Page<HotelDto>> searchHotels (@RequestBody HotelSearchRequest hotelSearchRequest){

        Page<HotelDto> page =inventoryService.searchHotels(hotelSearchRequest);
        return ResponseEntity.ok(page);
    }
}
