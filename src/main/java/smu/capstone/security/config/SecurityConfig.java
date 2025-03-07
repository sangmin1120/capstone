package smu.capstone.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import smu.capstone.domain.member.respository.UserRepository;
import smu.capstone.jwt.filter.JWTFilter;
import smu.capstone.jwt.repository.RefreshRepository;
import smu.capstone.jwt.util.JWTUtil;
import smu.capstone.security.filter.LoginFilter;
import smu.capstone.security.filter.CustomLogoutFiler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;

    private final UserRepository userRepository;
    private final RefreshRepository refreshRepository;

    //암호화
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        /*
        //cors
        http
                .cors((cors)->cors
                        .configurationSource(new CorsConfigurationSource() {
                            @Override
                            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                                CorsConfiguration config = new CorsConfiguration();
                                config.setAllowCredentials(Collections.singletonList("http://localhost:3000"));
                                config.setAllowedMethods(Collections.singletonList("*"));
                                config.setAllowCredentials(true);
                                config.setAllowedHeaders(Collections.singletonList("*"));
                                config.setMaxAge(3600L);
                                return null;
                            }
                        }));
         */

        //csrf disable
        http
                .csrf((auth) -> auth.disable());

        //From 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());

        //http basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());

        //경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/login", "/", "/join","/error").permitAll()
                        .requestMatchers("/admin").hasRole("USER")
                        .requestMatchers("/reissue").permitAll()
                        .anyRequest().authenticated());

        //filter 등록(JWTFilter)
        http
                .addFilterBefore(new JWTFilter(jwtUtil, userRepository), LoginFilter.class);

        //filter 등록(loginFilter)
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration)
                                , jwtUtil, refreshRepository)
                        , UsernamePasswordAuthenticationFilter.class);
        //filter 등록(logoutFilter)
        http
                .addFilterBefore(new CustomLogoutFiler(jwtUtil, refreshRepository), LogoutFilter.class);

        //세션 설정
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
