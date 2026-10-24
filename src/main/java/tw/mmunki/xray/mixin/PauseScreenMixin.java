package tw.mmunki.xray.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import tw.mmunki.xray.XrayPauseButton;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Adds the small X toggle button to the top-right corner of the pause menu,
 * away from the vanilla menu grid.
 */
@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin extends Screen {
    protected PauseScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void xray$addToggleButton(CallbackInfo ci) {
        addRenderableWidget(new XrayPauseButton(width - 28, 8, 20));
    }
}
