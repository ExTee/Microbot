package net.runelite.client.plugins.microbot.XTScripts.NpcTickCounter;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.AnimationChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Mocrosoft + "NPC Tick Counter",
        description = "Counts ticks and tracks NPC interactions",
        tags = {"npc", "tick", "counter", "microbot", "xtscripts"},
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

    @Inject
    NpcTickCounterScript npcTickCounterScript;

    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(npcTickCounterOverlay);
        }
        npcTickCounterScript.run(config);
    }

    protected void shutDown() {
        npcTickCounterScript.shutdown();
        overlayManager.remove(npcTickCounterOverlay);
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged animationChanged) {
        if (config.resetAnimationId() > 0 &&
            animationChanged.getActor() != null &&
            animationChanged.getActor().getAnimation() == config.resetAnimationId()) {
            npcTickCounterScript.resetCounterByAnimation(config.resetAnimationId());
        }
    }
}