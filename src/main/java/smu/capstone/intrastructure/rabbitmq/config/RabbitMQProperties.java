package smu.capstone.intrastructure.rabbitmq.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties("spring.rabbitmq")
public class RabbitMQProperties {

    private String host;
    private int port;
    private String username;
    private String password;
    private List<BindingConfig> bindings;

    @Data
    public static class BindingConfig {

        private QueueConfig queue;
        private ExchangeConfig exchange;
        private RoutingKeyConfig routing;

        @Data
        public static class QueueConfig {
            private String name;
        }

        @Data
        public static class ExchangeConfig {
            private String name;
        }

        @Data
        public static class RoutingKeyConfig {
            private String name;
        }
    }
}
