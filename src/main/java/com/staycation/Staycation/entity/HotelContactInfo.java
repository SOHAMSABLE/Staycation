package com.staycation.Staycation.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Embeddable
@Table(name = "hotel_contact_info")
public class HotelContactInfo {

    private String address;
    private String phoneNumber;
    private String email;
    private String location;


}
