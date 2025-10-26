package com.staycation.Staycation.service;
import com.staycation.Staycation.dto.HotelPriceDto;
import com.staycation.Staycation.dto.HotelSearchRequest;
import com.staycation.Staycation.entity.Room;
import org.springframework.data.domain.Page;

public interface InventoryService {

    void initializeRoomForAYear(Room room );

    void  deleteFutureInventories(Room room);

    Page<HotelPriceDto> searchHotels(HotelSearchRequest hotelSearchRequest);

}
