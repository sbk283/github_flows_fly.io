package com.apartment.apart.domain.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
     private final JavaMailSender mailSender;

     public void send(String to, String subject, String body) {

          MimeMessage mimeMessage = mailSender.createMimeMessage();

          try {
               MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false,"UTF-8");
               mimeMessageHelper.setTo(to);
               mimeMessageHelper.setSubject(subject);
               mimeMessageHelper.setText(body,true);
               mailSender.send(mimeMessage);
          } catch (MessagingException e) {
               throw new RuntimeException(e);
          }

     }
}
