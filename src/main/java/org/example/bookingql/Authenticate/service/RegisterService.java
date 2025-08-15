package org.example.bookingql.Authenticate.service;

import org.example.bookingql.Authenticate.dto.request.RegisterRequest;
import org.example.bookingql.enums.AppException;

public interface RegisterService {
    Boolean register(RegisterRequest request) throws AppException;
}
