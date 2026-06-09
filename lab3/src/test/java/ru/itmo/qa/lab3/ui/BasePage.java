package ru.itmo.qa.lab3.ui;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

// Базовый класс Page Object'ов.
// Реализует Page Object Model: каждой странице сайта соответствует свой класс,
// наследник BasePage, который инкапсулирует XPath-локаторы и действия. Тесты не работают
// с DOM напрямую, а вызывают методы Page Object'ов, что упрощает поддержку при изменениях
// вёрстки сайта.
// Здесь собраны общие операции: явные ожидания через WebDriverWait, безопасные клики,
// проверки видимости. Реализация полагается только на explicit waits, неявных ожиданий
// (implicit waits) нет специально, чтобы избежать смешения двух стратегий, которое
// приводит к непредсказуемым задержкам.
abstract class BasePage {
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    BasePage(WebDriver driver, Duration timeout) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, timeout);
    }

    // Ждёт появления элемента в DOM и его видимости пользователю.
    // Используется для проверки, что страница отрендерила нужный элемент.
    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    // Ждёт первый видимый элемент из списка совпадений.
    // Полезно, когда XPath матчит несколько элементов (например, дублирующиеся ссылки
    // в десктоп- и мобильной навигации), а кликнуть нужно по тому, что реально виден.
    protected WebElement waitForFirstDisplayed(By locator) {
        return wait.until(driver -> {
            List<WebElement> elements = driver.findElements(locator);
            return elements.stream().filter(WebElement::isDisplayed).findFirst().orElse(null);
        });
    }

    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void click(By locator) {
        waitForClickable(locator).click();
    }

    // Клик через JavaScript для случаев, когда обычный click перехватывается
    // другим элементом (всплывающий баннер, фиксированная шапка, оверлей).
    // В Firefox такая ситуация встречается чаще, чем в Chrome, поэтому метод
    // используется в навигации и кликах по ссылкам в публичной части сайта.
    // Сначала прокручиваем элемент в центр viewport через scrollIntoView,
    // потом синтезируем клик через element.click() в JavaScript, минуя проверку
    // перекрытия другими элементами, которую делает обычный WebDriver-клик.
    protected void jsClick(By locator) {
        WebElement element = waitForVisible(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    protected String visibleText(By locator) {
        return waitForVisible(locator).getText().trim();
    }

    // Не бросает исключение при отсутствии элемента, в отличие от waitForVisible.
    // Применяется в проверках вроде форма авторизации появилась ИЛИ форма регистрации
    // появилась, где нужно опросить несколько локаторов и получить булевый результат.
    protected boolean isVisible(By locator) {
        try {
            waitForVisible(locator);
            return true;
        } catch (TimeoutException ignored) {
            return false;
        }
    }
}
