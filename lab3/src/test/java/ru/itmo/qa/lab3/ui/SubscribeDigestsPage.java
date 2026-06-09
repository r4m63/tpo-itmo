package ru.itmo.qa.lab3.ui;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

// Page Object для каталога выпусков (UC-02).
// Структура каталога /digest нестабильна и заметно меняется в зависимости от того,
// какие категории и материалы Subscribe.ru считает актуальными. Поэтому проверка
// выполняется по двум устойчивым признакам: URL содержит /digest и страница
// прошла рендеринг (виден body, есть заголовок документа).
// Сценарий открытия отдельной карточки выпуска (см. SubscribeDigestCardPage) вынесен
// в отдельный поток через SubscribeHomePage.openFirstDigestArticle, потому что
// на главной странице ссылки на статьи появляются стабильнее, чем на каталоге /digest
// с его динамической подгрузкой.
final class SubscribeDigestsPage extends BasePage {
    private static final By PAGE_BODY = By.xpath("//body");

    SubscribeDigestsPage(WebDriver driver, Duration timeout) {
        super(driver, timeout);
    }

    // Сначала ждём, пока URL сменится на /digest (подтверждает, что навигация сработала),
    // затем дожидаемся видимости body как сигнала, что страница отрендерилась.
    void waitUntilLoaded() {
        wait.until(ExpectedConditions.urlContains("/digest"));
        waitForVisible(PAGE_BODY);
    }

    // Минимальная проверка: страница имеет title и URL соответствует разделу выпусков.
    // Этого достаточно для подтверждения UC-02 (доступность каталога), а проверка наличия
    // конкретных карточек уже относится к UC-04 и тестируется отдельно.
    boolean hasDigestsContent() {
        String title = driver.getTitle();
        return title != null && !title.isBlank()
                && driver.getCurrentUrl().contains("/digest");
    }
}
