package net.runelite.client.plugins.microbot.XTScripts.AutoNatureRC;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = "<html>[<font color=#ff69b4>\uD83D\uDC30</font>] "+ "AutoNatureRC",
        description = "An example plugin",
        tags = {},
        enabledByDefault = false
)
@Slf4j
public class AutoNatureRCPlugin extends Plugin {
    @Inject
    private AutoNatureRCConfig config;

    @Provides
    AutoNatureRCConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoNatureRCConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private AutoNatureRCOverlay overlay;

    @Inject
    AutoNatureRCScript script;

    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(overlay);
        }
        script.run(config);
    }

    protected void shutDown() {
        script.shutdown();
        overlayManager.remove(overlay);
    }
}