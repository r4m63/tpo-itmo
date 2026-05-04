package ru.itmo.qa.lab3.ui;

import java.util.Locale;
import java.util.stream.Stream;

enum BrowserType {
    CHROME,
    FIREFOX;

    static Stream<BrowserType> configuredBrowsers() {
        String requestedBrowser = System.getProperty("lab3.browser", "all")
                .trim()
                .toLowerCase(Locale.ROOT);

        return switch (requestedBrowser) {
            case "all" -> Stream.of(values());
            case "chrome" -> Stream.of(CHROME);
            case "firefox" -> Stream.of(FIREFOX);
            default -> throw new IllegalArgumentException("Unsupported browser: " + requestedBrowser);
        };
    }
}
