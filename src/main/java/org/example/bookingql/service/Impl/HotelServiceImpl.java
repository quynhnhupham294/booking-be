package org.example.bookingql.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bookingql.common.dto.response.PageResponse;
import org.example.bookingql.dto.request.HotelRequest;
import org.example.bookingql.dto.response.HotelResponse;
import org.example.bookingql.entity.Hotel;
import org.example.bookingql.mapper.HotelMapper;
import org.example.bookingql.reponsitory.HotelRepository;
import org.example.bookingql.service.HotelService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelServiceImpl implements HotelService {
    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;

    @Override
    public HotelResponse create(HotelRequest request) {
        Hotel hotel = hotelMapper.toEntity(request);

        hotel.setCreatedAt(LocalDateTime.now());
        hotel.setUpdatedAt(LocalDateTime.now());

        Hotel saved = hotelRepository.save(hotel);
        return hotelMapper.toResponse(saved);
    }

    @Override
    public HotelResponse update(UUID id, HotelRequest dto) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found"));

        hotel.setUpdatedAt(LocalDateTime.now());
        Hotel updated = hotelRepository.save(hotel);
        return hotelMapper.toResponse(updated);
    }

    @Override
    public HotelResponse getById(UUID uuid) {
        return null;
    }

    @Override
    public PageResponse<HotelResponse> getAll(Pageable pageable) {
        Page<HotelResponse> page = hotelRepository.findAll(pageable)
                .map(hotelMapper::toResponse);

        return new PageResponse<>(
                page.getContent(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getNumber(),
                page.getSize()
        );
    }

    @Override
    public boolean delete(UUID id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found"));

        hotel.setIsDeleted(true);
        hotel.setUpdatedAt(LocalDateTime.now());

        hotelRepository.delete(hotel);
        return true;
    }
}
