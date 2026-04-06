# Lab 2

Интеграционное тестирование системы функций для варианта:

```text
x <= 0 : (((((cos(x) ^ 2) ^ 2) + sin(x)) / cos(x)) - (sec(x) + (cos(x) ^ 2)))
x > 0  : (((((log_10(x) ^ 3) * log_2(x)) * log_2(x)) - ((log_3(x) + ln(x)) + ln(x))) * ln(x))
```

## Что есть в проекте

- Базовые функции:
  - `sin(x)` через ряд
  - `ln(x)` через ряд
- Производные модули:
  - `cos(x)`
  - `sec(x)`
  - `log2(x)`
  - `log3(x)`
  - `log10(x)`
- Система функций:
  - `EquationSystem`
- Табличные заглушки:
  - `src/test/java/ru/itmo/qa/lab2/stub`
- CSV-таблицы для тестов и заглушек:
  - `src/test/resources`
  - `src/test/resources/integration`

## Куда смотреть в коде

- Формула системы: `src/main/java/ru/itmo/qa/lab2/EquationSystem.java`
- Базовые функции:
  - `src/main/java/ru/itmo/qa/lab2/trig/Sine.java`
  - `src/main/java/ru/itmo/qa/lab2/trig/NaturalLogarithm.java`
- Производные функции:
  - `src/main/java/ru/itmo/qa/lab2/trig/Cosine.java`
  - `src/main/java/ru/itmo/qa/lab2/trig/Secant.java`
  - `src/main/java/ru/itmo/qa/lab2/trig/BaseNLogarithm.java`
- CLI и экспорт CSV:
  - `src/main/java/ru/itmo/qa/lab2/Main.java`
  - `src/main/java/ru/itmo/qa/lab2/util/CSVGraphWriter.java`

## Что демонстрировать на защите

Ниже порядок, в котором удобнее всего показывать лабораторную работу.

### 1. Показать архитектуру

Коротко объяснить:

- `sin(x)` и `ln(x)` являются базовыми функциями
- `cos(x)` и `sec(x)` образуют тригонометрическую ветку
- `log2(x)`, `log3(x)`, `log10(x)` строятся через `ln(x)`
- `EquationSystem` собирает обе ветки в одну систему

### 2. Прогнать все тесты

```bash
./gradlew :lab2:test
```

Эта команда проверяет:

- общие правила функций
- модульные тесты
- интеграционные тесты
- табличные заглушки
- экспорт CSV

### 3. Показать покрытие

```bash
./gradlew :lab2:jacocoTestReport
```

После этого открыть отчёт:

- `build/reports/jacoco/test/html/index.html`

### 4. Показать табличные заглушки

Открыть:

- `src/test/java/ru/itmo/qa/lab2/stub/CsvTableStub.java`
- `src/test/java/ru/itmo/qa/lab2/stub`
- `src/test/resources`
- `src/test/resources/integration`

Что говорить:

- каждая заглушка читает значения из CSV
- это позволяет интегрировать систему по одному модулю
- для взаимозависимых точек есть отдельные таблицы, например
  `integration/sineForCosineStub.csv`

### 5. Показать интеграцию по одному модулю

Порядок демонстрации:

1. `sin(x)` как базовая функция
2. `cos(x)` с заглушкой `sin`
3. `sec(x)` с заглушкой `cos`
4. `ln(x)` как базовая функция
5. `log2(x)`, `log3(x)`, `log10(x)` с заглушкой `ln`
6. вся система из заглушек

Команды:

```bash
./gradlew :lab2:test --tests "ru.itmo.qa.lab2.trig.module.SineTest"
./gradlew :lab2:test --tests "ru.itmo.qa.lab2.trig.integration.CosineIntegrationTest"
./gradlew :lab2:test --tests "ru.itmo.qa.lab2.trig.integration.SecantIntegrationTest"
./gradlew :lab2:test --tests "ru.itmo.qa.lab2.trig.module.NaturalLogarithmTest"
./gradlew :lab2:test --tests "ru.itmo.qa.lab2.trig.integration.LogarithmIntegrationTest"
./gradlew :lab2:test --tests "ru.itmo.qa.lab2.function.integration.EquationSystemIntegrationTest"
```

