# Lab 3

Функциональное тестирование интерфейса `Subscribe.ru` для лабораторной работы №3.

Вариант: `95033`  
Сайт: `https://subscribe.ru/`

Покрытие строится вокруг сценариев гостевого пользователя и использует XPath-локаторы, как требует задание.

## Что лежит в модуле

- `docs/use-case-diagram.puml` - use case-диаграмма
- `docs/checklist.md` - checklist тестового покрытия
- `docs/test-scenarios.md` - описание автоматизированных сценариев
- `docs/test-results.md` - шаблон для фиксации результатов
- `src/test/java/ru/itmo/qa/lab3/ui` - Selenium UI-тесты и вспомогательная инфраструктура

## Важное замечание по стеку

В историческом тексте задания упомянут `Selenium RC`, но в модуле используется современный `Selenium WebDriver` из Selenium 4. Это практическая замена устаревшему RC-подходу: тесты всё так же автоматизируют браузеры Chrome и Firefox, используют XPath и подходят для отчёта по лабораторной.

## Команды

Проверка обычных unit/smoke-тестов:

```bash
./gradlew :lab3:test
```

Запуск UI-тестов сразу для двух браузеров:

```bash
./gradlew :lab3:uiTest
```

Запуск только в Chrome:

```bash
./gradlew :lab3:chromeUiTest
```

Запуск только в Firefox:

```bash
./gradlew :lab3:firefoxUiTest
```

Запуск с видимым браузером:

```bash
./gradlew :lab3:uiTest -Dlab3.headless=false
```

## Параметры запуска

- `-Dlab3.browser=chrome|firefox|all`
- `-Dlab3.headless=true|false`
- `-Dlab3.baseUrl=https://subscribe.ru/`
- `-Dlab3.timeoutSeconds=15`

## Что проверяют автотесты

- загрузку главной страницы и ключевой навигации
- переход в каталог групп
- переход в каталог выпусков
- открытие карточки группы из каталога
- открытие карточки выпуска из каталога
- открытие страницы регистрации
- запуск сценария подписки с появлением окна авторизации

## Источники, на которые опиралась реализация

- `Subscribe.ru`: https://subscribe.ru/
- `Selenium Manager`: https://www.selenium.dev/documentation/selenium_manager/
- `Selenium WebDriver`: https://www.selenium.dev/documentation/webdriver/
