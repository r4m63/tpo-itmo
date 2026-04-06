package ru.itmo.qa.lab2;

import static java.math.RoundingMode.HALF_EVEN;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import ru.itmo.qa.lab2.function.AbstractFunction;
import ru.itmo.qa.lab2.trig.BaseNLogarithm;
import ru.itmo.qa.lab2.trig.Cosecant;
import ru.itmo.qa.lab2.trig.Cosine;
import ru.itmo.qa.lab2.trig.Cotangent;
import ru.itmo.qa.lab2.trig.NaturalLogarithm;
import ru.itmo.qa.lab2.trig.Secant;
import ru.itmo.qa.lab2.trig.Sine;
import ru.itmo.qa.lab2.trig.Tangent;
import ru.itmo.qa.lab2.util.CSVGraphWriter;

/**
 * Схема зависимостей для варианта:
 *
 * | Класс | Зависимости | Формула |
 * |-----------|---------------|---------------|
 * | Sine | — | Ряд Тейлора |
 * | Cosine | Sine | sin(π/2 - x) |
 * | Secant | Cosine | 1/cos(x) |
 * | NaturalLogarithm | — | Ряд |
 * | BaseNLogarithm | NaturalLogarithm | ln(x) / ln(base) |
 * | EquationSystem | sin, cos, sec, ln, log2, log3, log10 | Система функций
 * варианта |
 */
public class Main {
    private static final Path PROJECT_ROOT = Path.of(System.getProperty("user.dir"));
    private static final Map<String, Supplier<AbstractFunction>> MODULES = createModules();

    private static String outputDir = normalizeOutputDir("csv");

    private static final BigDecimal PRECISION = new BigDecimal("0.0000001");
    private static final BigDecimal POSITIVE_END = new BigDecimal(10).setScale(7, HALF_EVEN);
    private static final BigDecimal NEGATIVE_END = POSITIVE_END.negate();
    private static final BigDecimal STEP = new BigDecimal("0.01");

    public static void main(String[] args) {
        try {
            final CliOptions options = CliOptions.parse(args);

            if (options.listModules()) {
                printAvailableModules();
                return;
            }

            if (options.moduleName() == null) {
                generateFunctionData(options.from(), options.to(), options.step(), options.precision());
            } else {
                final AbstractFunction function = resolveModule(options.moduleName());
                exportFunction(function, options.from(), options.to(), options.step(), options.precision());
            }
        } catch (IOException e) {
            System.err.println("Ошибка при работе с файлами: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка параметров запуска: " + e.getMessage());
        }
    }

    public static void setOutputDir(String path) {
        outputDir = normalizeOutputDir(path);
    }

    private static void generateFunctionData(BigDecimal from, BigDecimal to, BigDecimal step, BigDecimal precision)
            throws IOException {
        for (String moduleName : List.of("sin", "cos", "sec", "ln", "log2", "log3", "log10", "system")) {
            exportFunction(resolveModule(moduleName), from, to, step, precision);
        }
    }

    private static void exportFunction(
            AbstractFunction function,
            BigDecimal from,
            BigDecimal to,
            BigDecimal step,
            BigDecimal precision) throws IOException {
        new CSVGraphWriter(function, outputDir).write(from, to, step, precision);
    }

    private static AbstractFunction resolveModule(String moduleName) {
        final Supplier<AbstractFunction> supplier = MODULES.get(moduleName.toLowerCase());
        if (supplier == null) {
            throw new IllegalArgumentException("Неизвестный модуль: " + moduleName);
        }
        return supplier.get();
    }

    private static void printAvailableModules() {
        System.out.println("Доступные модули:");
        for (String moduleName : MODULES.keySet()) {
            System.out.println(moduleName);
        }
        System.out.println("Пример запуска:");
        System.out.println(
                "--module system --from -5 --to 5 --step 0.1 --precision 0.0000001 --output-dir tmp/lab2-demo");
    }

    private static String normalizeOutputDir(String path) {
        Objects.requireNonNull(path, "Путь до каталога вывода не должен быть null");

        final Path resolvedPath;
        if (path.equals("/tmp") || path.startsWith("/tmp/")) {
            resolvedPath = PROJECT_ROOT.resolve(path.substring(1)).normalize();
        } else {
            final Path candidate = Path.of(path);
            resolvedPath = candidate.isAbsolute() ? candidate.normalize() : PROJECT_ROOT.resolve(candidate).normalize();
        }

        final String normalized = resolvedPath.toString();
        return normalized.endsWith(File.separator) ? normalized : normalized + File.separator;
    }

    private static Map<String, Supplier<AbstractFunction>> createModules() {
        Map<String, Supplier<AbstractFunction>> modules = new LinkedHashMap<>();
        modules.put("sin", Sine::new);
        modules.put("cos", Cosine::new);
        modules.put("sec", Secant::new);
        modules.put("csc", Cosecant::new);
        modules.put("tan", Tangent::new);
        modules.put("cot", Cotangent::new);
        modules.put("ln", NaturalLogarithm::new);
        modules.put("log2", () -> new BaseNLogarithm(2));
        modules.put("log3", () -> new BaseNLogarithm(3));
        modules.put("log10", () -> new BaseNLogarithm(10));
        modules.put("system", EquationSystem::new);
        return modules;
    }

    private record CliOptions(
            String moduleName,
            BigDecimal from,
            BigDecimal to,
            BigDecimal step,
            BigDecimal precision,
            boolean listModules) {
        private static CliOptions parse(String[] args) {
            String moduleName = null;
            BigDecimal from = NEGATIVE_END;
            BigDecimal to = POSITIVE_END;
            BigDecimal step = STEP;
            BigDecimal precision = PRECISION;
            boolean listModules = false;

            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "--module" -> moduleName = requireValue(args, ++i, "--module");
                    case "--from" -> from = new BigDecimal(requireValue(args, ++i, "--from"));
                    case "--to" -> to = new BigDecimal(requireValue(args, ++i, "--to"));
                    case "--step" -> step = new BigDecimal(requireValue(args, ++i, "--step"));
                    case "--precision" -> precision = new BigDecimal(requireValue(args, ++i, "--precision"));
                    case "--output-dir" -> setOutputDir(requireValue(args, ++i, "--output-dir"));
                    case "--list-modules" -> listModules = true;
                    default -> throw new IllegalArgumentException("Неизвестный аргумент: " + args[i]);
                }
            }

            return new CliOptions(moduleName, from, to, step, precision, listModules);
        }

        private static String requireValue(String[] args, int index, String optionName) {
            if (index >= args.length) {
                throw new IllegalArgumentException("Для опции " + optionName + " нужно указать значение");
            }
            return args[index];
        }
    }
}
