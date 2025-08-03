package com.staycation.Staycation.entity;
import com.staycation.Staycation.entity.Hotel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter

public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   @ManyToOne
   @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

   @Column(nullable = false)
   private  String type;

   @Column(nullable = false , precision = 10,scale = 2)
   private BigDecimal basePrice;

    @Column(columnDefinition = "TEXT[]")
    private  String[] photos;

    @Column(columnDefinition = "TEXT[]")
    private  String[] amenities;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private  Integer totalCount;

    @Column(nullable = false)
    private  Integer Capacity;
}
