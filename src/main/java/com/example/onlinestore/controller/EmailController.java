package com.example.onlinestore.controller;

import com.example.onlinestore.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestParam String to, 
                                            @RequestParam String subject, 
                                            @RequestParam String body, 
                                            @RequestParam String attachmentPath) {
        try {
            emailService.sendEmailWithAttachment(to, subject, body, attachmentPath);
            return ResponseEntity.ok("Email sent successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while sending email: " + e.getMessage());
        }
    }
}
