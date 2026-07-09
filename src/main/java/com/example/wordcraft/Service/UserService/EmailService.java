package com.example.wordcraft.Service.UserService;

import com.example.wordcraft.DTO.Mail.EmailCodeVerifyDTO;
import com.example.wordcraft.DTO.Mail.EmailVerifyRequestDTO;
import com.example.wordcraft.Exception.EmailSendException;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final Map<String, String> emailMap = new ConcurrentHashMap<>();

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public void sendEmail(EmailVerifyRequestDTO emailVerifyRequestDTO) {
        String code = String.valueOf(100000 + new SecureRandom().nextInt(900000));
        emailMap.put(emailVerifyRequestDTO.getEmail(), code);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(emailVerifyRequestDTO.getEmail());
        message.setSubject("[Wordcraft] 이메일 인증 코드");
        message.setText("인증 코드: " + code);

        try {
            mailSender.send(message);
        } catch (Exception e) {
            throw new EmailSendException("이메일 발송에 실패했습니다. 잠시 후 다시 시도해주세요.");
        }
    }

    public void verifyCode(EmailCodeVerifyDTO emailCodeVerifyDTO) {
        String email = emailCodeVerifyDTO.getEmail();
        String code = emailCodeVerifyDTO.getCode();

        if (!emailMap.containsKey(email))
            throw new IllegalArgumentException("인증 코드가 만료되었습니다.");
        if (!emailMap.get(email).equals(code))
            throw new IllegalArgumentException("인증 코드가 일치하지 않습니다.");

        emailMap.remove(email);
    }
}
