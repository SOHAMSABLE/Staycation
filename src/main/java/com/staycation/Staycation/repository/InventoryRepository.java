package com.staycation.Staycation.repository;

import com.staycation.Staycation.entity.Inventory;
import com.staycation.Staycation.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory,Long> {
    void deleteByDateAfterAndRoom(LocalDate date , Room room);
}
