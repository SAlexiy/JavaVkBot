package org.example;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.example.model.User;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CSV {
    public static ArrayList<String> readCSV(String path){
        List<String> strings = null;
        File file = new File(path);

        try {
            strings = Files.readAllLines(file.toPath());
            return new ArrayList<>(strings);
        } catch (IOException exception) {
            System.out.println("Файл не найден: " + exception.getMessage());
        }

        return null;
    }

    public static ArrayList<User> readUsers(List<String> strings) {
        ArrayList<User> students = new ArrayList<>();
        strings.forEach(s -> {
            String[] fields = s.split(";");

            if (!fields[0].isEmpty()) {

                try{
                    students.add(new User(Integer.parseInt(fields[0]), fields[1], fields[2]));
                }
                catch (IllegalArgumentException exception){
                    System.out.println("Возникла ошибка обработки строки: " + s);
                }
            }
        });
        return students;
    }

    public static void writeUsersInFile(String path, String name, List<User> students){
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder().setHeader("id", "first_name", "last_name").setDelimiter(';').build();
        try(BufferedWriter writer = Files.newBufferedWriter(Paths.get(path + name));
            CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)){
            students.forEach(student -> {
                try {
                    csvPrinter.printRecord(student.getId(), student.getFirst_name(), student.getLast_name());
                    csvPrinter.flush();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            });
        }
        catch (IOException exception){
            exception.printStackTrace();
        }
    }
}
