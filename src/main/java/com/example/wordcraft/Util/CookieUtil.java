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

    public static void removeCookie(HttpServletRequest request,HttpServletResponse response, String name){
        Cookie[] cookies = request.getCookies();
        if (cookies == null){
            return;
        }
        for(Cookie cookie : cookies){
            if (name.equals(cookie.getName())){
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
    }

    public static void addTokenCookie(HttpServletResponse response, String name, String value,int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);       // JS 접근 차단
        cookie.setSecure(false);        // 로컬은 false, 배포 시 true로 변경
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
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
