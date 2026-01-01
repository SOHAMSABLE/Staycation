package com.staycation.Staycation.repository;

import com.staycation.Staycation.entity.Hotel;
import com.staycation.Staycation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository  extends JpaRepository<Hotel,Long> {
    List<Hotel> findByOwner(User user);
}
