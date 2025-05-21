package com.bu.getactivecore.service.emailService;

import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.bu.getactivecore.service.emailService.api.EmailApi;
import org.springframework.beans.factory.annotation.Value;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailVerificationService implements EmailApi {

    private final JavaMailSender m_javaEmailSender;

    @Value("${spring.mail.username}")
    private String m_serverEmail;

    public EmailVerificationService(JavaMailSender javaEmailSender) {
        m_javaEmailSender = javaEmailSender;
    }

    @Override
    public void sendVerificationEmail(@NonNull String email) {
        String body = String.format(EmailTemplates.REGISTRATION_TEMPLATE, email);
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(m_serverEmail);
        msg.setTo(email);
        msg.setSubject("GetActive: Registration Verification");
        msg.setText(body);

        log.debug("Sending verification email to: {}", email);
        try {
            m_javaEmailSender.send(msg);
        } catch (MailSendException e) {
            log.error("Failed to send verification email to: {}", email, e);
        }
    }


}
