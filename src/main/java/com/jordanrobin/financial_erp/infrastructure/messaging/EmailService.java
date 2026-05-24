package com.jordanrobin.financial_erp.infrastructure.messaging;

import com.jordanrobin.financial_erp.config.AppProperties;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final AppProperties appProperties;

    @SneakyThrows
    public void sendInvitation(String toEmail, String rawToken) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        helper.setTo(toEmail);
        helper.setSubject("Invitation à rejoindre Financial ERP");
        helper.setFrom("noreply@financial-erp.com");
        String link = appProperties.baseUrl() + appProperties.setPasswordUrl() + "?token=" + rawToken;
        helper.setText("""
                <p>Vous avez été invité à rejoindre Financial ERP.</p>
                <p><a href="%s">Définir mon mot de passe</a></p>
                """.formatted(link), true);
        mailSender.send(message);
    }
}
