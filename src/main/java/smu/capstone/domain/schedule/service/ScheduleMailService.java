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


import java.io.UnsupportedEncodingException;

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
    public void sendScheduleAlert(String to, Schedule schedule) {
        MimeMessage mimeMessage = createMessage(schedule);
        sendMail(mimeMessage, to);
    }

    private MimeMessage createMessage(Schedule schedule) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            mimeMessage.setSubject("[RehaLink] 일정 알림: " + schedule.getTitle());

            String msg = "";
            msg += "<h2> 일정 알림</h2>";
            msg += "<p>곧 시작될 일정이 있습니다. 아래 내용을 확인해주세요.</p>";
            msg += "<ul>";
            msg += "<li><strong>제목:</strong> " + schedule.getTitle() + "</li>";
            msg += "<li><strong>시작 시각:</strong> " + schedule.getStartTime() + "</li>";
            if (schedule.getEndTime() != null) {
                msg += "<li><strong>종료 시각:</strong> " + schedule.getEndTime() + "</li>";
            }
            if (schedule.getDescription() != null) {
                msg += "<li><strong>내용:</strong> " + schedule.getDescription() + "</li>";
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

