package smu.capstone.jwt.service;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import smu.capstone.jwt.entity.RefreshEntity;
import smu.capstone.jwt.repository.RefreshRepository;
import smu.capstone.jwt.util.JWTUtil;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ReissueService {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public String reissueRefresh(HttpServletRequest request, HttpServletResponse response) {
        //get refresh token
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        //검증
        if (refresh == null) {
            //response
            return "refresh token null";
        }

        //expired check
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            return "access token expired";
        }

        //refresh 인지 확인
        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {
            return "invalid refresh token";
        }

        //DB 에 refresh 가 저장되어 잇는지 확인
        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        if (!isExist) {
            return "invalid refresh token";
        }

        String email = jwtUtil.getEmail(refresh);
        String role = jwtUtil.getRole(refresh);

        //make new JWT
        String newAccess = jwtUtil.createJwt("access", email, role, 600000L);
        String newRefresh = jwtUtil.createJwt("refresh", email, role, 86400000L);

        //refresh 토큰 저장 DB에 기존의 refresh 토큰 삭제 후 새 refresh 토큰 저장
        refreshRepository.deleteByRefresh(refresh);
        addRefreshEntity(email, refresh, 86400000L);
        
        //response
        response.setHeader("access", newAccess);
        response.addCookie(createCookie("refresh", newRefresh));

        return "OK";
    }

    private void addRefreshEntity(String email, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setEmail(email);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        //cookie.setSecure(true);
        //cookie.setPath("/");
        cookie.setHttpOnly(true); // 클라이언트 단에서 접근 못하도록 막는다.

        return cookie;
    }
}
