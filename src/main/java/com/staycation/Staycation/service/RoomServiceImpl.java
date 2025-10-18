package com.staycation.Staycation.service;

import com.staycation.Staycation.dto.RoomDto;
import com.staycation.Staycation.entity.Hotel;
import com.staycation.Staycation.entity.Room;
import com.staycation.Staycation.exception.ResourceNotFoundException;
import com.staycation.Staycation.repository.HotelRepository;
import com.staycation.Staycation.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements  RoomService{

    private final RoomRepository roomRepository;
    private  final HotelRepository hotelRepository;
    private  final  InventoryService inventoryService;
    private final ModelMapper modelMapper;

    @Override
    public RoomDto createNewRoom(Long hotelID, RoomDto roomDto) {
        log.info("Creating a new room in hotel with ID: {}", hotelID);

        Hotel hotel = hotelRepository.findById(hotelID)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + hotelID));

        // Map DTO to entity
        Room room = modelMapper.map(roomDto, Room.class);
        room.setHotel(hotel);

        // Save room
        room = roomRepository.save(room);

        // Automatically initialize inventory if hotel is active
        if (hotel.getActive()) {
            inventoryService.initializeRoomForAYear(room);
        }

        return modelMapper.map(room, RoomDto.class);
    }


    @Override
    public List<RoomDto> getAllRoomInHotel(Long hotelId) {
        log.info("Getting all rooms in hotel with ID:{}", hotelId);
        Hotel hotel= hotelRepository
                .findById(hotelId)
                .orElseThrow(()->new ResourceNotFoundException("Hotel not found with ID:"+hotelId));
        return hotel.getRooms()
                .stream().
                map((element) -> modelMapper.map(element, RoomDto.class)).
                collect(Collectors.toList());
    }

    @Override
    public RoomDto getRoomById(Long roomId) {
        log.info("Getting the room in hotel with ID:{}", roomId);
        Room room= roomRepository
                .findById(roomId)
                .orElseThrow(()->new ResourceNotFoundException("Room not found with ID:"+roomId));
        return  modelMapper.map(room, RoomDto.class);
    }

    @Override
    @Transactional
    public void deleteRoomById(Long roomId) {
        log.info("Deleting  the room  with ID:{}", roomId);
        Room room= roomRepository
                .findById(roomId)
                .orElseThrow(()->new ResourceNotFoundException("Room not found with ID:"+roomId));
        roomRepository.deleteById(roomId);
        inventoryService.deleteFutureInventories(room);


    }
}
