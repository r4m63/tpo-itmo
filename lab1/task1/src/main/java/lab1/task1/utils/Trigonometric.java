package lab1.task1.utils;

public class Trigonometric {

    public static double arccos(double x) {
        // По умолчанию достаточно большое количество итераций для сходимости
        return arccos(x, 10000);
    }

    /**
     * Реализация разложения arccos(x) через ряд Тейлора для arcsin.
     * arccos(x) = PI/2 - arcsin(x)
     *
     * @param x аргумент функции (должен быть в диапазоне [-1, 1])
     * @param n максимальное количество членов ряда для вычисления
     * @return значение arccos(x)
     */
    public static double arccos(double x, int n) {
        if (Math.abs(x) > 1) {
            return Double.NaN;
        }

        // arccos(1) = 0
        if (x == 1.0) {
            return 0.0;
        }
        // arccos(-1) = PI
        if (x == -1.0) {
            return Math.PI;
        }
        // arccos(0) = PI/2
        if (x == 0.0) {
            return Math.PI / 2;
        }

        // Оптимизация сходимости около границ:
        // Если x близок к 1 или -1, ряд Тейлора сходится очень медленно.
        // Используем свойство: arccos(x) = arcsin(sqrt(1 - x^2)) для x > 0
        // Для отрицательных x: arccos(x) = PI - arcsin(sqrt(1 - x^2))
        if (Math.abs(x) > 0.9) {
            double transformed = Math.sqrt(1 - x * x);
            double arcsinVal = calculateArcsinSeries(transformed, n);
            return (x > 0) ? arcsinVal : Math.PI - arcsinVal;
        }

        // Стандартный случай
        double arcsinVal = calculateArcsinSeries(x, n);
        return (Math.PI / 2) - arcsinVal;
    }

    /**
     * Вспомогательный метод для расчета ряда arcsin(x)
     */
    private static double calculateArcsinSeries(double x, int n) {
        double res = x;
        double term = x;

        for (int i = 0; i < n; i++) {
            double prev = res;

            // Формула следующего члена ряда:
            // term_{i+1} = term_i * x^2 * (2i+1)^2 / ((2i+2)*(2i+3))
            term *= x * x * (2 * i + 1) * (2 * i + 1);
            term /= (2.0 * i + 2.0) * (2.0 * i + 3.0);

            res += term;

            // Проверка сходимости (если изменение меньше машинного эпсилон)
            if (Math.abs(res - prev) < 1e-10)
                break;
        }
        return res;
    }
}
