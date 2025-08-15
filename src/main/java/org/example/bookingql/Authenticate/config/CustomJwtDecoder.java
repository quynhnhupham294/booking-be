package org.example.bookingql.Authenticate.config;

import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.example.bookingql.Authenticate.dto.request.IntrospectRequest;
import org.example.bookingql.Authenticate.service.LoginService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;

/**
 * CustomJwtDecoder:
 * - Kết hợp xác thực token qua introspection API (LoginService) và giải mã token local bằng NimbusJwtDecoder.
 * - Mục đích:
 *    1. Gọi LoginService.introspect(...) để kiểm tra token có hợp lệ và chưa hết hạn từ phía server (tránh dùng token giả hoặc đã revoke).
 *    2. Nếu hợp lệ, dùng NimbusJwtDecoder với SECRET_KEY để giải mã JWT và trích xuất claims.
 *
 * Quy trình:
 * - Bước 1: Gửi token tới introspection endpoint -> nếu không hợp lệ, ném JwtException.
 * - Bước 2: Khởi tạo NimbusJwtDecoder nếu chưa có (lazy init) với thuật toán HS256.
 * - Bước 3: Dùng jwtDecoder.decode(token) để parse JWT thành đối tượng Jwt.
 *
 * Lưu ý:
 * - SECRET_KEY phải khớp với key bên issuer dùng để ký JWT.
 * - Nếu LoginService trả về false hoặc lỗi parse/signature, sẽ ném JwtException để Spring Security xử lý.
 */
@Component
@RequiredArgsConstructor
public class CustomJwtDecoder implements JwtDecoder {
    @Value("${jwt.secret-key}")
    protected String SECRET_KEY;
    private final LoginService loginService;
    private NimbusJwtDecoder jwtDecoder = null;

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            // B1: Kiểm tra token qua introspection API
            var response = loginService.introspect(IntrospectRequest.builder()
                    .token(token)
                    .build()
            );
            if (!response.getIsValidToken()) throw new JwtException("invalid token");
        } catch (JOSEException | ParseException e) {
            throw new JwtException(e.getMessage());
        }

        // B2: Khởi tạo NimbusJwtDecoder nếu chưa có
        if (Objects.isNull(jwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "HS256");
            jwtDecoder = NimbusJwtDecoder
                    .withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS256)
                    .build();
        }

        // B3: Decode JWT để lấy claims
        return jwtDecoder.decode(token);
    }
}
