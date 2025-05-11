package smu.capstone.intrastructure.rabbitmq.dto;

import lombok.*;
import smu.capstone.intrastructure.mail.dto.EmailType;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AlarmMessageDto extends MessageDto{

    private String title;
    private String startTime;
    private String endTime;
    private String description;

    public Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        map.put("title", title);
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        map.put("description", description);
        return map;
    }

    public AlarmMessageDto(String email, EmailType type, Map<String, String> payload) {
        super(email, type);
        this.title = payload.get("title");
        this.startTime = payload.get("startTime");
        this.endTime = payload.get("endTime");
        this.description = payload.get("description");
    }
}
