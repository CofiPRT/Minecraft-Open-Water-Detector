package ro.cofi.openwaterdetector;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class OpenWaterDetector implements ClientModInitializer {

    public static Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "open-water-detector";
    public static final String MOD_NAME = "OpenWaterDetector";

    @Override
    public void onInitializeClient() {
        LOGGER.log(Level.INFO, "Initializing");

        ClientTickEvents.END_CLIENT_TICK.register(new MainTickEvent());

        LOGGER.log(Level.INFO, "Successfully loaded");
    }

    public static void logException(Throwable ex) {
        LOGGER.log(Level.ERROR, "Unexpected exception", ex);
    }

    public static void logError(String message) {
        log(Level.ERROR, message);
    }

    public static void logWarning(String message) {
        log(Level.WARN, message);
    }

    public static void log(String message) {
        log(Level.INFO, message);
    }

    public static void log(Level level, String message) {
        LOGGER.log(level, message);
    }

    public static MinecraftClient getClient() {
        return MinecraftClient.getInstance();
    }

    public static ClientPlayerEntity getPlayer() {
        return getClient().player;
    }
}
