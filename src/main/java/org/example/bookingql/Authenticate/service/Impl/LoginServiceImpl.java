package org.example.bookingql.Authenticate.service.Impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.example.bookingql.Authenticate.dto.request.IntrospectRequest;
import org.example.bookingql.Authenticate.dto.request.LoginRequest;
import org.example.bookingql.Authenticate.dto.response.IntrospectResponse;
import org.example.bookingql.Authenticate.dto.response.LoginResponse;
import org.example.bookingql.Authenticate.service.LoginService;
import org.example.bookingql.entity.User;
import org.example.bookingql.enums.AppException;
import org.example.bookingql.enums.ErrorCode;
import org.example.bookingql.reponsitory.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    @Value("${jwt.access-exp-minutes:60}")
    private long ACCESS_EXP_MINUTES;

    @Value("${jwt.refresh-exp-days:7}")
    private long REFRESH_EXP_DAYS;

    @Override
    public LoginResponse login(LoginRequest request) {
        final String email = request.getEmail();
        final String rawPassword = request.getPassword();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_LOGIN, "Người dùng không tồn tại."));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_LOGIN, "Mật khẩu không đúng.");
        }

        try {
            String accessToken  = generateAccessToken(user);
            String refreshToken = generateRefreshToken(user.getEmail());

            LoginResponse res = new LoginResponse();
            res.setAccessToken(accessToken);
            res.setRefreshToken(refreshToken);
            return res;
        } catch (JOSEException e) {
            throw new AppException(ErrorCode.INVALID_LOGIN, "Không tạo được token: " + e.getMessage());
        }
    }

    @Override
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        String token = request.getToken();
        SignedJWT jwt = SignedJWT.parse(token);

        boolean isVerified = jwt.verify(new MACVerifier(SECRET_KEY.getBytes()));
        boolean isExpired  = jwt.getJWTClaimsSet().getExpirationTime().before(new Date());

        if (!isVerified || isExpired) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        return IntrospectResponse.builder().isValidToken(true).build();
    }

    // ===== Helpers =====

    private String generateAccessToken(User user) throws JOSEException {
        // fallback về "USER" nếu chưa có role
        String roleName = (user.getRole() != null && user.getRole().getRoleName() != null && !user.getRole().getRoleName().isBlank())
                ? user.getRole().getRoleName()
                : "USER";

        String subject  = user.getEmail();
        Integer userId  = user.getIdUser();

        Instant now = Instant.now();
        Instant exp = now.plus(ACCESS_EXP_MINUTES, ChronoUnit.MINUTES);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(subject)
                .jwtID(UUID.randomUUID().toString())
                .issuer("Kynhiu.com")
                .claim("scope", roleName)   // converter sẽ map -> ROLE_*
                .claim("id", userId)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(exp))
                .build();

        SignedJWT signed = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
        signed.sign(new MACSigner(SECRET_KEY.getBytes()));
        return signed.serialize();
    }

    private String generateRefreshToken(String subject) throws JOSEException {
        Instant now = Instant.now();
        Instant exp = now.plus(REFRESH_EXP_DAYS, ChronoUnit.DAYS);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(subject)
                .jwtID(UUID.randomUUID().toString())
                .issuer("Jerry.com")
                .claim("typ", "refresh")
                .issueTime(Date.from(now))
                .expirationTime(Date.from(exp))
                .build();

        SignedJWT signed = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
        signed.sign(new MACSigner(SECRET_KEY.getBytes()));
        return signed.serialize();
    }
}
