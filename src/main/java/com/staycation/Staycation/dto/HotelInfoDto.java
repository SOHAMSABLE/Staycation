package com.staycation.Staycation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data
@AllArgsConstructor
public class HotelInfoDto extends HotelDto {
    private HotelDto hotel;
    private List<RoomDto> rooms;
}
