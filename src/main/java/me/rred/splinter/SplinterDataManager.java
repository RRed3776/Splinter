package me.rred.splinter;

import com.google.gson.Gson;
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
        Path path = FabricLoader.getInstance().getGameDir().resolve("splinter/routes.json");
        Gson gson = new Gson();
        String json = gson.toString();
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, gson.toJson(routes));
        } catch (IOException e) {
            Splinter.LOGGER.error("Failed to save routes: {}", e.getMessage());
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
