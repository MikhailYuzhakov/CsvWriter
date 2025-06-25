package org.writer;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Класс для записи объектов в JSON файл.
 * <p>
 * Пример использования:
 * <pre>
 * {@code
 * List<MyClass> objects = ...;
 * JsonWriter writer = new JsonWriter();
 * writer.writeToFile("org.writer.model", "output.json");
 * }
 * </pre>
 * </p>
 */
public class JsonWriter {

    /**
     * Записывает список объектов в CSV файл. Использует аннотации {@link Writable.CsvField}
     * для определения структуры CSV.
     *
     * @param classPath Путь к директории с экспортируемыми классами.
     * @param outputPath Имя файла для записи результатов
     * @throws IllegalArgumentException Если переданы некорректные параметры
     */
    public void writeToFile(String classPath, String outputPath) {
        try {
            List<Class<?>> classList = getClassesInPackage(classPath);
            StringBuilder json = new StringBuilder();
            json.append("[\n");
            for (Class<?> clazz : classList) {
                json.append("  {\n");
                json.append("    \"classname\": \"")
                        .append(clazz.getSimpleName()).append("\",\n");
                addToJsonClassFields(json, clazz.getDeclaredFields());
                addToJsonClassMethods(json, clazz.getDeclaredMethods());
                json.append("  },\n");
            }
            json.deleteCharAt(json.length()-1);
            json.deleteCharAt(json.length()-1);
            json.append("\n]");
            Path path = Paths.get(outputPath);
            Files.write(path, json.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод добавляет в JSON строку информацию о полях класса.
     * @param json Ссылка на StringBuilder в который будет формироваться JSON строка.
     * @param fields Поля класса.
     */
    void addToJsonClassFields(StringBuilder json, Field[] fields) {
        json.append("    \"fields\": {");
        for (Field field : fields) {
            json.append("\"");
            json.append(getAccessModifier(field.getModifiers())).append("\":\"");
            json.append(field.getName()).append("\", ");
        }
        json.deleteCharAt(json.length()-1);
        json.deleteCharAt(json.length()-1);
        json.append("},\n");
    }

    /**
     * Метод добавляет в JSON строку информацию о методах класса.
     * @param json Ссылка на StringBuilder в который будет формироваться JSON строка.
     * @param methods Методы класса.
     */
    void addToJsonClassMethods(StringBuilder json, Method[] methods) {
        json.append("    \"methods\": {");
        for (Method method : methods) {
            json.append("\"");
            json.append(getAccessModifier(method.getModifiers())).append("\":\"");
            json.append(method.getName()).append("()\", ");
        }
        json.deleteCharAt(json.length()-1);
        json.deleteCharAt(json.length()-1);
        json.append("}\n");
    }

    /**
     * Метод возвращает список всех классов, найденных в заданной директории.
     * @param packageName Путь к Classpath.
     * @return Список классов в директории.
     */
    List<Class<?>> getClassesInPackage(String packageName) throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');

        URL resource = classLoader.getResource(path);
        if (resource == null) return classes;

        File directory = new File(resource.getFile());
        if (directory.exists()) {
            for (File file : Objects.requireNonNull(directory.listFiles())) {
                if (file.getName().endsWith(".class") && !file.getName().contains("$")) {
                    String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                    classes.add(Class.forName(className));
                }
            }
        }
        return classes;
    }

    /**
     * Метод возвращает текстовое представление модификаторов доступа.
     * @param modifiers Модификаторы доступа к целочисленном формате.
     * @return Модификатор доступа в текстовом представлении.
     */
    public String getAccessModifier(int modifiers) {
        if (Modifier.isPrivate(modifiers)) return "private";
        if (Modifier.isProtected(modifiers)) return "protected";
        if (Modifier.isPublic(modifiers)) return "public";
        return "package-private";
    }
}
