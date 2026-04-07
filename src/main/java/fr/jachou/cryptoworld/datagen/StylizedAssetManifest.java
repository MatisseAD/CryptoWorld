package fr.jachou.cryptoworld.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class StylizedAssetManifest {
    private static final String MANIFEST_PATH = "cryptoworld_assetgen/styled_assets.json";

    private StylizedAssetManifest() {
    }

    public static List<BlockModelSpec> loadBlockModels() {
        try (InputStream stream = StylizedAssetManifest.class.getClassLoader().getResourceAsStream(MANIFEST_PATH)) {
            if (stream == null) {
                throw new IllegalStateException("Missing stylized asset manifest at " + MANIFEST_PATH);
            }

            try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                JsonArray blocks = root.getAsJsonArray("blockModels");
                List<BlockModelSpec> specs = new ArrayList<>();

                for (JsonElement element : blocks) {
                    JsonObject block = element.getAsJsonObject();
                    specs.add(new BlockModelSpec(
                            requiredString(block, "name"),
                            Layout.fromName(requiredString(block, "layout")),
                            getString(block, "all"),
                            getString(block, "side"),
                            getString(block, "top"),
                            getString(block, "bottom")
                    ));
                }

                return specs;
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to load stylized asset manifest", exception);
        }
    }

    private static String requiredString(JsonObject object, String key) {
        if (!object.has(key)) {
            throw new IllegalStateException("Missing required manifest key: " + key);
        }
        return object.get(key).getAsString();
    }

    private static String getString(JsonObject object, String key) {
        return object.has(key) ? object.get(key).getAsString() : null;
    }

    public record BlockModelSpec(String name, Layout layout, String all, String side, String top, String bottom) {
        public String bottomOrTop() {
            return bottom == null ? top : bottom;
        }
    }

    public enum Layout {
        CUBE_ALL,
        CUBE_BOTTOM_TOP;

        public static Layout fromName(String name) {
            return switch (name) {
                case "cube_all" -> CUBE_ALL;
                case "cube_bottom_top" -> CUBE_BOTTOM_TOP;
                default -> throw new IllegalStateException("Unknown layout in stylized asset manifest: " + name);
            };
        }
    }
}
