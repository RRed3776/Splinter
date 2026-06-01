package me.rred.splinter.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.rred.splinter.Splinter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/*
 * referencing Jingle's update checker (this version is much simpler)
 * https://github.com/DuncanRuns/Jingle/blob/v2/src/main/java/xyz/duncanruns/jingle/JingleUpdater.java
 */

public class SplinterUpdateChecker {
    private static final String UPDATE_URL = "https://github.com/RRed3776/Splinter/blob/main/update.json?raw=true";
    private static final String CURRENT_VERSION = "1.0.1";

    public synchronized static void check() {
        try {
            JsonObject json = fetchJson(UPDATE_URL);
            String latest = json.get("latest").getAsString();
            if (!CURRENT_VERSION.equals(latest)) {
                Text message = new LiteralText("[Splinter] Update available, Download ")
                        .append(new LiteralText("Here")
                                .styled(s -> s
                                        .withColor(Formatting.AQUA)
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                                                "https://github.com/RRed3776/Splinter/releases"))
                                                .withBold(true)
                                ));
                MinecraftClient.getInstance().player.sendMessage(message, false);
            }
        } catch (Exception e) {
            Splinter.LOGGER.error("Failed to grab Splinter update meta");
            return;
        }
    }

    // https://stackoverflow.com/a/1485730
    public static JsonObject fetchJson(String urlToRead) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            for (String line; (line = reader.readLine()) != null; ) {
                result.append(line);
            }
        }
        return new Gson().fromJson(result.toString(), JsonObject.class);
    }
}
