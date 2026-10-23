package dev.mmunki.xray.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.mmunki.xray.XrayState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Vanilla chunk meshing reads the blocks through {@code RenderSectionRegion}.
 * Pretending that every block outside the whitelist is air both removes its
 * geometry and stops it from occluding the faces of the blocks we keep, which
 * is what makes the ores visible through stone.
 */
@Pseudo
@Mixin(targets = "net.minecraft.client.renderer.chunk.RenderSectionRegion")
public abstract class RenderSectionRegionMixin {
    private static final BlockState XRAY_AIR = Blocks.AIR.defaultBlockState();

    @Inject(
            method = "getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
            at = @At("RETURN"),
            cancellable = true)
    private void xray$hideNonWhitelistedBlocks(BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
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
