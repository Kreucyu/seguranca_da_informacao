package com.service.products.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "security.hmac")
public class HmacProperties {

    private long toleranceSeconds = 60;
    private int maxBodyBytes = 1_048_576;
    private Map<String, String> consumers = Map.of();

    public long getToleranceSeconds() {
        return toleranceSeconds;
    }

    public void setToleranceSeconds(long toleranceSeconds) {
        this.toleranceSeconds = toleranceSeconds;
    }

    public int getMaxBodyBytes() {
        return maxBodyBytes;
    }

    public void setMaxBodyBytes(int maxBodyBytes) {
        this.maxBodyBytes = maxBodyBytes;
    }

    public Map<String, String> getConsumers() {
        return consumers;
    }

    public void setConsumers(Map<String, String> consumers) {
        this.consumers = consumers;
    }
}

