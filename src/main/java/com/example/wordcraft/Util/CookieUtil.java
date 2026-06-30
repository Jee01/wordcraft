package com.example.wordcraft.Util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.SerializationUtils;

import java.util.Base64;

public class CookieUtil {
    public static void addCookie(HttpServletResponse response, String name, String value){
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 1);
        response.addCookie(cookie);
    }

    public static void removeCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return;
        }
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                String header = name + "="
                        + "; Path=/"
                        + "; Max-Age=0"
                        + "; HttpOnly"
                        + "; SameSite=Lax";
                if (isSecureEnvironment()) {
                    header += "; Secure";
                }
                response.addHeader("Set-Cookie", header);
            }
        }
    }

    public static void addTokenCookie(HttpServletResponse response, String name, String value, int maxAge) {

        String header = name + "=" + value
                + "; Path=/"
                + "; Max-Age=" + maxAge
                + "; HttpOnly"
                + "; SameSite=Lax";

        if (isSecureEnvironment()) {
            header += "; Secure";
        }
        response.addHeader("Set-Cookie", header);
    }

    private static boolean isSecureEnvironment() {
        String profile = System.getProperty("spring.profiles.active", "");
        return profile.contains("prod");
    }

    public static String serialize(Object object){
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(object));
    }

    public static <T> T deserialize(Cookie cookie, Class<T> clazz){
        return clazz.cast(SerializationUtils.deserialize(
                Base64.getUrlDecoder().decode(cookie.getValue())));
    }
}
