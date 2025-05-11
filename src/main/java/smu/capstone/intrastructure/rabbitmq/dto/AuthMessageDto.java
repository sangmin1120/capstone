package smu.capstone.intrastructure.rabbitmq.dto;

import lombok.*;
import smu.capstone.intrastructure.mail.dto.EmailType;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AuthMessageDto extends MessageDto {
    private String key; // 인증 번호 or 새로운 비밀번호 등등

    public AuthMessageDto(String email, EmailType type, String key) {
        super(email, type);
        this.key = key;
    }
}
