package me.rred.splinter;

import me.rred.splinter.client.routing.Route;
import me.rred.splinter.client.sets.SplinterSet;
import me.rred.splinter.client.utils.TimerFormatter;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class SplinterDataManager {
    // for now try to implement overriding data, later store last backups
    public void saveRoutes(List<Route> routes) {
        Path routesPath = FabricLoader.getInstance().getGameDir().resolve("splinter/routes.json");
        try (BufferedWriter bw = Files.newBufferedWriter(routesPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)){
            bw.write("{");
            bw.newLine();

            bw.write("}");
        } catch (IOException e) {
            Splinter.LOGGER.error(e);
        }
    }

    public void saveSets(List<SplinterSet> sets) {
        Path setsPath = FabricLoader.getInstance().getGameDir().resolve("splinter/sets.json");
        try (BufferedWriter bw = Files.newBufferedWriter(setsPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)){
            bw.write("{");
        } catch (IOException e) {
            Splinter.LOGGER.error(e);
        }
    }

    public void loadRoutes() {

    }

    public void loadSets() {

    }
}
