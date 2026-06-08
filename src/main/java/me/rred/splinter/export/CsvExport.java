package me.rred.splinter.export;

import me.rred.splinter.client.sets.SplinterSet;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class CsvExport {
    public static void export(List<SplinterSet> sets, Path out) {
        for (SplinterSet set : sets) {
            List<Long> times = set.getTimes();
            // https://www.w3schools.com/java/java_files_write.asp
            // need to create an export file in the Minecraft folder at some point
            // for now just testing/learning file writing stuff in Java since there's like 2 billion ways to do it
            try (FileWriter myWriter = new FileWriter("filename.txt")) {
                myWriter.write("Files in Java might be tricky, but it is fun enough!");
                System.out.println("Successfully wrote to the file.");
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }
    }
}
