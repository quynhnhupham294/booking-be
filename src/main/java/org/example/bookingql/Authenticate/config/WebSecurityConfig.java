package org.example.bookingql.Authenticate.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
/**
 * WebSecurityConfig:
 * - Cấu hình Spring Security cho ứng dụng (API Resource Server).
 * - Sử dụng JWT để xác thực (CustomJwtDecoder + CustomJwtAuthenticationConverter).
 * - Cho phép một số endpoint công khai (PUBLIC_ENDPOINTS).
 * - Cấu hình CORS cho phép frontend gọi API.
 * - Tùy chỉnh cách lấy Bearer Token (không đọc token ở các endpoint /api/v1/auth/...).
 *
 * Thành phần chính:
 * 1. BearerTokenResolver:
 *    - Tùy chỉnh cách Spring Security đọc token từ request.
 *    - Nếu request vào các endpoint auth (/api/v1/auth/...) => bỏ qua token.
 *
 * 2. SecurityFilterChain:
 *    - Tắt CSRF (vì đây là API, không cần CSRF token như form login).
 *    - Bật CORS (cho phép cross-origin call từ frontend).
 *    - Cho phép truy cập công khai vào PUBLIC_ENDPOINTS (GET & POST).
 *    - Yêu cầu quyền ROLE_ADMIN cho /user/**.
 *    - Mọi request khác phải xác thực.
 *    - Cấu hình Resource Server để:
 *        + Lấy token bằng BearerTokenResolver.
 *        + Giải mã token bằng CustomJwtDecoder.
 *        + Chuyển JWT thành Authentication với CustomJwtAuthenticationConverter.
 *
 * 3. CorsConfigurationSource:
 *    - Cho phép origin "http://localhost:5173" (frontend dev).
 *    - Cho phép các method GET, POST, PUT, DELETE.
 *    - Cho phép tất cả header.
 *    - Cho phép credentials (cookie, authorization header...).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final CustomJwtDecoder jwtDecoder;
    private final CustomJwtAuthenticationConverter jwtAuthenticationConverter;

    // Danh sách endpoint không yêu cầu xác thực
    private final String[] PUBLIC_ENDPOINTS = {
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/auth/introspect",
            "/api/v1/hotel",
    };

    /**
     * Tuỳ chỉnh cách lấy Bearer Token từ request:
     * - Bỏ qua token nếu request vào /api/v1/auth/...
     * - Các request khác sẽ lấy token từ Authorization header (mặc định của Spring).
     */
    @Bean
    public BearerTokenResolver bearerTokenResolver() {
        DefaultBearerTokenResolver delegate = new DefaultBearerTokenResolver();
        return request -> {
            String path = request.getRequestURI();
            // CHỈ skip token cho các endpoint thực sự public
            if (path.equals("/api/v1/auth/login")
                    || path.equals("/api/v1/auth/register")
                    || path.equals("/api/v1/auth/introspect")) {
                return null;
            }
            return delegate.resolve(request);
        };
    }

    /**
     * Cấu hình chuỗi filter bảo mật (SecurityFilterChain).
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable); // API => tắt CSRF
        http.cors(); // Bật CORS với cấu hình bên dưới
        http
                .authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.GET, PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers("/user/**").hasRole("ADMIN") // Chỉ ADMIN được vào
                        .anyRequest().authenticated() // Các request khác phải login
                )
                .oauth2ResourceServer(configurer -> configurer
                        .bearerTokenResolver(bearerTokenResolver())
                        .jwt(jwtConfigurer -> jwtConfigurer
                                .decoder(jwtDecoder) // Giải mã token
                                .jwtAuthenticationConverter(jwtAuthenticationConverter.converter()) // Chuyển JWT -> Authentication
                        )
                );
        return http.build();
    }

    /**
     * Cấu hình CORS cho phép frontend gọi API.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
