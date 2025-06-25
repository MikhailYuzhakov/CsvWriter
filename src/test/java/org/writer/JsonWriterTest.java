package org.writer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonWriterTest {

    @TempDir
    Path tempDir;

    @Test
    void testGetAccessModifier() {
        JsonWriter writer = new JsonWriter();

        assertEquals("public", writer.getAccessModifier(Modifier.PUBLIC));
        assertEquals("private", writer.getAccessModifier(Modifier.PRIVATE));
        assertEquals("protected", writer.getAccessModifier(Modifier.PROTECTED));
        assertEquals("package-private", writer.getAccessModifier(0));
    }

    @Test
    void testAddToJsonClassFields() throws NoSuchFieldException {
        JsonWriter writer = new JsonWriter();
        StringBuilder json = new StringBuilder();

        Field[] fields = TestClass.class.getDeclaredFields();
        writer.addToJsonClassFields(json, fields);

        String expected = "    \"fields\": {\"private\":\"privateField\", \"public\":\"publicField\"},\n";
        assertEquals(expected, json.toString());
    }

    @Test
    void testAddToJsonClassMethods() throws NoSuchMethodException {
        JsonWriter writer = new JsonWriter();
        StringBuilder json = new StringBuilder();

        Method[] methods = TestClass.class.getDeclaredMethods();
        writer.addToJsonClassMethods(json, methods);

        String expected = "    \"methods\": {\"public\":\"publicMethod()\"}\n";
        assertEquals(expected, json.toString());
    }

    @Test
    void testWriteToFile() throws IOException {
        JsonWriter writer = new JsonWriter();
        Path testFile = tempDir.resolve("test.json");

        writer.writeToFile("org.writer.testdata", testFile.toString());

        String content = Files.readString(testFile);

        assertTrue(content.contains("\"classname\": \"TestClass\""));
        assertTrue(content.contains("\"private\":\"privateField\""));
        assertTrue(content.contains("\"public\":\"publicMethod()\""));
    }

    @Test
    void testGetClassesInPackage() throws Exception {
        JsonWriter writer = new JsonWriter();

        List<Class<?>> classes = writer.getClassesInPackage("org.writer.testdata");

        assertEquals(1, classes.size());
        assertEquals("TestClass", classes.get(0).getSimpleName());
    }

    // Тестовый класс для проверки
    public static class TestClass {
        private String privateField;
        public int publicField;

        public void publicMethod() {}
    }
}
