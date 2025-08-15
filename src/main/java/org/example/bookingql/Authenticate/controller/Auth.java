package org.example.bookingql.Authenticate.controller;

import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.example.bookingql.Authenticate.dto.request.IntrospectRequest;
import org.example.bookingql.Authenticate.dto.request.LoginRequest;
import org.example.bookingql.Authenticate.dto.request.RegisterRequest;
import org.example.bookingql.Authenticate.dto.response.IntrospectResponse;
import org.example.bookingql.Authenticate.dto.response.LoginResponse;
import org.example.bookingql.Authenticate.service.LoginService;
import org.example.bookingql.Authenticate.service.RegisterService;
import org.example.bookingql.common.response.ApiResponse;
import org.example.bookingql.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class Auth {
    private LoginService loginService;
    private RegisterService registerService;

    @Autowired
    public Auth(LoginService loginService, RegisterService registerService) {
        this.loginService = loginService;
        this.registerService = registerService;
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) throws JOSEException, ParseException {
        return ApiResponse.<IntrospectResponse>builder()
                .status(HttpStatus.OK.value())
                .success(true)
                .data(loginService.introspect(request))
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) throws JOSEException, ParseException {
        LoginResponse response = loginService.login(request);

        return ApiResponse.<LoginResponse>builder()
                .status(HttpStatus.OK.value())
                .success(true)
                .message("Login thành công")
                .data(response)
                .build();
    }

    @PostMapping("/register")
    ApiResponse<Void> register(@RequestBody RegisterRequest request) {
        registerService.register(request);

        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .success(true)
                .message("Đăng ký thành công")
//                .data(response)
                .build();
    }

    @GetMapping("/getAll")
    public List<User> getAll() {
        return null;
    }

}
