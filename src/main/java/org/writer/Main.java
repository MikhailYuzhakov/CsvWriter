package org.writer;

import org.writer.model.Months;
import org.writer.model.Person;
import org.writer.model.Student;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        JsonWriter jsonWriter = new JsonWriter();
        CsvWriter  csvWriter = new CsvWriter();

        //сохраняем в JSON данные о полях и методах классов
        jsonWriter.writeToFile("org.writer.model", "classesInfo.json");

        List<Student> students = new ArrayList<>();
        students.add(new Student("John Doe", List.of("30")));
        students.add(new Student("John Bill", List.of("28")));
        students.add(new Student("Will Gun", List.of("28, 28")));

        List<Person> persons = new ArrayList<>();
        persons.add(new Person("Kerry", "Smith", 14, Months.APRIL, 1973));
        persons.add(new Person("John", "Black", 10, Months.AUGUST, 1977));

        //сохраняем в CSV данные из объектов
        csvWriter.writeToFile(students, "students.csv");
        csvWriter.writeToFile(persons, "persons.csv");

    }
}