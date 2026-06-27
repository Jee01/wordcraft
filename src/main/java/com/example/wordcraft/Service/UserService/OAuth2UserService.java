package com.example.wordcraft.Service.UserService;

import com.example.wordcraft.Entity.Users;
import com.example.wordcraft.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class OAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String nickname = (String) attributes.get("name");
        String sub = (String) attributes.get("sub");

        userRepository.findByEmail(email).orElseGet(()->
                userRepository.save(Users.builder()
                        .email(email)
                        .nickname(nickname)
                        .password(null)
                        .provider("google")
                        .providerId(sub)
                        .build())
        );

        return oAuth2User;
    }
}
