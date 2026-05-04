package ru.itmo.qa.lab3.ui;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

final class SubscribeGroupsPage extends BasePage {
    private static final By FIRST_GROUP_CARD = By.xpath("(//h2/a)[1]");

    SubscribeGroupsPage(WebDriver driver, Duration timeout) {
        super(driver, timeout);
    }

    void waitUntilLoaded() {
        waitForVisible(FIRST_GROUP_CARD);
    }

    boolean hasGroupsContent() {
        return waitForVisible(FIRST_GROUP_CARD).isDisplayed();
    }

    void openFirstGroup() {
        click(FIRST_GROUP_CARD);
    }
}
