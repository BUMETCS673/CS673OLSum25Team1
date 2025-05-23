package com.bu.getactivecore.service.email;

import com.bu.getactivecore.service.email.api.EmailApi;
import com.bu.getactivecore.shared.ErrorCode;
import com.bu.getactivecore.shared.exception.ApiException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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
    public void sendVerificationEmail(@NonNull String email, @NonNull String registrationUrl) throws ApiException {
        String body = String.format(EmailTemplates.REGISTRATION_TEMPLATE, registrationUrl);
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(m_serverEmail);
        msg.setTo(email);
        msg.setSubject("GetActive: Registration Verification");
        msg.setText(body);

        log.debug("Sending verification email to: {}", email);
        try {
            m_javaEmailSender.send(msg);
        } catch (MailParseException | MailSendException e) {
            log.error("Failed to send verification email to: {}", email, e);
            throw new ApiException(HttpStatus.BAD_REQUEST, ErrorCode.EMAIL_SEND_FAILED, "Unable to send verification email to email address: '" + email + "'", e);
        }
    }


}
