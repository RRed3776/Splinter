package me.rred.splinter.export;

import com.ibm.icu.impl.number.range.StandardPluralRanges;
import me.rred.splinter.Splinter;
import me.rred.splinter.client.sets.SplinterSet;
import me.rred.splinter.client.utils.TimerFormatter;

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
        String headers = "Name,Time"; // for now just hardcode this since there's only one type of file to export

        try (BufferedWriter bw = Files.newBufferedWriter(out, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)){
            // write header, create if not made already
            bw.write(headers);
            bw.newLine();
            // write rows
            for (SplinterSet set : sets) {
                for (Long time : set.getTimes()) {
                    String timeEntry = TimerFormatter.format(time);
                    bw.write(set.getName() + DELIMITER + timeEntry);
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            Splinter.LOGGER.error(e);
        }
    }
}
