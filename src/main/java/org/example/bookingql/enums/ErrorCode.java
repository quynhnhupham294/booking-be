package org.example.bookingql.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INVALID_LOGIN(1001, "Login không hợp lệ.", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(1001, "Token không hợp lệ.", HttpStatus.UNAUTHORIZED),
    EMAIL_EXISTS(1003, "Email đã tồn tại", HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_MATCH(1004, "Mật khẩu không đúng.", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1005, "Sai thông tin người dùng.", HttpStatus.NOT_FOUND);

    private final Integer status;
    private final String defaultMessage;
    private final HttpStatus httpStatus;
}
