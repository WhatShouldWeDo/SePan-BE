package com.whatshouldwedo.core.utility;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;

import java.util.Arrays;
import java.util.Optional;

public class CookieUtil {

    public static Optional<String> refineCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(name))
                .findFirst()
                .map(Cookie::getValue);
    }

    public static void addCookie(HttpServletResponse response, String cookieDomain, String name, String value) {
        ResponseCookie cookie = buildBaseCookie(name, value, cookieDomain).build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public static void addSecureCookie(HttpServletResponse response, String cookieDomain, String name, String value, Integer maxAge) {
        ResponseCookie cookie = buildBaseCookie(name, value, cookieDomain)
                .maxAge(maxAge)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String cookieDomain, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                deleteCookieByResponse(response, cookieDomain, name);
            }
        }
    }

    public static void deleteCookieByResponse(HttpServletResponse response, String cookieDomain, String name) {
        ResponseCookie removedCookie = buildBaseCookie(name, "", cookieDomain)
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", removedCookie.toString());
    }

    private static ResponseCookie.ResponseCookieBuilder buildBaseCookie(String name, String value, String cookieDomain) {
        boolean isSecure = !cookieDomain.equals("localhost");
        return ResponseCookie.from(name, value)
                .domain(cookieDomain)
                .path("/")
                .httpOnly(true)
                .secure(isSecure)
                .sameSite("Lax");
    }
}
