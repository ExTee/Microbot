package net.runelite.client.plugins.microbot.XTScripts.ShipwreckSalvaging;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import javax.inject.Inject;
import java.awt.*;

import static net.runelite.client.plugins.microbot.XTScripts.ShipwreckSalvaging.ShipwreckSalvagingScript.SAILING_CRYSTAL_EXTRACTOR_ACTIVATED;
import static net.runelite.client.plugins.microbot.XTScripts.ShipwreckSalvaging.ShipwreckSalvagingScript.SAILING_SALVAGING_STATION_3X8;
import static net.runelite.client.plugins.microbot.util.Global.sleep;

@PluginDescriptor(
        name = "<html>[<font color=#ff69b4>\uD83D\uDC30</font>] " + "Shipwreck Salvaging",
        description = "Salvages shipwrecks",
        tags = {"xtscripts"},
        enabledByDefault = false
)
@Slf4j
public class ShipwreckSalvagingPlugin extends Plugin {
//    @Inject
    private ShipwreckSalvagingConfig config;

//    @Provides
//    ExampleConfig provideConfig(ConfigManager configManager) {
//        return configManager.getConfig(ExampleConfig.class);
//    }
//
//    @Inject
//    private OverlayManager overlayManager;
//
//    @Inject
//    private ExampleOverlay overlay;

    @Inject
    ShipwreckSalvagingScript script;

    @Override
    protected void startUp() throws AWTException {
//        if (overlayManager != null) {
//            overlayManager.add(overlay);
//        }
        script.run(config);
    }

    @Subscribe
    private void onChatMessage(final ChatMessage event) {
        final ChatMessageType chatMessageType = event.getType();

        if (chatMessageType != ChatMessageType.SPAM && chatMessageType != ChatMessageType.GAMEMESSAGE) {
            return;
        }

        final String message = event.getMessage();

        String MSG_CRYSTAL_EXTRACTOR_READY = "<col=00ffff>Your crystal extractor has harvested a crystal mote!";
        if (message.equalsIgnoreCase(MSG_CRYSTAL_EXTRACTOR_READY)){
            script.state = State.CRYSTAL;
//            script.harvestCrystalExtractor();
//            if (script.state == State.SORTING){
//                Rs2GameObject.interact(SAILING_SALVAGING_STATION_3X8, "Sort-salvage");
//            }
//            script.state = State.CRYSTAL;
        }
    }

    protected void shutDown() {
        script.shutdown();
//        overlayManager.remove(overlay);
    }
}