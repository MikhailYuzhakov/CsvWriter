package org.writer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс для записи объектов в CSV файл с использованием аннотаций.
 * Реализует интерфейс {@link Writable} для поддержки записи данных.
 * <p>
 * Пример использования:
 * <pre>
 * {@code
 * List<MyClass> objects = ...;
 * CsvWriter writer = new CsvWriter();
 * writer.writeToFile(objects, "output.csv");
 * }
 * </pre>
 * </p>
 *
 * @see Writable
 * @see Writable.CsvField
 */
public class CsvWriter implements Writable {

    /**
     * Записывает список объектов в CSV файл. Использует аннотации {@link Writable.CsvField}
     * для определения структуры CSV.
     *
     * @param objects Список объектов для записи. Если null или пуст, метод завершается без записи
     * @param filename Имя файла для записи результатов
     * @throws IllegalArgumentException Если переданы некорректные параметры
     */
    public void writeToFile(List<?> objects, String filename) {
        if (objects == null || objects.isEmpty()) {
            return;
        }

        Class<?> clazz = objects.get(0).getClass();
        List<Field> fields = getSortedAnnotatedFields(clazz);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Записываем заголовок
            writeHeader(writer, fields);

            // Записываем данные
            for (Object obj : objects) {
                writeRow(writer, fields, obj);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Получает аннотированные поля класса, отсортированные по порядку (order) из аннотации.
     *
     * @param clazz Класс для анализа
     * @return Список полей с аннотацией {@link Writable.CsvField}, отсортированный по order
     */
    static List<Field> getSortedAnnotatedFields(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Writable.CsvField.class))
                .sorted(Comparator.comparingInt(f -> f.getAnnotation(Writable.CsvField.class).order()))
                .collect(Collectors.toList());
    }


    /**
     * Записывает заголовок CSV на основе аннотаций полей.
     *
     * @param writer BufferedWriter для записи
     * @param fields Список полей для включения в заголовок
     * @throws IOException Если произошла ошибка ввода-вывода
     */
    static void writeHeader(BufferedWriter writer, List<Field> fields) throws IOException {
        List<String> headers = fields.stream()
                .map(f -> {
                    Writable.CsvField annotation = f.getAnnotation(Writable.CsvField.class);
                    return annotation.name().isEmpty() ? f.getName() : annotation.name();
                })
                .collect(Collectors.toList());

        writer.write(String.join(";", headers));
        writer.newLine();
    }

    /**
     * Записывает строку данных в CSV.
     *
     * @param writer BufferedWriter для записи
     * @param fields Список полей для включения в строку
     * @param obj Объект, данные которого нужно записать
     * @throws IOException Если произошла ошибка ввода-вывода
     */
    static void writeRow(BufferedWriter writer, List<Field> fields, Object obj) throws IOException {
        List<String> values = fields.stream()
                .map(f -> {
                    try {
                        f.setAccessible(true);
                        Object value = f.get(obj);
                        return value != null ? escapeCsv(value.toString()) : "";
                    } catch (IllegalAccessException e) {
                        return "";
                    }
                })
                .collect(Collectors.toList());

        writer.write(String.join(";", values));
        writer.newLine();
    }

    /**
     * Экранирует значения для корректного формата CSV.
     *
     * @param value Значение для экранирования
     * @return Экранированная строка, готовая для записи в CSV
     */
    static String escapeCsv(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
