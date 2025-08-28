package com.staycation.Staycation.repository;

import com.staycation.Staycation.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository <Room,Long> {
}
