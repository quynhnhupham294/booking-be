package org.example.bookingql.service;

import org.example.bookingql.common.service.BaseService;
import org.example.bookingql.dto.request.HotelRequest;
import org.example.bookingql.dto.response.HotelResponse;
import org.example.bookingql.enums.AppException;

import java.util.UUID;

public interface HotelService extends BaseService<HotelRequest, HotelResponse, UUID> {
}
