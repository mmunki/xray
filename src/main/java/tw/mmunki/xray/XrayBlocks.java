package tw.mmunki.xray;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * The block that replaces every block which is not on the whitelist.
 *
 * <p>It is a glass-like full cube with a texture that is 25% opaque.  The
 * block is only registered on the client and is never placed in the world; it
 * exists purely as a render state for the chunk mesher.</p>
 */
public final class XrayBlocks implements ModInitializer {
    public static final Block OVERLAY = new OverlayBlock(
            BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS));

    private static boolean registered;

    @Override
    public void onInitialize() {
        init();
    }

    public static void init() {
        if (registered) {
            return;
        }
        registered = true;
        Registry.register(BuiltInRegistries.BLOCK,
                Identifier.fromNamespaceAndPath(XrayClient.MOD_ID, "overlay"), OVERLAY);
    }

    private static final class OverlayBlock extends TransparentBlock {
        private OverlayBlock(BlockBehaviour.Properties properties) {
            super(properties);
        }
    }
}
