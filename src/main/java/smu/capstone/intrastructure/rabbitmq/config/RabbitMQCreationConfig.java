package smu.capstone.intrastructure.rabbitmq.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RabbitMQCreationConfig {

    // AmpqAdmin -> 기능들을 등록
    public RabbitMQCreationConfig(AmqpAdmin rabbitAdmin,
                                  List<Queue> queues,
                                  List<DirectExchange> exchanges,
                                  List<Binding> bindings) {

        // Exchange 생성 (RabbitMQ에 없는 경우에만)
        for (DirectExchange exchange : exchanges) {
            rabbitAdmin.declareExchange(exchange);
        }

        // Queue 생성 (RabbitMQ에 없는 경우에만)
        for (Queue queue : queues) {
            rabbitAdmin.declareQueue(queue);
        }

        // Binding 생성
        for (Binding binding : bindings) {
            rabbitAdmin.declareBinding(binding);
        }
    }
}
