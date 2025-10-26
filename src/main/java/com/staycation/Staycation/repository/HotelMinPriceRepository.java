package com.staycation.Staycation.repository;

import com.staycation.Staycation.dto.HotelPriceDto;
import com.staycation.Staycation.entity.Hotel;
import com.staycation.Staycation.entity.HotelMinPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface HotelMinPriceRepository extends JpaRepository<HotelMinPrice,Long> {

    @Query("""
            SELECT new com.staycation.Staycation.dto.HotelPriceDto(i.hotel , AVG(i.price));
            FROM HotelMinPrice i
            WHERE i.city = :city
               AND i.date BETWEEN :startDate AND :endDate
               AND i.hotel.active = true
            GROUP BY i.hotel
       
            """)
    Page<HotelPriceDto> findHotelsWithAvailableInventory(
            @Param("city") String city,
            @Param("startDate") LocalDate startDate,
            @Param("endDate")LocalDate endDate,
            @Param("roomsCount") Integer roomsCount,
            @Param("dateCount")Long dateCount,
            Pageable pageable
    );

    Optional<HotelMinPrice> findByHotelAndDate(Hotel hotel, LocalDate date);
}
