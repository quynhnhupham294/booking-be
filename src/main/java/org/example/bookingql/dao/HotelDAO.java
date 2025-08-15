package org.example.bookingql.dao;

import org.example.bookingql.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HotelDAO extends JpaRepository<Hotel, UUID> {

}
