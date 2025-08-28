package com.staycation.Staycation.repository;

import com.staycation.Staycation.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository  extends JpaRepository<Hotel,Long> {
}
