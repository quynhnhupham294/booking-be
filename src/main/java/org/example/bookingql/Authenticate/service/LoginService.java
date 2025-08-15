package org.example.bookingql.Authenticate.service;

import com.nimbusds.jose.JOSEException;
import org.example.bookingql.Authenticate.dto.request.IntrospectRequest;
import org.example.bookingql.Authenticate.dto.request.LoginRequest;
import org.example.bookingql.Authenticate.dto.response.IntrospectResponse;
import org.example.bookingql.Authenticate.dto.response.LoginResponse;
import org.example.bookingql.enums.AppException;

import java.text.ParseException;

public interface LoginService {
    LoginResponse login(LoginRequest request) throws JOSEException, ParseException ;
    IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException;
}
