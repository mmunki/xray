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
     * chunks.  Used by both the key binding and the pause menu button.
     */
    public static void toggle(Minecraft client) {
        XrayState state = XrayState.get();
        state.toggle();

        if (client.player != null) {
            Component message = Component.translatable(state.isActive() ? "xray.toggle.on" : "xray.toggle.off")
                    .withStyle(state.isActive() ? ChatFormatting.GREEN : ChatFormatting.RED);
            client.player.sendOverlayMessage(message);
        }

        if (client.level != null) {
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
