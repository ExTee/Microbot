package net.runelite.client.plugins.microbot.XTScripts.AutoSaplings;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = "<html>[<font color=#ff69b4>\uD83D\uDC30</font>] " + "AutoSaplings",
        description = "Creates Saplings",
        tags = {"XT"},
        enabledByDefault = false
)
@Slf4j
public class AutoSaplingsPlugin extends Plugin {
    @Inject
    private AutoSaplingsConfig config;
    @Provides
    AutoSaplingsConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoSaplingsConfig.class);
    }

    @Inject
    AutoSaplingsScript autoSaplingsScript;


    @Override
    protected void startUp() throws AWTException {
        autoSaplingsScript.run(config);
    }

    protected void shutDown() {
        autoSaplingsScript.shutdown();
    }

}
