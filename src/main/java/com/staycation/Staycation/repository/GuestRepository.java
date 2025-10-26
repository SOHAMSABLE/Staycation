package com.staycation.Staycation.repository;

import com.staycation.Staycation.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface GuestRepository extends JpaRepository<Guest, Long> {
}