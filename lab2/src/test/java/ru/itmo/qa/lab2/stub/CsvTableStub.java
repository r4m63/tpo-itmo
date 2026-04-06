package ru.itmo.qa.lab2.stub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import ru.itmo.qa.lab2.function.AbstractFunction;

// Базовая табличная заглушка для интеграционных тестов:
// загружает эталонные пары x,y из CSV и подменяет реальное вычисление
// возвратом заранее подготовленного значения.
public class CsvTableStub extends AbstractFunction {
    private final String resourcePath;
    private final Map<String, BigDecimal> values = new HashMap<>();

    // Сохраняет путь к CSV-ресурсу и сразу подготавливает внутреннюю
    // таблицу значений, чтобы заглушка была готова к использованию.
    public CsvTableStub(final String resourcePath) {
        this.resourcePath = resourcePath;
        loadValues();
    }

    @Override
    // Повторно использует базовую валидацию AbstractFunction, ищет точное
    // табличное значение по нормализованному x и приводит результат к шкале
    // requested precision, чтобы интерфейс совпадал с реальными функциями.
    public BigDecimal calculate(final BigDecimal x, final BigDecimal precision) {
        isValid(x, precision);

        final BigDecimal value = values.get(normalize(x));
        if (value == null) {
            throw new ArithmeticException("В табличной заглушке нет значения при x = " + x);
        }
        return value.setScale(precision.scale(), java.math.RoundingMode.HALF_EVEN);
    }

    @Override
    // Возвращает техническое имя заглушки, из которого видно, из какого
    // CSV-ресурса она подгружает эталонные данные.
    public String getName() {
        return "Stub_" + resourcePath.replace('/', '_');
    }

    // Читает CSV-ресурс построчно, пропускает заголовок и пустые/битые
    // строки, после чего сохраняет пары x,y в map для быстрых lookup-ов.
    private void loadValues() {
        final InputStream stream = getClass().getResourceAsStream(resourcePath);
        if (stream == null) {
            throw new IllegalArgumentException("Ресурс не найден: " + resourcePath);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                final String[] parts = line.split(",");
                if (parts.length < 2) {
                    continue;
                }

                final BigDecimal x = new BigDecimal(parts[0].trim());
                final BigDecimal y = new BigDecimal(parts[1].trim());
                values.put(normalize(x), y);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось прочитать ресурс: " + resourcePath, e);
        }
    }

    // Нормализует BigDecimal к стабильному строковому ключу, чтобы значения
    // вида 1, 1.0 и 1.00 попадали в одну и ту же запись таблицы.
    private String normalize(final BigDecimal value) {
        return value.stripTrailingZeros().toPlainString();
    }
}
