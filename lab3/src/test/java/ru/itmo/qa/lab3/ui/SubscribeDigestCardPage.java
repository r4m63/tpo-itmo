package ru.itmo.qa.lab3.ui;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

final class SubscribeDigestCardPage extends BasePage {
    private static final By PAGE_BODY = By.xpath("//body");

    SubscribeDigestCardPage(WebDriver driver, Duration timeout) {
        super(driver, timeout);
    }

    void waitUntilLoaded() {
        wait.until(ExpectedConditions.urlContains(".html"));
        waitForVisible(PAGE_BODY);
    }

    boolean hasDigestContent() {
        String title = driver.getTitle();
        return title != null && !title.isBlank();
    }
}
