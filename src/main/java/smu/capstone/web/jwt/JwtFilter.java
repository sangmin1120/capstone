package smu.capstone.web.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import smu.capstone.common.exception.RestApiException;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = tokenProvider.getAccessToken(request);

            tokenProvider.validateToken(TokenType.ACCESS_TOKEN, token);

            Authentication authentication = tokenProvider.createAuthenticationByAccessToken(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        catch (RestApiException ignored) {
        }
        filterChain.doFilter(request, response);
    }
}
