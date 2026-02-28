package com.whatshouldwedo.security.filter;

import com.whatshouldwedo.core.constant.Constants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class GlobalLoggerFilter extends OncePerRequestFilter {

    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String uri = request.getRequestURI();

        // 🔥 1) 로그 제외 URL 이면 -> 필터 패스
        if (isNoLoggingRequired(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 🔥 2) 일반 로그 처리
        log.info("[Global] HTTP Request Received! ({} {} {})",
                getClientIp(request),
                request.getMethod(),
                uri);

        request.setAttribute("INTERCEPTOR_PRE_HANDLE_TIME", System.currentTimeMillis());

        filterChain.doFilter(request, response);

        long duration = System.currentTimeMillis() - (Long) request.getAttribute("INTERCEPTOR_PRE_HANDLE_TIME");

        log.info("[Global] HTTP Request Has Been Processed! It Tokes {}ms. ({} {} {})",
                duration,
                getClientIp(request),
                request.getMethod(),
                uri);
    }

    /**
     * ============================= PRIVATE METHODS =============================
     */

    private boolean isNoLoggingRequired(String uri) {
        return Constants.NO_NEED_AUTH_URLS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, uri));
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-FORWARDED-FOR");
        return forwarded != null ? forwarded : request.getRemoteAddr();
    }
}