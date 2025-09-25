package net.runelite.client.plugins.microbot.XTScripts.ExamplePlugin;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Mocrosoft + "Example Plugin",
        description = "An example plugin for XTScripts",
        tags = {"example", "microbot", "xtscripts"},
        enabledByDefault = false
)
@Slf4j
public class ExamplePlugin extends Plugin {
    @Inject
    private ExampleConfig config;

    @Provides
    ExampleConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ExampleConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ExampleOverlay exampleOverlay;

    @Inject
    ExampleScript exampleScript;

    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(exampleOverlay);
        }
        exampleScript.run(config);
    }

    protected void shutDown() {
        exampleScript.shutdown();
        overlayManager.remove(exampleOverlay);
    }
}