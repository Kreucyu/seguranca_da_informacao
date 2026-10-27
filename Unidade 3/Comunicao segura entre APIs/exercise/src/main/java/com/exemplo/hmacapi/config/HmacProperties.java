package com.exemplo.hmacapi.config;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "security.hmac")
public class HmacProperties {

    private long toleranceSeconds = 60;
    private int maxBodyBytes = 1_048_576;
    private Map<String, String> consumers = new HashMap<>();

    public void setToleranceSeconds(long toleranceSeconds) { this.toleranceSeconds = toleranceSeconds; }

    public void setMaxBodyBytes(int maxBodyBytes) { this.maxBodyBytes = maxBodyBytes; }

    public void setConsumers(Map<String, String> consumers) { this.consumers = consumers; }
}
