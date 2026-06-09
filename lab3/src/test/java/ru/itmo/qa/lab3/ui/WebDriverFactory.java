package ru.itmo.qa.lab3.ui;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

// Фабрика WebDriver для Chrome и Firefox.
// Драйверы создаются с настройками, которые делают тесты стабильными для русского
// сайта Subscribe.ru: устанавливается русская локаль, отключаются всплывающие подсказки
// Chrome (выбор поисковой системы, нотификации), задаётся одинаковый размер окна
// для воспроизводимости верстки.
// Селениум 4 умеет сам качать драйверы через Selenium Manager, поэтому WebDriverManager
// или ручная установка chromedriver/geckodriver не требуются.
final class WebDriverFactory {
    private WebDriverFactory() {
    }

    static WebDriver create(BrowserType browserType, Lab3UiConfig config) {
        WebDriver driver = switch (browserType) {
            case CHROME -> new ChromeDriver(createChromeOptions(config));
            case FIREFOX -> new FirefoxDriver(createFirefoxOptions(config));
        };

        driver.manage().timeouts().pageLoadTimeout(config.timeout());
        // Фиксированный размер окна для воспроизводимости: динамические элементы сайта
        // могут вести себя по-разному при узком и широком viewport.
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(1600, 1200));
        return driver;
    }

    private static ChromeOptions createChromeOptions(Lab3UiConfig config) {
        ChromeOptions options = new ChromeOptions();
        // Русская локаль нужна, чтобы XPath-локаторы по русским текстам ссылок (Группы,
        // Выпуски, Подписаться) гарантированно совпадали с тем, что отрендерит сайт.
        options.addArguments("--lang=ru-RU");
        options.addArguments("--window-size=1600,1200");
        // Отключаем экраны Chrome, которые перекрывают элементы и ломают клики.
        options.addArguments("--disable-search-engine-choice-screen");
        options.addArguments("--disable-notifications");
        // Убираем баннер Chrome контролируется автоматизированным ПО, который иногда
        // мешает тестам сайтов с антибот-защитой.
        options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));

        if (config.headless()) {
            // headless=new это новый headless-режим Chrome 109+, поведение ближе к обычному
            // браузеру, чем у старого --headless.
            options.addArguments("--headless=new");
        }

        return options;
    }

    private static FirefoxOptions createFirefoxOptions(Lab3UiConfig config) {
        FirefoxOptions options = new FirefoxOptions();
        options.addPreference("intl.accept_languages", "ru-RU,ru");

        if (config.headless()) {
            options.addArguments("-headless");
        }

        return options;
    }
}
