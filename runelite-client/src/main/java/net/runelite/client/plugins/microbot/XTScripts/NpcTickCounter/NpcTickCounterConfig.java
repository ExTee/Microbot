package net.runelite.client.plugins.microbot.XTScripts.NpcTickCounter;

import net.runelite.client.config.*;

@ConfigGroup("NpcTickCounter")
public interface NpcTickCounterConfig extends Config {

    @ConfigSection(
            name = "General",
            description = "Configure NPC tick counter settings",
            position = 0,
            closedByDefault = false
    )
    String generalSection = "Configure NPC tick counter settings";

    @ConfigItem(
            name = "NPC ID",
            keyName = "npcId",
            position = 0,
            description = "The ID of the NPC to track ticks for",
            section = generalSection
    )
    default int npcId() {
        return 0;
    }

    @Range(min = 1, max = 100)
    @ConfigItem(
            name = "Max Ticks",
            keyName = "maxTicks",
            position = 1,
            description = "Maximum number of ticks before resetting (1-100)",
            section = generalSection
    )
    default int maxTicks() {
        return 4;
    }

    @ConfigItem(
            name = "Font Size",
            keyName = "fontSize",
            position = 2,
            description = "Font size for the tick counter display",
            section = generalSection
    )
    default int fontSize() {
        return 16;
    }

    @ConfigItem(
            name = "Text Color",
            keyName = "textColor",
            position = 3,
            description = "Color of the tick counter text",
            section = generalSection
    )
    default java.awt.Color textColor() {
        return java.awt.Color.YELLOW;
    }

    @ConfigItem(
            name = "Y Offset",
            keyName = "yOffset",
            position = 4,
            description = "Vertical offset above the NPC (negative values go higher)",
            section = generalSection
    )
    default int yOffset() {
        return -40;
    }
}