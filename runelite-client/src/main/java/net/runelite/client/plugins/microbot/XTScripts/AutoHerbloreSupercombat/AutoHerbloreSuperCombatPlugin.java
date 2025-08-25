package net.runelite.client.plugins.microbot.XTScripts.AutoHerbloreSupercombat;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = "HerbloreSuperCombatPotions",
        description = "Creates Super Combat Potions",
        tags = {},
        enabledByDefault = false
)
@Slf4j
public class AutoHerbloreSuperCombatPlugin extends Plugin {

    @Inject
    private OverlayManager overlayManager;

    @Inject
    AutoHerbloreSuperCombatScript script;


    @Override
    protected void startUp() throws AWTException {
        script.run();
    }

    protected void shutDown() {
        script.shutdown();
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
