package ru.itmo.qa.lab3.ui;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

// Page Object для страницы регистрации Subscribe.ru.
// Содержит локаторы основных элементов формы: поле email, чекбоксы согласия с условиями
// и обработкой персональных данных, кнопка отправки.
// В текущем наборе тестов класс не используется напрямую (тест регистрации проверяет только
// факт открытия регистрационного потока, без сабмита формы), но сохранён как точка
// расширения: при добавлении полноценного UC-теста с реальной регистрацией здесь уже
// готовы стабильные локаторы.
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
