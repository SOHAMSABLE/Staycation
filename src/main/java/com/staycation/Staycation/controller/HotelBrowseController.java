package com.staycation.Staycation.controller;

import com.staycation.Staycation.dto.HotelDto;
import com.staycation.Staycation.dto.HotelInfoDto;
import com.staycation.Staycation.dto.HotelPriceDto;
import com.staycation.Staycation.dto.HotelSearchRequest;
import com.staycation.Staycation.service.HotelService;
import com.staycation.Staycation.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor

public class HotelBrowseController {

    private final InventoryService inventoryService;
    private  final HotelService hotelService;

    @GetMapping("/search")
    public ResponseEntity<Page<HotelPriceDto>> searchHotels (@RequestBody HotelSearchRequest hotelSearchRequest){

        var page =inventoryService.searchHotels(hotelSearchRequest);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelInfo(@PathVariable Long hotelId){
        return  ResponseEntity.ok(hotelService.getHotelInfoById(hotelId));
    }
}
