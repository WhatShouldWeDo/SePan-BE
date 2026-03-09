package com.whatshouldwedo.statistics.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "public-data")
public class PublicDataApiProperties {

    private ApiKey dataGoKr = new ApiKey();
    private ApiKey safeMapGoKr = new ApiKey();
    private ApiKey neisGoKr = new ApiKey();
    private ApiKey vworld = new ApiKey();
    private ApiKey kosis = new ApiKey();
    private ApiKey seoulData = new ApiKey();
    private ApiKey foodSafety = new ApiKey();
    private ApiKey careerNet = new ApiKey();
    private ApiKey lofin = new ApiKey();

    @Getter
    @Setter
    public static class ApiKey {
        private String serviceKey;
        private String apiKey;
        private String key;

        /**
         * serviceKey, apiKey, key 중 설정된 값을 반환
         */
        public String getKey() {
            if (serviceKey != null && !serviceKey.isBlank()) return serviceKey;
            if (apiKey != null && !apiKey.isBlank()) return apiKey;
            if (key != null && !key.isBlank()) return key;
            return "";
        }

        public boolean isConfigured() {
            return !getKey().isBlank();
        }
    }
}
