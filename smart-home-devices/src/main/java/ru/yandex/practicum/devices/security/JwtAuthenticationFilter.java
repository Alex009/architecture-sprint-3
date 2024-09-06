package ru.yandex.practicum.devices.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtParser jwtParser;

    public JwtAuthenticationFilter(String jwtSecret) {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        this.jwtParser = Jwts.parserBuilder().setSigningKey(keyBytes).build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // Извлечение JWT токена из запроса
            String jwt = extractJwtFromRequest(request);

            if (StringUtils.hasText(jwt)) {
                // Извлечение id из токена
                String userId = getUserIdFromJwt(jwt);

                // Создание объекта Authentication
                User user = new User(userId, "", Collections.emptyList());
                Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());

                // Установка Authentication в SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            // Логирование или обработка ошибки
            logger.error("Authorization failed", ex);
        }

        // Продолжение цепочки фильтров
        filterChain.doFilter(request, response);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private String getUserIdFromJwt(String token) {
        Claims claims = (Claims) jwtParser
                .parse(token)
                .getBody();

        return claims.getSubject();
    }
}
