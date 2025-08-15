package org.example.bookingql.dto.response;

import lombok.Data;
import org.example.bookingql.entity.Hotel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class HotelResponse {
    private UUID idHotels;
    private String hotelName;
    private String address;
    private Float price;
    private List<String> hotelImages;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
}
