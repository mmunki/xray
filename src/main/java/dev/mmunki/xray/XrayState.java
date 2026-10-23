package dev.mmunki.xray;

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
    private static final XrayState INSTANCE = new XrayState();

    private static final Set<Block> DEFAULT_WHITELIST = new HashSet<>();

    static {
        DEFAULT_WHITELIST.add(Blocks.COAL_ORE);
        DEFAULT_WHITELIST.add(Blocks.DEEPSLATE_COAL_ORE);
        DEFAULT_WHITELIST.add(Blocks.IRON_ORE);
        DEFAULT_WHITELIST.add(Blocks.DEEPSLATE_IRON_ORE);
        DEFAULT_WHITELIST.add(Blocks.COPPER_ORE);
        DEFAULT_WHITELIST.add(Blocks.DEEPSLATE_COPPER_ORE);
        DEFAULT_WHITELIST.add(Blocks.GOLD_ORE);
        DEFAULT_WHITELIST.add(Blocks.DEEPSLATE_GOLD_ORE);
        DEFAULT_WHITELIST.add(Blocks.REDSTONE_ORE);
        DEFAULT_WHITELIST.add(Blocks.DEEPSLATE_REDSTONE_ORE);
        DEFAULT_WHITELIST.add(Blocks.EMERALD_ORE);
        DEFAULT_WHITELIST.add(Blocks.DEEPSLATE_EMERALD_ORE);
        DEFAULT_WHITELIST.add(Blocks.LAPIS_ORE);
        DEFAULT_WHITELIST.add(Blocks.DEEPSLATE_LAPIS_ORE);
        DEFAULT_WHITELIST.add(Blocks.DIAMOND_ORE);
        DEFAULT_WHITELIST.add(Blocks.DEEPSLATE_DIAMOND_ORE);
        DEFAULT_WHITELIST.add(Blocks.NETHER_GOLD_ORE);
        DEFAULT_WHITELIST.add(Blocks.NETHER_QUARTZ_ORE);
        DEFAULT_WHITELIST.add(Blocks.ANCIENT_DEBRIS);
        DEFAULT_WHITELIST.add(Blocks.BUDDING_AMETHYST);
        DEFAULT_WHITELIST.add(Blocks.CHEST);
        DEFAULT_WHITELIST.add(Blocks.TRAPPED_CHEST);
        DEFAULT_WHITELIST.add(Blocks.SPAWNER);
        DEFAULT_WHITELIST.add(Blocks.TRIAL_SPAWNER);
        DEFAULT_WHITELIST.add(Blocks.WATER);
        DEFAULT_WHITELIST.add(Blocks.LAVA);
    }

    private volatile boolean active;
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
     * @return {@code true} when the block should still be rendered while X-Ray
     *         is active.
     */
    public boolean isVisible(BlockState state) {
        return whitelist.contains(state.getBlock());
    }

    public Set<Block> getWhitelist() {
        return whitelist;
    }
}
