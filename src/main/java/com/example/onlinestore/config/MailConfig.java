package com.example.onlinestore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import java.util.Properties;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // Gmail SMTP Sunucusu
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587); // Gmail için SMTP portu

        // Gmail hesabınızın bilgileri
        mailSender.setUsername("cs308.team17@gmail.com"); // Gmail adresiniz
        mailSender.setPassword("dvfh rsle uvur rflq"); // Gmail şifreniz

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // STARTTLS etkinleştirme
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com"); // SSL için güvenilir host
        props.put("mail.debug", "true");

        return mailSender;
    }
}
