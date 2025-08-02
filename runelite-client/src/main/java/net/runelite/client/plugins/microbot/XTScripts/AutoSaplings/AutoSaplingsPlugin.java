package net.runelite.client.plugins.microbot.XTScripts.AutoSaplings;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = "AutoSaplings",
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
    int ticks = 10;
    @Subscribe
    public void onGameTick(GameTick tick)
    {
        //System.out.println(getName().chars().mapToObj(i -> (char)(i + 3)).map(String::valueOf).collect(Collectors.joining()));

        if (ticks > 0) {
            ticks--;
        } else {
            ticks = 10;
        }

    }

}
