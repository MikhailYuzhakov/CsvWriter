package org.writer.model;

import lombok.*;
import org.writer.CsvWriter;
import org.writer.Writable;

import java.util.List;

@Data
@Builder
//@AllArgsConstructor
@NoArgsConstructor
public class Student {
    @Writable.CsvField
    private String name;

    @Writable.CsvField
    private List<String> score;

    public Student(String name, List<String> score) {
        this.name = name;
        this.score = score;
    }
}