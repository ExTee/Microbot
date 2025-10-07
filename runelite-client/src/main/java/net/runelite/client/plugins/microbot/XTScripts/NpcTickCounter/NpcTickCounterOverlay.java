package net.runelite.client.plugins.microbot.XTScripts.NpcTickCounter;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;

public class NpcTickCounterOverlay extends Overlay {

    private final Client client;
    private final NpcTickCounterConfig config;
    private final NpcTickCounterPlugin plugin;

    @Inject
    public NpcTickCounterOverlay(Client client, NpcTickCounterConfig config, NpcTickCounterPlugin plugin) {
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(OverlayPriority.MED);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (config.npcId() == 0) {
            return null;
        }

        // Set font
        graphics.setFont(new Font(FontManager.getRunescapeFont().getName(), Font.BOLD, config.fontSize()));

        // Find NPCs with the configured ID
        for (NPC npc : client.getNpcs()) {
            if (npc == null || npc.getId() != config.npcId()) {
                continue;
            }

            // Get NPC position
            LocalPoint localLocation = npc.getLocalLocation();
            if (localLocation == null) {
                continue;
            }

            // Calculate overlay position above NPC
            int height = npc.getLogicalHeight() + config.yOffset();
            Point npcPoint = Perspective.localToCanvas(client, localLocation, client.getPlane(), height);

            if (npcPoint == null) {
                continue;
            }

            // Render tick count
            String tickText = String.valueOf(plugin.getCurrentTickCount());
            OverlayUtil.renderTextLocation(graphics, npcPoint, tickText, config.textColor());
        }

        return null;
    }
}