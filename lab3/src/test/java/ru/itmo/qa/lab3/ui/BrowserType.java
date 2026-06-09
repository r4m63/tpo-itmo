package ru.itmo.qa.lab3.ui;

import java.util.Locale;
import java.util.stream.Stream;

// Перечисление поддерживаемых браузеров для UI-тестов.
// Используется как параметр для ParameterizedTest через метод configuredBrowsers,
// который читает системное свойство lab3.browser и возвращает список браузеров для прогона.
// Такая схема позволяет одной командой Gradle запускать тесты в выбранном браузере
// без правки исходников: -Dlab3.browser=chrome|firefox|all.
enum BrowserType {
    CHROME,
    FIREFOX;

    // Возвращает поток браузеров, в которых нужно запускать тесты.
    // Источник конфигурации, системное свойство lab3.browser:
    // all (по умолчанию), оба браузера, тест запускается дважды;
    // chrome, только Chrome;
    // firefox, только Firefox.
    // Соответствует Gradle-задачам chromeUiTest, firefoxUiTest, uiTest.
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
