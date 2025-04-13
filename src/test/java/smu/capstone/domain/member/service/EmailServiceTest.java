package smu.capstone.domain.member.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import smu.capstone.CapstoneApplication;
import smu.capstone.domain.member.dto.AuthRequestDto;
import smu.capstone.intrastructure.mail.service.EmailService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
@ContextConfiguration(classes = CapstoneApplication.class)
class EmailServiceTest {

    private EmailService emailService;
    private SignupService signupService;

    @Autowired
    public void setEmailService(EmailService emailService, SignupService signupService) {
        this.emailService = emailService;
        this.signupService = signupService;
    }

    @Test
    @DisplayName("메일 전송 시간 측정")
    void sendEmail() throws InterruptedException {

        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            int threadNum = i;
            String email = "vecodoy172@oronny.com";
            AuthRequestDto.VerificationMail verificationMail = new AuthRequestDto.VerificationMail(email, "");
            executorService.submit(() -> {
                try {
                    System.out.println(threadNum + " thread Start");
                    signupService.sendVerificationMail(verificationMail);
                    System.out.println(threadNum + " thread End");
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        long endTime = System.currentTimeMillis();
        System.out.println("Total execution time " + (endTime - startTime) + "ms");
    }
}