package ru.itmo.qa.lab3.ui;

import java.time.Duration;

// Конфигурация UI-тестов, читаемая из системных свойств JVM.
// Параметры передаются из Gradle через -D флаги, что удобно для CI,
// локального запуска и переключения окружений без правки кода.
// Поддерживаемые свойства:
// lab3.baseUrl, базовый URL тестируемого сайта (по умолчанию https://subscribe.ru/);
// lab3.headless, запускать ли браузер без UI (по умолчанию true);
// lab3.timeoutSeconds, таймаут ожиданий WebDriverWait (по умолчанию 15 секунд).
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

    // Гарантирует завершающий слэш в URL, чтобы относительные ссылки на страницах
    // корректно резолвились.
    private static String normalizeBaseUrl(String rawBaseUrl) {
        String trimmed = rawBaseUrl.trim();
        return trimmed.endsWith("/") ? trimmed : trimmed + "/";
    }
}
