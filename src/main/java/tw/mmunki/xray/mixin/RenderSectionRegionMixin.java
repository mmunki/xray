package tw.mmunki.xray.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import tw.mmunki.xray.XrayState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

/**
 * Vanilla chunk meshing reads the blocks through {@code RenderSectionRegion}.
 * Blocks in an enabled category are returned unchanged; every other block is
 * replaced by the translucent overlay.  Fluids are hidden when the fluid
 * category is disabled so the overlay is not covered by water or lava.
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

    @Inject(
            method = "getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;",
            at = @At("RETURN"),
            cancellable = true)
    private void xray$hideNonWhitelistedFluids(BlockPos pos, CallbackInfoReturnable<FluidState> cir) {
        xray$hideFluid(cir);
    }

    private static void xray$hideFluid(CallbackInfoReturnable<FluidState> cir) {
        XrayState state = XrayState.get();
        if (!state.isActive()) {
            return;
        }

        FluidState fluidState = cir.getReturnValue();
        if (fluidState == null || fluidState.isEmpty()) {
            return;
        }

        if (!state.isVisible(fluidState.createLegacyBlock())) {
            cir.setReturnValue(Fluids.EMPTY.defaultFluidState());
        }
    }
}
