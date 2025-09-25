package net.runelite.client.plugins.microbot.XTScripts.NpcTickCounter;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Mocrosoft + "NPC Tick Counter",
        description = "Displays tick count above specified NPCs, resetting at max count",
        tags = {"npc", "tick", "counter", "overlay", "microbot", "xtscripts"},
        enabledByDefault = false
)
@Slf4j
public class NpcTickCounterPlugin extends Plugin {

    @Inject
    private NpcTickCounterConfig config;

    @Provides
    NpcTickCounterConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(NpcTickCounterConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private NpcTickCounterOverlay npcTickCounterOverlay;

    @Getter
    private int currentTickCount = 0;

    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(npcTickCounterOverlay);
        }
        currentTickCount = 0;
    }

    @Override
    protected void shutDown() {
        if (overlayManager != null) {
            overlayManager.remove(npcTickCounterOverlay);
        }
        currentTickCount = 0;
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        // Increment tick counter
        currentTickCount++;

        // Reset if we've reached max ticks
        if (currentTickCount > config.maxTicks()) {
            currentTickCount = 1;
        }
    }
}