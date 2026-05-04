package ru.itmo.qa.lab3.ui;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

final class WebDriverFactory {
    private WebDriverFactory() {
    }

    static WebDriver create(BrowserType browserType, Lab3UiConfig config) {
        WebDriver driver = switch (browserType) {
            case CHROME -> new ChromeDriver(createChromeOptions(config));
            case FIREFOX -> new FirefoxDriver(createFirefoxOptions(config));
        };

        driver.manage().timeouts().pageLoadTimeout(config.timeout());
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(1600, 1200));
        return driver;
    }

    private static ChromeOptions createChromeOptions(Lab3UiConfig config) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--lang=ru-RU");
        options.addArguments("--window-size=1600,1200");
        options.addArguments("--disable-search-engine-choice-screen");
        options.addArguments("--disable-notifications");
        options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));

        if (config.headless()) {
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
