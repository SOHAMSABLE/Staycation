package com.staycation.Staycation.controller;

import com.staycation.Staycation.dto.HotelDto;
import com.staycation.Staycation.dto.RoomDto;
import com.staycation.Staycation.entity.Room;
import com.staycation.Staycation.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/hotels/{hotelId}/rooms")
@RequiredArgsConstructor


public class RoomAdminController {

    private  final RoomService roomService;

    @PostMapping
    public ResponseEntity <RoomDto> createNewRoom(@PathVariable Long hotelId , @RequestBody RoomDto roomDto){
        RoomDto room =roomService.createNewRoom(hotelId , roomDto);
        return  new ResponseEntity<>(room, HttpStatus.CREATED);
    }

    @GetMapping
    public  ResponseEntity<List<RoomDto>> getAllRoomsInHotel(@PathVariable Long hotelId){
        return ResponseEntity.ok(roomService.getAllRoomInHotel(hotelId));
    }

    @GetMapping("/{roomId}")
    public  ResponseEntity<RoomDto> getRoomById(@PathVariable Long hotelId, @PathVariable Long roomId){
        return  ResponseEntity.ok(roomService.getRoomById(roomId));
    }

    @DeleteMapping("/{roomId}")
    public  ResponseEntity<RoomDto> deleteRoomById(@PathVariable Long hotelId, @PathVariable Long roomId){
        roomService.deleteRoomById(roomId);
        return  ResponseEntity.noContent().build();
    }



}

