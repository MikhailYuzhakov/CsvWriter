package org.writer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/**
 * Интерфейс для поддержки записи объектов в файлы.
 * Содержит аннотацию {@code CsvField} для настройки формата CSV.
 */
public interface Writable {
    /**
     * Аннотация для настройки полей при экспорте в CSV.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface CsvField {
        /**
         * Имя столбца в CSV. Если не указано, используется имя поля.
         */
        String name() default "";
        /**
         * Порядок столбца в CSV (меньше значение - раньше в файле).
         */
        int order() default 0;
    }

    /**
     * Записывает список объектов в файл.
     *
     * @param data Список объектов для записи
     * @param fileName Имя целевого файла
     */
    void writeToFile(List<?> data, String fileName);
}
