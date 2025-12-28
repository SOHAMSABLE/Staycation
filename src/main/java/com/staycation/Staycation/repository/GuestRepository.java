package com.staycation.Staycation.repository;

import com.staycation.Staycation.entity.Guest;
import com.staycation.Staycation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface GuestRepository extends JpaRepository<Guest, Long> {
    List<Guest> findByUser(User user);
}