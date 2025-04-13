package smu.capstone.intrastructure.rabbitmq.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import smu.capstone.intrastructure.mail.dto.EmailType;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {

    private String email;
    private EmailType type;
    private String payLoad;

    public MessageDto(String email, EmailType type) {
        this.email = email;
        this.type = type;
    }
}
