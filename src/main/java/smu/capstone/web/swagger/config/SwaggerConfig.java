package smu.capstone.web.swagger.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    Info info = new Info().title("Swagger Test").version("0.0.1")
            .description("<h3>Swagger test</h3");

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(info);
    }
}
