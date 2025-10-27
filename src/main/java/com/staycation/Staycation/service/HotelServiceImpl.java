package com.staycation.Staycation.service;

import com.staycation.Staycation.dto.HotelDto;
import com.staycation.Staycation.dto.HotelInfoDto;
import com.staycation.Staycation.dto.RoomDto;
import com.staycation.Staycation.entity.Hotel;
import com.staycation.Staycation.entity.Room;
import com.staycation.Staycation.entity.User;
import com.staycation.Staycation.exception.ResourceNotFoundException;
import com.staycation.Staycation.exception.UnAuthorisedException;
import com.staycation.Staycation.repository.HotelRepository;
import com.staycation.Staycation.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {
    private final RoomRepository roomRepository;

    private final HotelRepository hotelRepository;
    private  final ModelMapper modelMapper;
    private  final  InventoryService inventoryService;

    @Override
    public HotelDto createNewHotel(HotelDto hotelDto) {
        log.info("Creating a new hotel with name: {}", hotelDto.getName());
        Hotel hotel = modelMapper.map(hotelDto, Hotel.class);
        if (hotelDto.getActive() == null) {
            hotelDto.setActive(false);
        }

        User user =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        hotel.setOwner(user);

        // Map DTO to entity, respect 'active' value from JSON


        // Save hotel
        hotel = hotelRepository.save(hotel);
        log.info("Created a new hotel with ID: {}", hotel.getId());

        // If hotel is active and has rooms already (optional, mostly for updates)
        if (hotel.getActive() && hotel.getRooms() != null) {
            for (Room room : hotel.getRooms()) {
                inventoryService.initializeRoomForAYear(room);
            }
        }

        return modelMapper.map(hotel, HotelDto.class);
    }


    @Override
    public HotelDto getHotelById(Long id) {
        log.info("Getting the hotel with ID:{}",id);
        Hotel hotel= hotelRepository
                .findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Hotel not found with ID:"+id));

        User user =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.equals(hotel.getOwner())){
            throw  new UnAuthorisedException("This user does not own this hotel with id"+ id);
        }
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto updateHotelById(Long id, HotelDto hotelDto) {
        log.info("Updating the hotel with ID:{}",id);
        Hotel hotel= hotelRepository
                .findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Hotel not found with ID:"+id));
        modelMapper.map(hotelDto,hotel);
        hotel.setId(id);
       hotel= hotelRepository.save(hotel);
        return modelMapper.map(hotel,HotelDto.class);
    }

    @Override
   @Transactional
    public void deleteHotelById(Long id) {
        log.info("Deleting the hotel withID: {}" , id);
        Hotel hotel = hotelRepository
                .findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel not found with ID:"+id));

        User user =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.equals(hotel.getOwner())){
            throw  new UnAuthorisedException("This user does not own this hotel with id"+ id);
        }


        for (Room room : hotel.getRooms()){
            inventoryService.deleteFutureInventories(room);
            roomRepository.deleteById(room.getId());
        }

        hotelRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void activateHotel(Long hotelId) {
     log.info("Activating the hotel withID: {}" , hotelId);
     Hotel hotel = hotelRepository
             .findById(hotelId)
             .orElseThrow(()-> new ResourceNotFoundException("Hotel not found with ID:"+hotelId));

        User user =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.equals(hotel.getOwner())){
            throw  new UnAuthorisedException("This user does not own this hotel with id"+ hotelId);
        }

     hotel.setActive(true);
     //Assuming only do it once(first time activating hotel)
        for (Room room : hotel.getRooms()){
            inventoryService.initializeRoomForAYear(room);
        }


    }

    @Override
    public HotelInfoDto getHotelInfoById(Long hotelId) {
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel not found with ID:"+hotelId));
        List<RoomDto> rooms = hotel.getRooms().stream()
                .map((element) -> modelMapper.map(element, RoomDto.class))
                .toList();
        return  new HotelInfoDto(modelMapper.map(hotel,HotelDto.class),rooms);
    }
}
