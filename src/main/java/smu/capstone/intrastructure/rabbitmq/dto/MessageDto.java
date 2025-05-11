package smu.capstone.intrastructure.rabbitmq.dto;

import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import smu.capstone.intrastructure.mail.dto.EmailType;

import java.util.Map;


@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class MessageDto {

    private String email;
    private EmailType type;
}
