package me.rred.splinter.export;

import me.rred.splinter.client.sets.SplinterSet;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

// referencing https://javabook.mccue.dev/files/write_to_a_file
// https://www.youtube.com/watch?v=UxS4dJaRgiY

public class CsvExport {

    private static final String DELIMITER = ",";

    public static void export(List<SplinterSet> sets, Path out) {
        List<String> headers = new ArrayList<>();
        headers.add("Name");
        headers.add("Time");

        try (BufferedWriter bw = Files.newBufferedWriter(out, StandardOpenOption.APPEND)){
            // write header, create if not made already
            bw.write(writeHeader(headers));
            bw.newLine();
            // write rows
            for (SplinterSet set : sets) {
                for (Long time : set.getTimes()) {
                    bw.write(set.getName() + DELIMITER + time.toString());
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String writeHeader(List<String> headers) {
        StringBuilder result = new StringBuilder();
        headers.stream().forEach(item -> result.append(item).append(DELIMITER));
        return result.toString();
    }

}
