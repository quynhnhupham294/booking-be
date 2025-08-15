package org.example.bookingql.common.service;

import org.example.bookingql.common.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

/**
 * Base Service
 * @param <REQ> Request DTO
 * @param <RES> Response DTO
 * @param <ID> Data type ID
 */
public interface BaseService<REQ, RES, ID> {
    RES create(REQ dto);

    RES update(ID id, REQ dto);

    RES getById(ID id);

    PageResponse<RES> getAll(Pageable pageable);

    boolean delete(ID id);
}
