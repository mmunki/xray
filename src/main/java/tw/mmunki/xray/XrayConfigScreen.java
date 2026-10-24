package tw.mmunki.xray;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

/**
 * The settings screen that Mod Menu opens for this mod.  Every setting is
 * stored in {@code config/xray.json} and takes effect immediately.
 */
public final class XrayConfigScreen extends Screen {
    private static final int BUTTON_WIDTH = 204;

    private final Screen parent;
    private GridLayout layout;

    public XrayConfigScreen(Screen parent) {
        super(Component.translatable("xray.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        layout = new GridLayout();
        layout.rowSpacing(6);
        layout.columnSpacing(6);
        layout.defaultCellSetting().padding(2).alignHorizontallyCenter();
        GridLayout.RowHelper helper = layout.createRowHelper(1);

        helper.addChild(Button.builder(stateLabel("xray.config.enabled", XrayState.get().isActive()), button -> {
            XrayClient.setActive(minecraft, !XrayState.get().isActive());
            button.setMessage(stateLabel("xray.config.enabled", XrayState.get().isActive()));
        }).width(BUTTON_WIDTH).build());

        helper.addChild(Button.builder(stateLabel("xray.config.start", XrayConfig.get().startEnabled), button -> {
            XrayConfig.get().startEnabled = !XrayConfig.get().startEnabled;
            button.setMessage(stateLabel("xray.config.start", XrayConfig.get().startEnabled));
            XrayConfig.save();
        }).width(BUTTON_WIDTH).build());

        helper.addChild(Button.builder(stateLabel("xray.config.ores", XrayConfig.get().showOres), button -> {
            XrayConfig.get().showOres = !XrayConfig.get().showOres;
            button.setMessage(stateLabel("xray.config.ores", XrayConfig.get().showOres));
            XrayConfig.save();
            XrayClient.refreshChunks(minecraft);
        }).width(BUTTON_WIDTH).build());

        helper.addChild(Button.builder(stateLabel("xray.config.containers", XrayConfig.get().showContainers), button -> {
            XrayConfig.get().showContainers = !XrayConfig.get().showContainers;
            button.setMessage(stateLabel("xray.config.containers", XrayConfig.get().showContainers));
            XrayConfig.save();
            XrayClient.refreshChunks(minecraft);
        }).width(BUTTON_WIDTH).build());

        helper.addChild(Button.builder(stateLabel("xray.config.spawners", XrayConfig.get().showSpawners), button -> {
            XrayConfig.get().showSpawners = !XrayConfig.get().showSpawners;
            button.setMessage(stateLabel("xray.config.spawners", XrayConfig.get().showSpawners));
            XrayConfig.save();
            XrayClient.refreshChunks(minecraft);
        }).width(BUTTON_WIDTH).build());

        helper.addChild(Button.builder(stateLabel("xray.config.fluids", XrayConfig.get().showFluids), button -> {
            XrayConfig.get().showFluids = !XrayConfig.get().showFluids;
            button.setMessage(stateLabel("xray.config.fluids", XrayConfig.get().showFluids));
            XrayConfig.save();
            XrayClient.refreshChunks(minecraft);
        }).width(BUTTON_WIDTH).build());

        helper.addChild(Button.builder(CommonComponents.GUI_DONE, button -> onClose())
                .width(BUTTON_WIDTH).build());

        layout.arrangeElements();
        layout.setX((width - layout.getWidth()) / 2);
        layout.setY((height - layout.getHeight()) / 2);
        layout.visitWidgets(this::addRenderableWidget);
    }

    private static Component stateLabel(String key, boolean value) {
        return Component.translatable(key, value ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.centeredText(font, title, width / 2, 15, 0xFFFFFF);
        graphics.centeredText(font, Component.translatable("xray.config.note"),
                width / 2, height - 28, 0xA0A0A0);
    }

    @Override
    public void onClose() {
        minecraft.gui.setScreen(parent);
    }
}
