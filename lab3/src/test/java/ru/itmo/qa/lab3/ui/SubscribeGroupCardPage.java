package ru.itmo.qa.lab3.ui;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

final class SubscribeGroupCardPage extends BasePage {
    private static final By GROUP_TITLE = By.xpath(
            "//h3[a[contains(@href,'/group/')]]");

    SubscribeGroupCardPage(WebDriver driver, Duration timeout) {
        super(driver, timeout);
    }

    void waitUntilLoaded() {
        waitForVisible(GROUP_TITLE);
    }

    boolean hasGroupTitle() {
        String title = visibleText(GROUP_TITLE);
        return title != null && !title.isBlank();
    }
}
