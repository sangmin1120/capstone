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
    @Value("${spring.mail.username}")
    private String username;

    public String sendCertificationKey(String targetEmail) {

        String certificationKey = createCertificationKey();

        MimeMessage mimeMessage = createMessage(certificationKey);
        sendMail(mimeMessage, targetEmail);

        return certificationKey;
    }

    private String createCertificationKey() {

        StringBuilder key = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for (int i = 0; i < 6; i++) {
            int index = secureRandom.nextInt(characters.length());
            char randomChar = characters.charAt(index);
            key.append(randomChar);
        }
        return key.toString();
    }

    private MimeMessage createMessage(String certificationKey) {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            mimeMessage.setSubject("이메일 인증 코드입니다");

            String msg = "";
            msg += "<h1 style=\"font-size: 30px; padding-right: 30px; padding-left: 30px;\">이메일 주소 확인</h1>";
            msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\">아래 확인 코드를 회원가입 화면에서 입력해주세요.</p>";
            msg += "<div style=\"padding-right: 30px; padding-left: 30px; margin: 32px 0 40px;\"><table style=\"border-collapse: collapse; border: 0; background-color: #F4F4F4; height: 70px; table-layout: fixed; word-wrap: break-word; border-radius: 6px;\"><tbody><tr><td style=\"text-align: center; vertical-align: middle; font-size: 30px;\">";
            msg += certificationKey;
            msg += "</td></tr></tbody></table></div>";

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
