package smu.capstone.web.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import smu.capstone.common.errorcode.CommonStatusCode;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.common.response.BaseResponse;

import java.io.IOException;


@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        BaseResponse<Void> baseResponse = BaseResponse.fail(new RestApiException(CommonStatusCode.UNAUTHORIZED));

        response.setStatus(baseResponse.getStatusCode().status().value());
        response.setContentType("application/json;charset=UTF-8");
        String result = mapper.writeValueAsString(baseResponse);
        response.getWriter().write(result);
    }
}
