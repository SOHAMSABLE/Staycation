package com.staycation.Staycation.service;

import com.staycation.Staycation.entity.Room;

public interface InventoryService {

    void initializeRoomForAYear(Room room );

    void  deleteFutureInventories(Room room);


}
