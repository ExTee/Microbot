package net.runelite.client.plugins.microbot.XTScripts.ExamplePlugin;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class ExampleOverlay extends OverlayPanel {
    @Inject
    ExampleOverlay(ExamplePlugin plugin) {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        setNaughty();
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            panelComponent.setPreferredSize(new Dimension(200, 150));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Example Plugin V" + ExampleScript.version)
                    .color(Color.GREEN)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder().build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Status:")
                    .right(Microbot.isLoggedIn() ? "Running" : "Not logged in")
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Plugin:")
                    .right("Example XTScript")
                    .build());

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
}