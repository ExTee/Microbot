package net.runelite.client.plugins.microbot.XTScripts.tob;

import com.google.inject.Provides;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.XTScripts.AutoSaplings.AutoSaplingsConfig;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = "<html>[<font color=#ff69b4>\uD83D\uDC30</font>] " + "TOB Helper",
        description = "helper for tob stuff",
        tags = {"XT"},
        enabledByDefault = false
)
public class TOBhelperPlugin extends Plugin{
    @Inject
    private TOBhelperConfig config;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private TOBhelperOverlay overlay;

    @Provides
    TOBhelperConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(TOBhelperConfig.class);
    }

    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(overlay);
        }
    }

    protected void shutDown() {
        overlayManager.remove(overlay);
    }
}
