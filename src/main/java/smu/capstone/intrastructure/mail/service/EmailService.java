package smu.capstone.intrastructure.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.intrastructure.mail.dto.EmailType;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Map;

import static smu.capstone.common.errorcode.AuthExceptionCode.FAIL_TO_SEND_MAIL;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${mail.username}")
    private String username;

    public void sendMailWithKey(String targetEmail, EmailType type, String key) {
        MimeMessage mimeMessage = createMessage(type, key);
        sendMail(mimeMessage, targetEmail);
    }

    private MimeMessage createMessage(EmailType type, String key) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            mimeMessage.setSubject(type.getSubject());
            String msg = "";
            msg += "<h1 style=\"font-size: 30px; padding: 30px;\">" + type.getTitle() + "</h1>";
            msg += "<p style=\"font-size: 17px; padding: 30px;\">" + type.getMessage() + "</p>";
            msg += "<div style=\"padding: 30px; margin: 32px 0 40px;\">"
                    + "<table style=\"border-collapse: collapse; background-color: #F4F4F4; height: 70px; border-radius: 6px;\">"
                    + "<tbody><tr><td style=\"text-align: center; font-size: 30px;\">" + key + "</td></tr></tbody></table></div>";
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
