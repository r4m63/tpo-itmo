package ru.itmo.qa.lab3.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.WebDriver;

@Tag("ui")
class SubscribeRuUiTest {
    private final Lab3UiConfig config = new Lab3UiConfig();

    static Stream<BrowserType> browsers() {
        return BrowserType.configuredBrowsers();
    }

    @ParameterizedTest(name = "[{0}] главная страница содержит основные разделы")
    @MethodSource("browsers")
    @DisplayName("Home page should expose the main public navigation")
    void shouldDisplayHomePageNavigation(BrowserType browserType) {
        withDriver(browserType, driver -> {
            SubscribeHomePage homePage = new SubscribeHomePage(driver, config.timeout(), config.baseUrl());

            homePage.open();

            assertEquals("Отборные выпуски рассылок", homePage.featuredHeaderText());
            assertTrue(homePage.hasMainNavigation());
        });
    }

    @ParameterizedTest(name = "[{0}] каталог групп открывается из шапки")
    @MethodSource("browsers")
    @DisplayName("Groups catalog should open from the main navigation")
    void shouldNavigateToGroupsCatalog(BrowserType browserType) {
        withDriver(browserType, driver -> {
            SubscribeHomePage homePage = new SubscribeHomePage(driver, config.timeout(), config.baseUrl());
            SubscribeGroupsPage groupsPage = new SubscribeGroupsPage(driver, config.timeout());

            homePage.open();
            homePage.openGroupsCatalog();
            groupsPage.waitUntilLoaded();

            assertTrue(driver.getCurrentUrl().contains("/group/"));
            assertTrue(groupsPage.hasGroupsContent());
        });
    }

    @ParameterizedTest(name = "[{0}] каталог выпусков открывается из шапки")
    @MethodSource("browsers")
    @DisplayName("Digests catalog should open from the main navigation")
    void shouldNavigateToDigestsCatalog(BrowserType browserType) {
        withDriver(browserType, driver -> {
            SubscribeHomePage homePage = new SubscribeHomePage(driver, config.timeout(), config.baseUrl());
            SubscribeDigestsPage digestsPage = new SubscribeDigestsPage(driver, config.timeout());

            homePage.open();
            homePage.openDigestsCatalog();
            digestsPage.waitUntilLoaded();

            assertTrue(driver.getCurrentUrl().contains("/digest"));
            assertTrue(digestsPage.hasDigestsContent());
        });
    }

    @ParameterizedTest(name = "[{0}] карточка группы открывается из каталога")
    @MethodSource("browsers")
    @DisplayName("Group card should open from the groups catalog")
    void shouldOpenGroupCard(BrowserType browserType) {
        withDriver(browserType, driver -> {
            SubscribeHomePage homePage = new SubscribeHomePage(driver, config.timeout(), config.baseUrl());
            SubscribeGroupsPage groupsPage = new SubscribeGroupsPage(driver, config.timeout());
            SubscribeGroupCardPage groupCardPage = new SubscribeGroupCardPage(driver, config.timeout());

            homePage.open();
            homePage.openGroupsCatalog();
            groupsPage.waitUntilLoaded();
            groupsPage.openFirstGroup();
            groupCardPage.waitUntilLoaded();

            assertTrue(driver.getCurrentUrl().contains("/group/"));
            assertTrue(groupCardPage.hasGroupTitle());
        });
    }

    @ParameterizedTest(name = "[{0}] карточка выпуска открывается с главной страницы")
    @MethodSource("browsers")
    @DisplayName("Digest card should open from the home page")
    void shouldOpenDigestCard(BrowserType browserType) {
        withDriver(browserType, driver -> {
            SubscribeHomePage homePage = new SubscribeHomePage(driver, config.timeout(), config.baseUrl());
            SubscribeDigestCardPage digestCardPage = new SubscribeDigestCardPage(driver, config.timeout());

            homePage.open();
            homePage.openFirstDigestArticle();
            digestCardPage.waitUntilLoaded();

            assertTrue(driver.getCurrentUrl().contains("/digest/"));
            assertTrue(digestCardPage.hasDigestContent());
        });
    }

    @ParameterizedTest(name = "[{0}] форма регистрации доступна гостю")
    @MethodSource("browsers")
    @DisplayName("Registration page should be reachable for unauthenticated users")
    void shouldOpenRegistrationPage(BrowserType browserType) {
        withDriver(browserType, driver -> {
            SubscribeHomePage homePage = new SubscribeHomePage(driver, config.timeout(), config.baseUrl());

            homePage.open();
            homePage.openRegistrationPage();

            assertTrue(homePage.registrationJourneyStarted());
        });
    }

    @ParameterizedTest(name = "[{0}] подписка требует авторизации")
    @MethodSource("browsers")
    @DisplayName("Subscribe action should open the authorization popup")
    void shouldOpenAuthorizationPopupWhenSubscriptionStarts(BrowserType browserType) {
        withDriver(browserType, driver -> {
            SubscribeHomePage homePage = new SubscribeHomePage(driver, config.timeout(), config.baseUrl());

            homePage.open();
            homePage.startFirstSubscription();

            assertTrue(homePage.subscriptionJourneyStarted());
        });
    }

    private void withDriver(BrowserType browserType, ThrowingWebDriverConsumer assertions) {
        WebDriver driver = WebDriverFactory.create(browserType, config);

        try {
            assertions.accept(driver);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        } finally {
            driver.quit();
        }
    }

    @FunctionalInterface
    private interface ThrowingWebDriverConsumer {
        void accept(WebDriver driver) throws Exception;
    }
}
