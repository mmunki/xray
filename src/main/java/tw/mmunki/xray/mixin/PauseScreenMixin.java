package tw.mmunki.xray.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import tw.mmunki.xray.XrayPauseButton;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Adds the "Minecraft X" button to the pause menu grid, in the same list as
 * the Mod Menu button instead of the old icon in the top-right corner.
 */
@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin extends Screen {
    protected PauseScreenMixin(Component title) {
        super(title);
    }

    @Inject(
            method = "createPauseMenu",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;ILnet/minecraft/client/gui/layouts/LayoutSettings;)Lnet/minecraft/client/gui/layouts/LayoutElement;",
                    ordinal = 1,
                    shift = At.Shift.AFTER))
    private void xray$addToggleButton(CallbackInfo ci, @Local GridLayout.RowHelper helper) {
        helper.addChild(new XrayPauseButton(0, 0, 204, 20), 2);
    }
}
