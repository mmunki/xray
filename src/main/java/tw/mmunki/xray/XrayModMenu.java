package tw.mmunki.xray;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/**
 * Optional Mod Menu integration.  Mod Menu only loads this class when it is
 * installed, so the mod still works without it.
 */
public final class XrayModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return XrayConfigScreen::new;
    }
}
