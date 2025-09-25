package net.runelite.client.plugins.microbot.XTScripts.NpcTickCounter;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.api.Perspective;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NpcTickCounterOverlay extends Overlay {

    private final Client client;
    private final NpcTickCounterConfig config;
    private final NpcTickCounterScript script;
    private List<String> trackedNpcNames;

    @Inject
    NpcTickCounterOverlay(Client client, NpcTickCounterConfig config, NpcTickCounterScript script) {
        this.client = client;
        this.config = config;
        this.script = script;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(PRIORITY_HIGH);
        updateTrackedNpcs();
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!config.showOverlay() || !Microbot.isLoggedIn()) {
            return null;
        }

        updateTrackedNpcs();

        for (NPC npc : Rs2Npc.getNpcs()) {
            if (npc == null || npc.getName() == null) continue;

            if (trackedNpcNames.isEmpty() || trackedNpcNames.contains(npc.getName())) {
                renderNpcOverlay(graphics, npc);
            }
        }

        return null;
    }

    private void renderNpcOverlay(Graphics2D graphics, NPC npc) {
        Point npcPoint = Perspective.getCanvasTextLocation(client, graphics, npc.getLocalLocation(),
                "Tick: " + script.getTickCount(), npc.getLogicalHeight() + 40);

        if (npcPoint == null) return;

        String tickText = "Tick: " + script.getTickCount();
        String interactionText = "";

        Map<String, Integer> interactions = script.getNpcInteractionCounts();
        if (config.trackInteractions() && interactions.containsKey(npc.getName())) {
            interactionText = "Interactions: " + interactions.get(npc.getName());
        }

        Font originalFont = graphics.getFont();
        graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));

        OverlayUtil.renderTextLocation(graphics, npcPoint, tickText, Color.CYAN);

        if (!interactionText.isEmpty()) {
            Point interactionPoint = new Point(npcPoint.getX(), npcPoint.getY() + 15);
            OverlayUtil.renderTextLocation(graphics, interactionPoint, interactionText, Color.YELLOW);
        }

        if (script.getState() != null) {
            Point statePoint = new Point(npcPoint.getX(), npcPoint.getY() - 15);
            String stateText = "State: " + script.getState().toString();
            OverlayUtil.renderTextLocation(graphics, statePoint, stateText, Color.GREEN);
        }

        graphics.setFont(originalFont);
    }

    private void updateTrackedNpcs() {
        String npcNamesConfig = config.npcNames();
        if (npcNamesConfig == null || npcNamesConfig.trim().isEmpty()) {
            trackedNpcNames = Arrays.asList();
        } else {
            trackedNpcNames = Arrays.stream(npcNamesConfig.split(","))
                    .map(String::trim)
                    .filter(name -> !name.isEmpty())
                    .collect(Collectors.toList());
        }
    }
}