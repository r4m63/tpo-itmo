package ru.itmo.qa.lab3.ui;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

final class SubscribeRegistrationPage extends BasePage {
    private static final By EMAIL_FIELD = By.xpath("//input[@id='arfemail']");
    private static final By TERMS_CHECKBOX = By.xpath("//input[@id='js_tap_panel_checkbox_terms']");
    private static final By PERSONAL_DATA_CHECKBOX = By.xpath("//input[@id='js_tap_panel_checkbox_personal']");
    private static final By SUBMIT_BUTTON = By.xpath("//a[@id='js_regFormBut' and contains(normalize-space(),'Готово')]");

    SubscribeRegistrationPage(WebDriver driver, Duration timeout) {
        super(driver, timeout);
    }

    void waitUntilLoaded() {
        waitForVisible(EMAIL_FIELD);
        waitForVisible(TERMS_CHECKBOX);
        waitForVisible(PERSONAL_DATA_CHECKBOX);
        waitForVisible(SUBMIT_BUTTON);
    }

    boolean hasRequiredFields() {
        return waitForVisible(EMAIL_FIELD).isDisplayed()
                && waitForVisible(TERMS_CHECKBOX).isDisplayed()
                && waitForVisible(PERSONAL_DATA_CHECKBOX).isDisplayed()
                && waitForVisible(SUBMIT_BUTTON).isDisplayed();
    }
}
