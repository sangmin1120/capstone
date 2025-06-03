package smu.capstone.intrastructure.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Profile("prod")
@Configuration
public class CorsMvcConfig_prod implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://smu-rehab.duckdns.org") // 나중에 수정해야됨
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 필요한 메서드 허용
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
