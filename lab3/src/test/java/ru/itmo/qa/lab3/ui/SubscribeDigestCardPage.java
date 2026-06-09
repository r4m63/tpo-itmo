package ru.itmo.qa.lab3.ui;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

// Page Object для карточки отдельного выпуска (UC-04).
// Карточки выпусков на Subscribe.ru имеют разную HTML-структуру в зависимости от категории,
// поэтому привязка к конкретным классам или id ненадёжна. Вместо этого проверяется,
// что URL содержит .html (сигнал, что мы на странице конкретной статьи) и страница
// успешно загружена (виден body, есть title).
final class SubscribeDigestCardPage extends BasePage {
    private static final By PAGE_BODY = By.xpath("//body");

    SubscribeDigestCardPage(WebDriver driver, Duration timeout) {
        super(driver, timeout);
    }

    // Ждём, пока URL сменится на статью (.html), затем подтверждаем рендеринг через body.
    void waitUntilLoaded() {
        wait.until(ExpectedConditions.urlContains(".html"));
        waitForVisible(PAGE_BODY);
    }

    // Карточка считается валидной, если у документа есть непустой title.
    // Для UC-04 этого достаточно, чтобы подтвердить открытие материала.
    boolean hasDigestContent() {
        String title = driver.getTitle();
        return title != null && !title.isBlank();
    }
}
