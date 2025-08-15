package org.example.bookingql.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.bookingql.common.dto.response.PageResponse;
import org.example.bookingql.common.response.ApiResponse;
import org.example.bookingql.dto.request.HotelRequest;
import org.example.bookingql.dto.response.HotelResponse;
import org.example.bookingql.service.HotelService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/hotel")
@RequiredArgsConstructor
public class HotelController {
    private final HotelService hotelService;

    @PostMapping("/create")
    public ApiResponse<HotelResponse> createHotel(@Valid @RequestBody HotelRequest request) {
        HotelResponse response = hotelService.create(request);

        return ApiResponse.<HotelResponse>builder()
                .status(HttpStatus.CREATED.value())
                .success(true)
                .message("Thêm khách sạn thành công")
                .data(response)
                .build();
    }

    @PutMapping("/update/{id}")
    public ApiResponse<HotelResponse> updateHotel(@Valid @RequestBody HotelRequest request,
                                                  @RequestParam UUID id) {
        HotelResponse response = hotelService.update(id, request);

        return ApiResponse.<HotelResponse>builder()
                .status(200)
                .success(true)
                .message("Sửa thông tin khách sạn thành công")
                .data(response)
                .build();
    }

    @GetMapping("/getAll")
    public ApiResponse<PageResponse<HotelResponse>> getAllHotels(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<HotelResponse> hotels = hotelService.getAll(pageable);
        return ApiResponse.<PageResponse<HotelResponse>>builder()
                .status(200)
                .success(true)
                .message("Lấy tất cả thông tin khách sạn")
                .data(hotels)
                .build();
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<Boolean> deleteHotel(@PathVariable UUID id) {
        Boolean response = hotelService.delete(id);

        return ApiResponse.<Boolean>builder()
                .status(200)
                .success(true)
                .message("Xóa khách sạn thành công")
                .data(response)
                .build();
    }
}
