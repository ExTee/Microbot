package net.runelite.client.plugins.microbot.XTScripts.tob;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class TOBhelperOverlay extends OverlayPanel {

    private Client client;

    @Inject
    public void ProjectileOutlineOverlay(Client client)
    {
        this.client = client;
        setPosition(OverlayPosition.DYNAMIC);
    }

    @Inject
    TOBhelperOverlay(TOBhelperPlugin plugin)
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
                    .text("TOB helper is ON")
                    .color(Color.GREEN)
                    .build());

        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }


        return super.render(graphics);
    }

}
