package org.writer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CsvWriter implements Writable {
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

    static List<Field> getSortedAnnotatedFields(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Writable.CsvField.class))
                .sorted(Comparator.comparingInt(f -> f.getAnnotation(Writable.CsvField.class).order()))
                .collect(Collectors.toList());
    }

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

    static String escapeCsv(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
