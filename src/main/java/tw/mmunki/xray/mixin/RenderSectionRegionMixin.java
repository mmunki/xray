package tw.mmunki.xray.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import tw.mmunki.xray.XrayState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Vanilla chunk meshing reads the blocks through {@code RenderSectionRegion}.
 * Blocks outside the whitelist are returned as air, which removes their
 * geometry and stops them from occluding the blocks we keep.  Whitelisted
 * blocks are returned as glass, so the renderer draws only their outer glass
 * shell and culls the faces between adjacent glass blocks.
 */
@Pseudo
@Mixin(targets = "net.minecraft.client.renderer.chunk.RenderSectionRegion")
public abstract class RenderSectionRegionMixin {
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

        cir.setReturnValue(state.getRenderState(blockState));
    }
}
