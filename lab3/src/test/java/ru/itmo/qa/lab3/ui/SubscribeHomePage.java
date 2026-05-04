package ru.itmo.qa.lab3.ui;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

final class SubscribeHomePage extends BasePage {
    private static final By FEATURED_HEADER = By.xpath("//h1[normalize-space()='Отборные выпуски рассылок']");
    private static final By DIGESTS_LINK = By.xpath("(//a[contains(@href,'/digest') and normalize-space()='Выпуски'])[1]");
    private static final By GROUPS_LINK = By.xpath("(//a[@href='/group/' and normalize-space()='Группы'])[1]");
    private static final By COLLECTIONS_LINK = By.xpath("//a[contains(normalize-space(),'Подборки')]");
    private static final By REGISTRATION_LINK = By.xpath("//a[normalize-space()='Регистрация']");
    private static final By FIRST_DIGEST_ARTICLE = By.xpath(
            "(//a[contains(@href,'/digest/') and contains(@href,'.html')])[1]");
    private static final By FIRST_SUBSCRIBE_BUTTON = By.xpath("//a[normalize-space()='Подписаться']");
    private static final By LOGIN_EMAIL_FIELD = By.xpath("//input[@id='credential_0']");
    private static final By LOGIN_PASSWORD_FIELD = By.xpath("//input[@id='credential_1']");
    private static final By REGISTRATION_EMAIL_FIELD = By.xpath("//input[@id='arfemail']");
    private static final By POPUP_REGISTRATION_TAB = By.xpath("//li[@id='js_tab_reg']//a[contains(normalize-space(),'Регистрация')]");

    private final String baseUrl;

    SubscribeHomePage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout);
        this.baseUrl = baseUrl;
    }

    void open() {
        driver.get(baseUrl);
        waitForVisible(FEATURED_HEADER);
    }

    String featuredHeaderText() {
        return visibleText(FEATURED_HEADER);
    }

    boolean hasMainNavigation() {
        return waitForVisible(DIGESTS_LINK).isDisplayed()
                && waitForVisible(GROUPS_LINK).isDisplayed()
                && waitForVisible(COLLECTIONS_LINK).isDisplayed();
    }

    void openDigestsCatalog() {
        jsClick(DIGESTS_LINK);
    }

    void openFirstDigestArticle() {
        WebElement link = waitForFirstDisplayed(FIRST_DIGEST_ARTICLE);
        String href = link.getAttribute("href");
        link.click();

        if (href != null && !href.isBlank() && !driver.getCurrentUrl().contains(".html")) {
            driver.get(href);
        }
    }

    void openGroupsCatalog() {
        click(GROUPS_LINK);
    }

    void openRegistrationPage() {
        WebElement registrationLink = waitForFirstDisplayed(REGISTRATION_LINK);
        String href = registrationLink.getAttribute("href");
        registrationLink.click();

        if (!registrationGateIsVisible() && href != null && !href.isBlank() && !href.equals("#")) {
            driver.get(href);
        }
    }

    void startFirstSubscription() {
        WebElement subscribeLink = waitForFirstDisplayed(FIRST_SUBSCRIBE_BUTTON);
        String href = subscribeLink.getAttribute("href");
        subscribeLink.click();

        if (!subscriptionGateIsVisible() && href != null && !href.isBlank() && !href.equals("#")) {
            driver.get(href);
        }
    }

    boolean registrationGateIsVisible() {
        return isVisible(REGISTRATION_EMAIL_FIELD) || isVisible(POPUP_REGISTRATION_TAB);
    }

    boolean registrationJourneyStarted() {
        return registrationGateIsVisible() || driver.getCurrentUrl().contains("/member/join");
    }

    boolean subscriptionGateIsVisible() {
        return (isVisible(LOGIN_EMAIL_FIELD) && isVisible(LOGIN_PASSWORD_FIELD))
                || isVisible(REGISTRATION_EMAIL_FIELD)
                || isVisible(POPUP_REGISTRATION_TAB);
    }

    boolean subscriptionJourneyStarted() {
        return subscriptionGateIsVisible() || driver.getCurrentUrl().contains("/member/quick");
    }
}
