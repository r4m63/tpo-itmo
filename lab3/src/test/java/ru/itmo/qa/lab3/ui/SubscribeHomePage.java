package ru.itmo.qa.lab3.ui;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

// Page Object для главной страницы Subscribe.ru.
// Содержит XPath-локаторы и методы для UC-01 (просмотр главной), UC-02 (переход в выпуски),
// UC-03 (переход в группы), UC-04 (открытие карточки выпуска с главной), UC-06 (старт регистрации),
// UC-08 + UC-09 (старт подписки и требование авторизации).
// Все локаторы построены на XPath, как требует задание из-за динамической генерации DOM
// на сайте. Привязка идёт к стабильным признакам, текст ссылки или частичное совпадение href.
final class SubscribeHomePage extends BasePage {
    // h1 главной страницы, маркер успешной загрузки.
    private static final By FEATURED_HEADER = By.xpath("//h1[normalize-space()='Отборные выпуски рассылок']");

    // Ссылка на каталог выпусков. Обёртка в (...)[1] нужна, потому что одинаковый текст
    // Выпуски встречается в шапке и в футере, кликаем по первому совпадению.
    private static final By DIGESTS_LINK = By.xpath("(//a[contains(@href,'/digest') and normalize-space()='Выпуски'])[1]");

    // Ссылка на каталог групп. Точная привязка к href исключает ложные совпадения.
    private static final By GROUPS_LINK = By.xpath("(//a[@href='/group/' and normalize-space()='Группы'])[1]");

    private static final By COLLECTIONS_LINK = By.xpath("//a[contains(normalize-space(),'Подборки')]");
    private static final By REGISTRATION_LINK = By.xpath("//a[normalize-space()='Регистрация']");

    // Первая ссылка на отдельный выпуск (статью) на главной странице.
    // Условие contains(@href,'.html') отфильтровывает ссылки на категории
    // (вида /digest/economics/), оставляя только ссылки на статьи
    // (вида /digest/economics/kris/n692492909.html).
    private static final By FIRST_DIGEST_ARTICLE = By.xpath(
            "(//a[contains(@href,'/digest/') and contains(@href,'.html')])[1]");

    // Кнопка Подписаться на первой карточке выпуска.
    private static final By FIRST_SUBSCRIBE_BUTTON = By.xpath("//a[normalize-space()='Подписаться']");

    // Маркеры формы авторизации (используются для проверки UC-09).
    // ID credential_0/credential_1 это автогенерация фреймворка сайта, но они стабильны
    // в рамках публичной формы логина.
    private static final By LOGIN_EMAIL_FIELD = By.xpath("//input[@id='credential_0']");
    private static final By LOGIN_PASSWORD_FIELD = By.xpath("//input[@id='credential_1']");

    // Маркеры формы регистрации.
    private static final By REGISTRATION_EMAIL_FIELD = By.xpath("//input[@id='arfemail']");
    private static final By POPUP_REGISTRATION_TAB = By.xpath("//li[@id='js_tab_reg']//a[contains(normalize-space(),'Регистрация')]");

    private final String baseUrl;

    SubscribeHomePage(WebDriver driver, Duration timeout, String baseUrl) {
        super(driver, timeout);
        this.baseUrl = baseUrl;
    }

    // Открывает главную страницу и ждёт появления заголовка как сигнала готовности DOM.
    void open() {
        driver.get(baseUrl);
        waitForVisible(FEATURED_HEADER);
    }

    String featuredHeaderText() {
        return visibleText(FEATURED_HEADER);
    }

    // Проверяет, что в шапке доступны три ключевые секции (Выпуски, Группы, Подборки).
    // Используется в TS-01 для подтверждения корректной отрисовки навигации.
    boolean hasMainNavigation() {
        return waitForVisible(DIGESTS_LINK).isDisplayed()
                && waitForVisible(GROUPS_LINK).isDisplayed()
                && waitForVisible(COLLECTIONS_LINK).isDisplayed();
    }

    // Переход в каталог выпусков. Используется jsClick, потому что обычный клик в Firefox
    // иногда перехватывается оверлеем cookie-баннера.
    void openDigestsCatalog() {
        jsClick(DIGESTS_LINK);
    }

    // Переход к карточке отдельного выпуска с главной страницы.
    // Сначала пробуем обычный клик по ссылке. Если по какой-то причине браузер не успел
    // перейти на страницу статьи (URL не содержит .html), делаем явную навигацию через
    // driver.get(href). Это страховка от перехвата клика всплывающими элементами.
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

    // Открывает регистрационный поток.
    // Сайт может реагировать двояко: открыть отдельную страницу /member/join или показать
    // popup с вкладкой регистрации. После клика проверяем, открылся ли регистрационный гейт,
    // если нет, форсируем переход через href.
    void openRegistrationPage() {
        WebElement registrationLink = waitForFirstDisplayed(REGISTRATION_LINK);
        String href = registrationLink.getAttribute("href");
        registrationLink.click();

        if (!registrationGateIsVisible() && href != null && !href.isBlank() && !href.equals("#")) {
            driver.get(href);
        }
    }

    // Запускает поток подписки: ищет первую кнопку Подписаться и кликает её.
    // Поведение сайта вариативно: для гостя клик может открыть popup с вкладкой логина,
    // popup с вкладкой регистрации или перенаправить на /member/quick. Тест считает успех,
    // если сработал любой из этих сценариев (см. subscriptionJourneyStarted).
    void startFirstSubscription() {
        WebElement subscribeLink = waitForFirstDisplayed(FIRST_SUBSCRIBE_BUTTON);
        String href = subscribeLink.getAttribute("href");
        subscribeLink.click();

        if (!subscriptionGateIsVisible() && href != null && !href.isBlank() && !href.equals("#")) {
            driver.get(href);
        }
    }

    // Проверка, что отрисовался один из вариантов формы регистрации
    // (отдельная страница или popup-вкладка).
    boolean registrationGateIsVisible() {
        return isVisible(REGISTRATION_EMAIL_FIELD) || isVisible(POPUP_REGISTRATION_TAB);
    }

    // Считает регистрационный поток запущенным, если виден регистрационный гейт ИЛИ
    // URL соответствует маршруту регистрации.
    // Используется как assertTrue в тесте shouldOpenRegistrationPage.
    boolean registrationJourneyStarted() {
        return registrationGateIsVisible() || driver.getCurrentUrl().contains("/member/join");
    }

    // Проверка, что появилась форма авторизации (email + password) ИЛИ форма регистрации.
    // Соответствует требованию UC-09: подписка требует входа или регистрации.
    boolean subscriptionGateIsVisible() {
        return (isVisible(LOGIN_EMAIL_FIELD) && isVisible(LOGIN_PASSWORD_FIELD))
                || isVisible(REGISTRATION_EMAIL_FIELD)
                || isVisible(POPUP_REGISTRATION_TAB);
    }

    // Поток подписки считается запущенным, если показан гейт авторизации/регистрации
    // ИЛИ пользователь перенаправлен на маршрут /member/quick (быстрая подписка).
    boolean subscriptionJourneyStarted() {
        return subscriptionGateIsVisible() || driver.getCurrentUrl().contains("/member/quick");
    }
}
