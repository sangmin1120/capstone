package smu.capstone.web.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import static smu.capstone.object.member.service.EmailService.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {

    private String email;
    private EmailType type;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class Mail {
        private String email;
    }
}
