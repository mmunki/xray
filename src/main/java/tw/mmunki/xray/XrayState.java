package tw.mmunki.xray;

import java.util.Set;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Global, client-side state of the X-Ray mod.
 *
 * <p>{@link #active} is read from the chunk building threads, so it is
 * volatile.  The block categories are immutable, so they can be read from
 * those threads without synchronisation.</p>
 */
public final class XrayState {
    private static final Set<Block> ORES = Set.of(
            Blocks.COAL_ORE,
            Blocks.DEEPSLATE_COAL_ORE,
            Blocks.IRON_ORE,
            Blocks.DEEPSLATE_IRON_ORE,
            Blocks.COPPER_ORE,
            Blocks.DEEPSLATE_COPPER_ORE,
            Blocks.GOLD_ORE,
            Blocks.DEEPSLATE_GOLD_ORE,
            Blocks.REDSTONE_ORE,
            Blocks.DEEPSLATE_REDSTONE_ORE,
            Blocks.EMERALD_ORE,
            Blocks.DEEPSLATE_EMERALD_ORE,
            Blocks.LAPIS_ORE,
            Blocks.DEEPSLATE_LAPIS_ORE,
            Blocks.DIAMOND_ORE,
            Blocks.DEEPSLATE_DIAMOND_ORE,
            Blocks.NETHER_GOLD_ORE,
            Blocks.NETHER_QUARTZ_ORE,
            Blocks.ANCIENT_DEBRIS,
            Blocks.BUDDING_AMETHYST);

    private static final Set<Block> CONTAINERS = Set.of(
            Blocks.CHEST,
            Blocks.TRAPPED_CHEST);

    private static final Set<Block> SPAWNERS = Set.of(
            Blocks.SPAWNER,
            Blocks.TRIAL_SPAWNER);

    private static final Set<Block> FLUIDS = Set.of(
            Blocks.WATER,
            Blocks.LAVA);

    private static final XrayState INSTANCE = new XrayState();

    /** X-Ray is off by default; the player has to enable it explicitly. */
    private volatile boolean active;

    private XrayState() {
    }

    public static XrayState get() {
        return INSTANCE;
    }

    /** Restores the "enable at startup" setting after the config was loaded. */
    public void initFromConfig() {
        active = XrayConfig.get().startEnabled;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void toggle() {
        active = !active;
    }

    /**
     * @return {@code true} when the block belongs to an enabled whitelist
     *         category (ores, containers, spawners or fluids).
     */
    public boolean isVisible(BlockState state) {
        Block block = state.getBlock();
        XrayConfig config = XrayConfig.get();

        return (config.showOres && ORES.contains(block))
                || (config.showContainers && CONTAINERS.contains(block))
                || (config.showSpawners && SPAWNERS.contains(block))
                || (config.showFluids && FLUIDS.contains(block));
    }

    /**
     * The block state that the chunk renderer has to use for the given world
     * block while X-Ray is active.
     *
     * <p>Whitelisted blocks are returned unchanged, so ores and containers are
     * shown exactly as they look in the world.  Every other block becomes the
     * 25%-opaque glass overlay, which keeps the shape of the terrain visible
     * without hiding the ores behind it.</p>
     */
    public BlockState getRenderState(BlockState state) {
        if (isVisible(state)) {
            return state;
        }
        return XrayBlocks.OVERLAY.defaultBlockState();
    }
}
