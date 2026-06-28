package com.example.wordcraft.Service.UserService;

import com.example.wordcraft.DTO.Mail.EmailCodeVerifyDTO;
import com.example.wordcraft.DTO.Mail.EmailVerifyRequestDTO;
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
    private final Map<String,String> emailMap = new ConcurrentHashMap<>();
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

            mailSender.send(message);

    }
    public Boolean verifyCode(EmailCodeVerifyDTO emailCodeVerifyDTO) {
        String email = emailCodeVerifyDTO.getEmail();
        String code = emailCodeVerifyDTO.getCode();

        if (!emailMap.containsKey(email)) return false;
        if (!emailMap.get(email).equals(code)) return false;

        emailMap.remove(email); // 인증 완료 후 삭제
        return true;
    }
}
