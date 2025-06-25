package org.writer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvWriterTest {

    @TempDir
    Path tempDir;

    // Тестовый класс с аннотациями
    static class TestClass {
        @Writable.CsvField(name = "ID", order = 1)
        private int id;

        @Writable.CsvField(name = "Name", order = 2)
        private String name;

        @Writable.CsvField(order = 3) // Без имени - должно использовать имя поля
        private String description;

        public TestClass(int id, String name, String description) {
            this.id = id;
            this.name = name;
            this.description = description;
        }
    }

    @Test
    void writeToFile_ShouldCreateFileWithCorrectContent() throws IOException {
        // Подготовка
        CsvWriter writer = new CsvWriter();
        Path testFile = tempDir.resolve("test.csv");
        List<TestClass> testData = Arrays.asList(
                new TestClass(1, "Test 1", "Description 1"),
                new TestClass(2, "Test 2", "Description, \"with\" quotes")
        );

        // Действие
        writer.writeToFile(testData, testFile.toString());

        // Проверка
        assertTrue(Files.exists(testFile), "Файл должен быть создан");

        try (BufferedReader reader = new BufferedReader(new FileReader(testFile.toFile()))) {
            // Проверяем заголовок
            String header = reader.readLine();
            assertEquals("ID;Name;description", header);

            // Проверяем первую строку данных
            String firstLine = reader.readLine();
            assertEquals("1;Test 1;Description 1", firstLine);

            // Проверяем вторую строку данных (с экранированием)
            String secondLine = reader.readLine();
            assertEquals("2;Test 2;\"Description, \"\"with\"\" quotes\"", secondLine);
        }
    }

    @Test
    void writeToFile_WithEmptyList_ShouldNotCreateFile() throws IOException {
        // Подготовка
        CsvWriter writer = new CsvWriter();
        Path testFile = tempDir.resolve("empty.csv");

        // Действие
        writer.writeToFile(List.of(), testFile.toString());

        // Проверка
        assertFalse(Files.exists(testFile), "Файл не должен быть создан для пустого списка");
    }

    @Test
    void getSortedAnnotatedFields_ShouldReturnFieldsInCorrectOrder() {
        // Действие
        List<Field> fields = CsvWriter.getSortedAnnotatedFields(TestClass.class);

        // Проверка
        assertEquals(3, fields.size());
        assertEquals("id", fields.get(0).getName());
        assertEquals("name", fields.get(1).getName());
        assertEquals("description", fields.get(2).getName());
    }

    @Test
    void escapeCsv_ShouldHandleSpecialCharacters() {
        assertEquals("normal", CsvWriter.escapeCsv("normal"));
        assertEquals("\"contains,comma\"", CsvWriter.escapeCsv("contains,comma"));
        assertEquals("\"contains\"\"quote\"", CsvWriter.escapeCsv("contains\"quote"));
        assertEquals("\"multi\nline\"", CsvWriter.escapeCsv("multi\nline"));
    }

    @Test
    void writeHeader_ShouldUseAnnotationNameOrFieldName() throws IOException {
        // Подготовка
        Path testFile = tempDir.resolve("header.csv");
        BufferedWriter writer = new BufferedWriter(new FileWriter(testFile.toFile()));
        List<Field> fields = CsvWriter.getSortedAnnotatedFields(TestClass.class);

        // Действие
        CsvWriter.writeHeader(writer, fields);
        writer.close();

        // Проверка
        String content = Files.readString(testFile);
        assertEquals("ID;Name;description\r\n", content);
    }

    @Test
    void writeRow_ShouldHandleNullValues() throws IOException {
        // Подготовка
        Path testFile = tempDir.resolve("nulls.csv");
        BufferedWriter writer = new BufferedWriter(new FileWriter(testFile.toFile()));
        List<Field> fields = CsvWriter.getSortedAnnotatedFields(TestClass.class);
        TestClass obj = new TestClass(1, null, null);

        // Действие
        CsvWriter.writeRow(writer, fields, obj);
        writer.close();

        // Проверка
        String content = Files.readString(testFile);
        assertEquals("1;;\r\n", content);
    }
}
