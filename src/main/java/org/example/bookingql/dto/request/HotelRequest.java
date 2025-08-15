package org.example.bookingql.dto.request;

import lombok.Data;

import java.util.List;
@Data
public class HotelRequest {
    private String hotelName;
    private String address;
    private Float price;
    private List<String> hotelImages;
    private String description;
}
