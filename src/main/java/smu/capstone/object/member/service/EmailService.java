package smu.capstone.object.member.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import smu.capstone.common.exception.RestApiException;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

import static smu.capstone.common.errorcode.AuthExceptionCode.FAIL_TO_SEND_MAIL;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${mail.username}")
    private String username;

    public String sendCertificationKey(String targetEmail, EmailType type) {
        String key = generateKey(type);
        MimeMessage mimeMessage = createMessage(type, key);
        sendMail(mimeMessage, targetEmail);
        return key;
    }

    private String generateKey(EmailType type) {
        String characters = type == EmailType.PASSWORD_RESET
                ? "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()"
                : "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            int index = secureRandom.nextInt(characters.length());
            key.append(characters.charAt(index));
        }
        return key.toString();
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

    public enum EmailType {
        SIGNUP_CODE_MAIL("이메일 인증 코드입니다", "이메일 주소 확인", "아래 확인 코드를 회원가입 화면에서 입력해주세요."),
        PASSWORD_CODE_MAIL("비밀번호 찾기 코드입니다.", "인증 번호 확인", "아래 확인 코드를 인증 화면에 입력해주세요."),
        PASSWORD_RESET("비밀번호 재설정 코드입니다", "비밀번호 재설정", "아래 코드를 입력하여 새로운 비밀번호를 설정해주세요.");

        private final String subject;
        private final String title;
        private final String message;

        EmailType(String subject, String title, String message) {
            this.subject = subject;
            this.title = title;
            this.message = message;
        }

        public String getSubject() { return subject; }
        public String getTitle() { return title; }
        public String getMessage() { return message; }
    }
}
