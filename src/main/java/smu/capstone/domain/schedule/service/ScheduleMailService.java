package smu.capstone.domain.schedule.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.domain.schedule.domain.Schedule;
import smu.capstone.intrastructure.mail.dto.EmailType;
import smu.capstone.intrastructure.rabbitmq.dto.AlarmMessageDto;


import java.io.UnsupportedEncodingException;
import java.util.Map;

import static smu.capstone.common.errorcode.AuthExceptionCode.FAIL_TO_SEND_MAIL;

@Service
@RequiredArgsConstructor
public class ScheduleMailService {

    // 상민님이 등록한 메일 보내는 bean
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String username;

    /**
     * 일정 알림 메일 전송
     */
    public void sendScheduleAlert(AlarmMessageDto messageDto) {
        String targetEmail = messageDto.getEmail();
        EmailType type = messageDto.getType();
        Map<String,String> schedule = messageDto.getMap();


        MimeMessage mimeMessage = createMessage(type, schedule);
        sendMail(mimeMessage, targetEmail);
    }

    private MimeMessage createMessage(EmailType type, Map<String,String> schedule) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        String title = schedule.get("title");
        String startTime = schedule.get("startTime");
        String endTime = schedule.get("endTime");
        String description = schedule.get("description");

//        "[RehaLink] 일정 알림: "
        try {
            mimeMessage.setSubject(type.getSubject() + " " + title);

            String msg = "";
            msg += "<h2> 일정 알림</h2>";
            msg += "<p>곧 시작될 일정이 있습니다. 아래 내용을 확인해주세요.</p>";
            msg += "<ul>";
            msg += "<li><strong>제목:</strong> " + title + "</li>";
            msg += "<li><strong>시작 시각:</strong> " + startTime + "</li>";
            if (endTime != null) {
                msg += "<li><strong>종료 시각:</strong> " + endTime + "</li>";
            }
            if (description != null) {
                msg += "<li><strong>내용:</strong> " + description + "</li>";
            }
            msg += "</ul>";
            msg += "<br><p style='color:gray;'>본 알림은 RehaLink 일정 관리 서비스에 의해 자동 발송되었습니다.</p>";

            mimeMessage.setText(msg, "utf-8", "html");
        } catch (MessagingException e) {
            throw new RestApiException(FAIL_TO_SEND_MAIL);
        }

        return mimeMessage;
    }

    private void sendMail(MimeMessage mimeMessage, String targetEmail) {
        try {
            mimeMessage.addRecipients(MimeMessage.RecipientType.TO, targetEmail);
            mimeMessage.setFrom(new InternetAddress(username, "RehaLink"));
            javaMailSender.send(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RestApiException(FAIL_TO_SEND_MAIL);
        }
    }
}