### 6. Показать модульные тесты системы

```bash
./gradlew :lab2:test --tests "ru.itmo.qa.lab2.function.module.EquationSystemTest"
```

Что здесь проверяется:

- `null`
- невалидная точность
- точки разрыва
- корректные значения на характерных точках
- значения по таблице `system.csv`

### 7. Показать CSV-выгрузку любого модуля

Список доступных модулей:

```bash
./gradlew :lab2:run --args="--list-modules"
```

Экспорт всей системы с произвольным шагом:

```bash
./gradlew :lab2:run --args="--module system --from -1 --to 1 --step 0.5 --precision 0.0000001 --output-dir tmp/lab2-demo"
```

Экспорт отдельного модуля:

```bash
./gradlew :lab2:run --args="--module ln --from 0.2 --to 2 --step 0.2 --precision 0.0000001 --output-dir tmp/lab2-ln"
```

Экспорт основного набора модулей по умолчанию:

```bash
./gradlew :lab2:run
```

По умолчанию CSV-файлы попадут в каталог:

- `./csv`

Если указан `--output-dir tmp/lab2-demo`, то CSV-файлы будут сохранены туда.

### 8. Что показать после экспорта

Открыть, например:

- `csv/EquationSystem.csv`
- `csv/Sine.csv`
- `csv/NaturalLogarithm.csv`

или, если запуск был с кастомным каталогом:

- `tmp/lab2-demo/EquationSystem.csv`

Что говорить:

- формат CSV: `x,y`
- шаг задаётся через `--step`
- диапазон задаётся через `--from` и `--to`
- можно выгружать любой модуль отдельно

## Минимальный набор команд для защиты

Если времени мало, достаточно этих команд:

```bash
./gradlew :lab2:test
./gradlew :lab2:jacocoTestReport
./gradlew :lab2:run --args="--list-modules"
./gradlew :lab2:run --args="--module system --from -1 --to 1 --step 0.5 --precision 0.0000001 --output-dir tmp/lab2-demo"
```

## Что говорить рядом с командами

- `:lab2:test`
  - здесь проходят модульные и интеграционные тесты
- `:lab2:jacocoTestReport`
  - здесь показывается покрытие тестами
- `--list-modules`
  - приложение умеет экспортировать любой модуль системы
- `--module system ... --step 0.5`
  - выполняется требование задания про CSV и произвольный шаг изменения `x`

## Полезные замечания

- Табличные заглушки лежат в `src/test/java/ru/itmo/qa/lab2/stub`
- Табличные значения лежат в `src/test/resources`
- Интеграционные таблицы лежат в `src/test/resources/integration`
- Для защиты удобнее сначала показать архитектуру, потом тесты, потом интеграцию, потом CSV-выгрузку

## Полный сценарий в одном блоке

```bash
./gradlew :lab2:test
./gradlew :lab2:jacocoTestReport
./gradlew :lab2:test --tests "ru.itmo.qa.lab2.trig.integration.CosineIntegrationTest"
./gradlew :lab2:test --tests "ru.itmo.qa.lab2.trig.integration.SecantIntegrationTest"
./gradlew :lab2:test --tests "ru.itmo.qa.lab2.trig.integration.LogarithmIntegrationTest"
./gradlew :lab2:test --tests "ru.itmo.qa.lab2.function.integration.EquationSystemIntegrationTest"
./gradlew :lab2:run --args="--list-modules"
./gradlew :lab2:run --args="--module system --from -1 --to 1 --step 0.5 --precision 0.0000001 --output-dir tmp/lab2-demo"
```
