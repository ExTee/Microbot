package net.runelite.client.plugins.microbot.XTScripts.AutoProjectilePrayer;

import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.api.Client;

import javax.inject.Inject;
import java.awt.*;

import static net.runelite.client.ui.overlay.OverlayUtil.renderPolygon;

public class AutoProjectilePrayerOverlay extends OverlayPanel {

    private Client client;

    @Inject
    public void ProjectileOutlineOverlay(Client client)
    {
        this.client = client;
        setPosition(OverlayPosition.DYNAMIC);
    }

    @Inject
    AutoProjectilePrayerOverlay(AutoProjectilePrayerPlugin plugin)
    {
        super(plugin);
        setPosition(OverlayPosition.DYNAMIC);
        setNaughty();
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            panelComponent.setPreferredSize(new Dimension(200, 300));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("AutoPrayer is ON")
                    .color(Color.GREEN)
                    .build());

        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }

}
