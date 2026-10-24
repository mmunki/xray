package tw.mmunki.xray;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Persistent settings of the mod.  The file lives in {@code config/xray.json}.
 *
 * <p>The fields are volatile because the chunk building threads read them
 * while the settings screen and the pause menu button write them on the
 * client thread.</p>
 */
public final class XrayConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(XrayClient.MOD_ID);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("xray.json");
    private static final XrayConfig INSTANCE = new XrayConfig();

    public volatile boolean startEnabled = false;
    public volatile boolean showOres = true;
    public volatile boolean showContainers = true;
    public volatile boolean showSpawners = true;
    public volatile boolean showFluids = true;

    private XrayConfig() {
    }

    public static XrayConfig get() {
        return INSTANCE;
    }

    public static void load() {
        if (!Files.isRegularFile(FILE)) {
            return;
        }

        try (Reader reader = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
            XrayConfig loaded = GSON.fromJson(reader, XrayConfig.class);
            if (loaded != null) {
                INSTANCE.startEnabled = loaded.startEnabled;
                INSTANCE.showOres = loaded.showOres;
                INSTANCE.showContainers = loaded.showContainers;
                INSTANCE.showSpawners = loaded.showSpawners;
                INSTANCE.showFluids = loaded.showFluids;
            }
        } catch (IOException | RuntimeException exception) {
            LOGGER.warn("Could not read {}", FILE, exception);
        }
    }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer writer = Files.newBufferedWriter(FILE, StandardCharsets.UTF_8)) {
                GSON.toJson(INSTANCE, writer);
            }
        } catch (IOException exception) {
            LOGGER.warn("Could not write {}", FILE, exception);
        }
    }
}
