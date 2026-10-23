package tw.mmunki.xray;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Global, client-side state of the X-Ray mod.
 *
 * <p>{@link #active} is read from the chunk building threads, so it is
 * volatile.  The whitelist is only mutated from the client thread while the
 * mod is being initialised, which makes a plain {@link HashSet} safe here.</p>
 */
public final class XrayState {
    /*
     * The whitelist has to be populated before INSTANCE is created.  The
     * constructor copies DEFAULT_WHITELIST, so declaring INSTANCE first (as an
     * earlier version did) made the copy run while the field was still null and
     * threw a NullPointerException from HashSet(Collection), which in turn made
     * the whole class fail with NoClassDefFoundError.
     */
    private static final Set<Block> DEFAULT_WHITELIST;

    static {
        Set<Block> blocks = new HashSet<>();
        blocks.add(Blocks.COAL_ORE);
        blocks.add(Blocks.DEEPSLATE_COAL_ORE);
        blocks.add(Blocks.IRON_ORE);
        blocks.add(Blocks.DEEPSLATE_IRON_ORE);
        blocks.add(Blocks.COPPER_ORE);
        blocks.add(Blocks.DEEPSLATE_COPPER_ORE);
        blocks.add(Blocks.GOLD_ORE);
        blocks.add(Blocks.DEEPSLATE_GOLD_ORE);
        blocks.add(Blocks.REDSTONE_ORE);
        blocks.add(Blocks.DEEPSLATE_REDSTONE_ORE);
        blocks.add(Blocks.EMERALD_ORE);
        blocks.add(Blocks.DEEPSLATE_EMERALD_ORE);
        blocks.add(Blocks.LAPIS_ORE);
        blocks.add(Blocks.DEEPSLATE_LAPIS_ORE);
        blocks.add(Blocks.DIAMOND_ORE);
        blocks.add(Blocks.DEEPSLATE_DIAMOND_ORE);
        blocks.add(Blocks.NETHER_GOLD_ORE);
        blocks.add(Blocks.NETHER_QUARTZ_ORE);
        blocks.add(Blocks.ANCIENT_DEBRIS);
        blocks.add(Blocks.BUDDING_AMETHYST);
        blocks.add(Blocks.CHEST);
        blocks.add(Blocks.TRAPPED_CHEST);
        blocks.add(Blocks.SPAWNER);
        blocks.add(Blocks.TRIAL_SPAWNER);
        blocks.add(Blocks.WATER);
        blocks.add(Blocks.LAVA);
        DEFAULT_WHITELIST = Set.copyOf(blocks);
    }

    private static final XrayState INSTANCE = new XrayState();

    private static final BlockState GLASS = Blocks.GLASS.defaultBlockState();
    private static final BlockState AIR = Blocks.AIR.defaultBlockState();

    /** X-Ray is off by default; the player has to enable it explicitly. */
    private volatile boolean active = false;
    private final Set<Block> whitelist = new HashSet<>(DEFAULT_WHITELIST);

    private XrayState() {
    }

    public static XrayState get() {
        return INSTANCE;
    }

    public boolean isActive() {
        return active;
    }

    public void toggle() {
        active = !active;
    }

    /**
     * @return {@code true} when the block belongs to the ore/container whitelist.
     */
    public boolean isVisible(BlockState state) {
        return whitelist.contains(state.getBlock());
    }

    /**
     * The block state that the chunk renderer has to use for the given world
     * block while X-Ray is active.
     *
     * <p>Whitelisted blocks are replaced by glass, so only their outer glass
     * shell is drawn; the faces shared by adjacent glass blocks are culled by
     * the renderer, which removes the interior.  Everything else becomes air,
     * which also stops it from occluding the glass outlines.</p>
     */
    public BlockState getRenderState(BlockState state) {
        if (whitelist.contains(state.getBlock())) {
            return GLASS;
        }
        return AIR;
    }

    public Set<Block> getWhitelist() {
        return whitelist;
    }
}
