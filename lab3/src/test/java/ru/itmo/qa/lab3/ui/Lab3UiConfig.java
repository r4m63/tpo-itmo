package ru.itmo.qa.lab3.ui;

import java.time.Duration;

final class Lab3UiConfig {
    private final String baseUrl;
    private final boolean headless;
    private final Duration timeout;

    Lab3UiConfig() {
        this.baseUrl = normalizeBaseUrl(System.getProperty("lab3.baseUrl", "https://subscribe.ru/"));
        this.headless = Boolean.parseBoolean(System.getProperty("lab3.headless", "true"));
        this.timeout = Duration.ofSeconds(Long.parseLong(System.getProperty("lab3.timeoutSeconds", "15")));
    }

    String baseUrl() {
        return baseUrl;
    }

    boolean headless() {
        return headless;
    }

    Duration timeout() {
        return timeout;
    }

    private static String normalizeBaseUrl(String rawBaseUrl) {
        String trimmed = rawBaseUrl.trim();
        return trimmed.endsWith("/") ? trimmed : trimmed + "/";
    }
}
