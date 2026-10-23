package tw.mmunki.xray;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/**
 * The icon button that is added to the pause menu.  The X-Ray icon is drawn in
 * its normal colours while the mod is off and in green while it is active.
 * Clicking the button toggles the mod exactly like the configured key does.
 */
public final class XrayPauseButton extends Button {
    private static final Identifier ICON_OFF = Identifier.fromNamespaceAndPath(
            XrayClient.MOD_ID, "textures/gui/xray_icon.png");
    private static final Identifier ICON_ON = Identifier.fromNamespaceAndPath(
            XrayClient.MOD_ID, "textures/gui/xray_icon_green.png");
    private static final int ICON_INSET = 2;

    public XrayPauseButton(int x, int y, int size) {
        super(x, y, size, size, Component.translatable("xray.button"),
                button -> XrayClient.toggle(Minecraft.getInstance()), DEFAULT_NARRATION);
        setTooltip(Tooltip.create(Component.translatable("xray.button.tooltip")));
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        extractDefaultSprite(graphics);

        Identifier icon = XrayState.get().isActive() ? ICON_ON : ICON_OFF;
        int iconSize = getWidth() - ICON_INSET * 2;
        int iconX = getX() + ICON_INSET;
        int iconY = getY() + ICON_INSET;
        graphics.blit(icon, iconX, iconY, iconX + iconSize, iconY + iconSize, 0.0F, 1.0F, 0.0F, 1.0F);
    }
}
