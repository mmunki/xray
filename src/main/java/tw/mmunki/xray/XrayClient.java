package tw.mmunki.xray;

import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.SectionPos;
import net.minecraft.network.chat.Component;

public final class XrayClient implements ClientModInitializer {
    public static final String MOD_ID = "xray";

    private static final InputConstants.Key TOGGLE_KEY = InputConstants.getKey("key.keyboard.x");
    public static final KeyMapping TOGGLE = new KeyMapping(
            "key.xray.toggle",
            TOGGLE_KEY.getType(),
            TOGGLE_KEY.getValue(),
            KeyMapping.Category.MISC);

    @Override
    public void onInitializeClient() {
        XrayBlocks.init();
        XrayConfig.load();
        XrayState.get().initFromConfig();

        KeyMappingHelper.registerKeyMapping(TOGGLE);
        ClientTickEvents.END_CLIENT_TICK.register(XrayClient::onEndClientTick);
    }

    private static void onEndClientTick(Minecraft client) {
        boolean toggled = false;
        while (TOGGLE.consumeClick()) {
            toggled = true;
        }

        if (toggled) {
            toggle(client);
        }
    }

    /**
     * Toggles X-Ray, shows the action bar message and rebuilds the visible
     * chunks.  Used by the key binding, the pause menu button and the settings
     * screen.
     */
    public static void toggle(Minecraft client) {
        setActive(client, !XrayState.get().isActive());
    }

    /** Enables or disables X-Ray and refreshes the rendered chunks. */
    public static void setActive(Minecraft client, boolean active) {
        XrayState state = XrayState.get();
        state.setActive(active);

        if (client.player != null) {
            Component message = Component.translatable(active ? "xray.toggle.on" : "xray.toggle.off")
                    .withStyle(active ? ChatFormatting.GREEN : ChatFormatting.RED);
            client.player.sendOverlayMessage(message);
        }

        refreshChunks(client);
    }

    /** Rebuilds the chunks after a setting changed, but only while active. */
    public static void refreshChunks(Minecraft client) {
        if (XrayState.get().isActive()) {
            rebuildChunks(client);
        }
    }

    /**
     * Chunk meshes are built asynchronously and cached.  Mark the whole
     * render-distance area dirty so the change is visible immediately.
     */
    private static void rebuildChunks(Minecraft client) {
        if (client.player == null || client.level == null) {
            return;
        }

        int radius = client.options.getEffectiveRenderDistance();
        SectionPos camera = SectionPos.of(client.player.blockPosition());
        int minY = client.level.getMinSectionY();
        int maxY = client.level.getMaxSectionY() - 1;

        for (int y = minY; y <= maxY; y++) {
            for (int x = camera.getX() - radius; x <= camera.getX() + radius; x++) {
                for (int z = camera.getZ() - radius; z <= camera.getZ() + radius; z++) {
                    client.level.setSectionDirtyWithNeighbors(x, y, z);
                }
            }
        }
    }
}
