package tw.mmunki.xray;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

/**
 * The text button that is added to the pause menu.  Instead of the old image
 * icon it simply reads "Minecraft X" in bold green and toggles the mod exactly
 * like the configured key does.
 */
public final class XrayPauseButton extends Button.Plain {
    private static final Component LABEL = Component.literal("Minecraft X")
            .withStyle(ChatFormatting.BOLD, ChatFormatting.GREEN);

    public XrayPauseButton(int x, int y, int width, int height) {
        super(x, y, width, height, LABEL,
                button -> XrayClient.toggle(Minecraft.getInstance()), DEFAULT_NARRATION);
        setTooltip(Tooltip.create(Component.translatable("xray.button.tooltip")));
    }
}
