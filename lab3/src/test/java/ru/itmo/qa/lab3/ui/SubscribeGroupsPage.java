package ru.itmo.qa.lab3.ui;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

// Page Object для каталога групп (UC-03).
// На странице /group/ карточки групп оформлены как заголовки h2 со ссылкой внутри.
// XPath (//h2/a)[1] находит первую такую ссылку, что используется и для
// подтверждения загрузки каталога, и для перехода в карточку конкретной группы (UC-05).
final class SubscribeGroupsPage extends BasePage {
    private static final By FIRST_GROUP_CARD = By.xpath("(//h2/a)[1]");

    SubscribeGroupsPage(WebDriver driver, Duration timeout) {
        super(driver, timeout);
    }

    // Сигнал готовности страницы: появилась хотя бы одна карточка группы.
    void waitUntilLoaded() {
        waitForVisible(FIRST_GROUP_CARD);
    }

    boolean hasGroupsContent() {
        return waitForVisible(FIRST_GROUP_CARD).isDisplayed();
    }

    // Переход в карточку первой группы каталога, используется в TS-05.
    void openFirstGroup() {
        click(FIRST_GROUP_CARD);
    }
}
