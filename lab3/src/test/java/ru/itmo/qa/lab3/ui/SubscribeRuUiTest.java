package ru.itmo.qa.lab3.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.WebDriver;

// UI-тесты для сайта Subscribe.ru, лабораторная работа №3, вариант 95033.
// Каждый тест помечен @Tag("ui"), что позволяет Gradle разделять обычные unit-тесты
// (test) и UI-тесты (uiTest, chromeUiTest, firefoxUiTest).
// Все тесты параметризованы по BrowserType: при lab3.browser=all каждый сценарий
// автоматически запускается дважды, в Chrome и Firefox. Это даёт кроссбраузерное
// покрытие без дублирования кода тестов.
// Каждый тест отвечает за один из тестовых сценариев TS-01..TS-07 и косвенно покрывает
// прецеденты UC-01..UC-09 из use case-диаграммы.
@Tag("ui")
class SubscribeRuUiTest {
    private final Lab3UiConfig config = new Lab3UiConfig();

    // Источник параметров для @ParameterizedTest. Перечень браузеров определяется
    // через системное свойство lab3.browser (см. BrowserType.configuredBrowsers).
    static Stream<BrowserType> browsers() {
        return BrowserType.configuredBrowsers();
    }

    // TS-01, UC-01: проверка доступности главной страницы.
    // Тест подтверждает, что главная страница рендерит заголовок "Отборные выпуски
    // рассылок"
    // и в шапке доступны три ключевых раздела навигации.
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

    // TS-02, UC-03: переход в каталог групп из главной навигации.
    // После клика по ссылке Группы URL должен содержать /group/, а на странице
    // должна
    // отрендериться хотя бы одна карточка группы.
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

    // TS-07, UC-02: переход в каталог выпусков из главной навигации.
    // Подтверждает, что после клика по ссылке Выпуски URL соответствует разделу
    // /digest
    // и страница успешно загружена.
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

    // TS-05, UC-05: открытие карточки конкретной группы из каталога.
    // Сценарий: главная > каталог групп > клик по первой карточке > проверка URL и
    // заголовка.
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

    // TS-06, UC-04: открытие карточки выпуска (статьи) с главной страницы.
    // Карточка выпуска открывается напрямую с главной, потому что там ссылки на
    // конкретные статьи (вида /digest/.../n[id].html) появляются стабильнее, чем на
    // каталоге /digest, где много динамической подгрузки.
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

    // TS-03, UC-06: запуск регистрационного потока для гостя.
    // Тест проверяет, что после клика по ссылке Регистрация на главной странице
    // пользователь попадает в регистрационный интерфейс (отдельная страница или
    // popup).
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

    // TS-04, UC-08 + UC-09: подписка требует авторизации.
    // Негативно-позитивный сценарий: гость нажимает Подписаться, и сайт не должен
    // оформить подписку напрямую, вместо этого должна открыться форма авторизации
    // или регистрации (UC-09 расширяется через UC-06 и UC-07).
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

    // Управляет жизненным циклом WebDriver для каждого теста: создание, выполнение
    // ассертов, гарантированное закрытие через finally. Каждый тест получает свой
    // изолированный экземпляр драйвера, чтобы состояние одного теста не влияло на
    // другой.
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

    // Функциональный интерфейс, аналог Consumer<WebDriver>, но с поддержкой
    // проверяемых
    // исключений. Это нужно, потому что внутри лямбды могут вызываться методы
    // Selenium, потенциально бросающие checked-исключения.
    @FunctionalInterface
    private interface ThrowingWebDriverConsumer {
        void accept(WebDriver driver) throws Exception;
    }
}
