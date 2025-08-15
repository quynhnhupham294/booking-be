package org.example.bookingql.Authenticate.service.Impl;

import lombok.RequiredArgsConstructor;
import org.example.bookingql.Authenticate.dto.request.RegisterRequest;
import org.example.bookingql.Authenticate.service.RegisterService;
import org.example.bookingql.entity.Role;
import org.example.bookingql.entity.User;
import org.example.bookingql.enums.AppException;
import org.example.bookingql.enums.ErrorCode;
import org.example.bookingql.reponsitory.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public Boolean register(RegisterRequest request) throws AppException {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTS, "Email không tồn tại.");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH, "Mật khẩu xác nhận không khớp.");
        }

        User userClientRequest = new User();
        userClientRequest.setUsername(request.getUsername());
        userClientRequest.setEmail(request.getEmail());
        userClientRequest.setPassword(passwordEncoder.encode(request.getPassword()));
        userClientRequest.setIdGoogle(false);
        userClientRequest.setIdFacebook(false);
        userClientRequest.setCreatedAt(new Date());
        Role defaultRole = new Role();
        defaultRole.setIdRole(3);
        userClientRequest.setRole(defaultRole);

        userRepository.save(userClientRequest);
        return true;
    }
}
