package org.writer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.writer.CsvWriter;
import org.writer.Writable;

import java.util.List;

@Data
@Builder
//@AllArgsConstructor
@NoArgsConstructor
public class Person {
    @Writable.CsvField
    private String firstName;

    @Writable.CsvField
    private String lastName;

    @Writable.CsvField
    private int dayOfBirth;

    @Writable.CsvField
    private Months monthOfBirth;

    @Writable.CsvField
    private int yearOfBirth;

    public Person(String firstName, String lastName, int dayOfBirth, Months monthOfBirth, int yearOfBirth) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dayOfBirth = dayOfBirth;
        this.monthOfBirth = monthOfBirth;
        this.yearOfBirth = yearOfBirth;
    }
}
