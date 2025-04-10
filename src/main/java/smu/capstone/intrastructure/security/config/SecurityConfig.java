package smu.capstone.intrastructure.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import smu.capstone.intrastructure.jwt.filter.JwtFilter;
import smu.capstone.intrastructure.jwt.handler.JwtAccessDeniedHandler;
import smu.capstone.intrastructure.jwt.handler.JwtAuthenticationEntryPoint;
import smu.capstone.intrastructure.jwt.service.TokenProvider;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public JwtFilter jwtFilter(TokenProvider tokenProvider) {
        return new JwtFilter(tokenProvider);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //csrf disable
        http
                .csrf((auth) -> auth.disable());


        http
                .exceptionHandling(configurer -> {
                configurer.authenticationEntryPoint(jwtAuthenticationEntryPoint);
                configurer.accessDeniedHandler(jwtAccessDeniedHandler);
                });
        //Form 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());

        //http basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());

        //경로별 인가 작업
        //로그인이나 회원가입은 인가 x
        //admin은 ADMIN만 가능
        //다른 요청에 대해서는 로그인이 되어야만 가능
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/api/user-auth/**", "/api/user-search/**").permitAll()
                        .requestMatchers("/api/map/**").permitAll() //임시
                        .requestMatchers("/error/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**").permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated());

        //세션 설정
        //JWT에서는 Session을 무상태성으로 관리
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
                .addFilterBefore(jwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}