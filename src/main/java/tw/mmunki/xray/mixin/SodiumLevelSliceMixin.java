package tw.mmunki.xray.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import tw.mmunki.xray.XrayState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Sodium reads the world through its own {@code LevelSlice} instead of the
 * vanilla render region.  The mixin is optional: without Sodium the config
 * simply does not apply.
 */
@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.world.LevelSlice")
public abstract class SodiumLevelSliceMixin {
    private static final BlockState XRAY_AIR = Blocks.AIR.defaultBlockState();

    @Inject(
            method = "getBlockState(III)Lnet/minecraft/world/level/block/state/BlockState;",
            at = @At("RETURN"),
            cancellable = true,
            require = 0)
    private void xray$hideNonWhitelistedBlocks(int x, int y, int z, CallbackInfoReturnable<BlockState> cir) {
        xray$hide(cir);
    }

    @Inject(
            method = "getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
            at = @At("RETURN"),
            cancellable = true,
            require = 0)
    private void xray$hideNonWhitelistedBlocks(BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
        xray$hide(cir);
    }

    private static void xray$hide(CallbackInfoReturnable<BlockState> cir) {
        XrayState state = XrayState.get();
        if (!state.isActive()) {
            return;
        }

        BlockState blockState = cir.getReturnValue();
        if (blockState == null || blockState.isAir()) {
            return;
        }

        if (!state.isVisible(blockState)) {
            cir.setReturnValue(XRAY_AIR);
        }
    }
}
