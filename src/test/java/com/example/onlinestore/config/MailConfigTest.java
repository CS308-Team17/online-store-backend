package com.example.onlinestore.config;

import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class MailConfigTest {

    @Test
    void javaMailSender_ShouldBeConfiguredCorrectly() {
        // Arrange
        MailConfig mailConfig = new MailConfig();

        // Act
        JavaMailSender mailSender = mailConfig.javaMailSender();

        // Assert
        assertNotNull(mailSender);
        assertTrue(mailSender instanceof JavaMailSenderImpl);

        JavaMailSenderImpl mailSenderImpl = (JavaMailSenderImpl) mailSender;

        // Verify host and port
        assertEquals("smtp.gmail.com", mailSenderImpl.getHost());
        assertEquals(587, mailSenderImpl.getPort());

        // Verify username and password
        assertEquals("cs308.team17@gmail.com", mailSenderImpl.getUsername());
        assertEquals("dvfh rsle uvur rflq", mailSenderImpl.getPassword());

        // Verify mail properties
        Properties mailProperties = mailSenderImpl.getJavaMailProperties();
        assertEquals("smtp", mailProperties.getProperty("mail.transport.protocol"));
        assertEquals("true", mailProperties.getProperty("mail.smtp.auth"));
        assertEquals("true", mailProperties.getProperty("mail.smtp.starttls.enable"));
        assertEquals("smtp.gmail.com", mailProperties.getProperty("mail.smtp.ssl.trust"));
        assertEquals("true", mailProperties.getProperty("mail.debug"));
    }
}
