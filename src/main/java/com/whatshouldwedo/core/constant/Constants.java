package com.whatshouldwedo.core.constant;

import java.util.List;

public class Constants {

    // JWT
    public static String ACCOUNT_ID_CLAIM_NAME = "aid";
    public static String ACCOUNT_ROLE_CLAIM_NAME = "rol";

    // HEADER
    public static String BEARER_PREFIX = "Bearer ";
    public static String AUTHORIZATION_HEADER = "Authorization";

    /**
     * 인증이 필요 없는 URL (HTTP 메서드 무관)
     */
    public static List<String> NO_NEED_AUTH_URLS = List.of(
            // Auth
            "/api/auth/signup",
            "/api/auth/login",

            // Health Check
            "/actuator/health"
    );

    /**
     * GET 요청만 인증 없이 허용되는 URL
     */
    public static List<String> NO_NEED_AUTH_GET_URLS = List.of(
            "/api/v1/temp"
    );
}
