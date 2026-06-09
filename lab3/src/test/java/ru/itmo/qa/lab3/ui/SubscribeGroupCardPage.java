package ru.itmo.qa.lab3.ui;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

// Page Object для карточки конкретной группы (UC-05).
// Subscribe.ru на странице группы использует h3 для заголовка группы (а не h1, как
// можно было бы ожидать). Привязка XPath к структуре h3 > a[href*='/group/']
// специфична для карточки группы и не совпадает со страницей каталога, где h2.
final class SubscribeGroupCardPage extends BasePage {
    private static final By GROUP_TITLE = By.xpath(
            "//h3[a[contains(@href,'/group/')]]");

    SubscribeGroupCardPage(WebDriver driver, Duration timeout) {
        super(driver, timeout);
    }

    void waitUntilLoaded() {
        waitForVisible(GROUP_TITLE);
    }

    // Карточка считается валидной, если заголовок группы непустой.
    boolean hasGroupTitle() {
        String title = visibleText(GROUP_TITLE);
        return title != null && !title.isBlank();
    }
}
