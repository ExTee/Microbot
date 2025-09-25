package net.runelite.client.plugins.microbot.XTScripts.NpcTickCounter;

import net.runelite.client.config.*;

@ConfigGroup("NpcTickCounter")
@ConfigInformation("<h2>NPC Tick Counter</h2>" +
        "<h3>Version: " + NpcTickCounterScript.version + "</h3>" +
        "<p>This plugin counts game ticks and tracks NPC interactions.</p>" +
        "<p>1. <strong>Enable Counting:</strong> Toggle tick counting on/off</p>" +
        "<p>2. <strong>NPC Name Filter:</strong> Specify NPC names to track (comma separated)</p>" +
        "<p>3. <strong>Reset Counter:</strong> Reset the tick counter to zero</p>")
public interface NpcTickCounterConfig extends Config {

    @ConfigSection(
            name = "General",
            description = "General settings",
            position = 0
    )
    String generalSection = "general";

    @ConfigSection(
            name = "NPC Tracking",
            description = "NPC tracking settings",
            position = 1
    )
    String npcSection = "npc";

    @ConfigItem(
            keyName = "enableCounting",
            name = "Enable Counting",
            description = "Enable or disable tick counting",
            position = 1,
            section = generalSection
    )
    default boolean enableCounting() {
        return true;
    }

    @ConfigItem(
            keyName = "showOverlay",
            name = "Show Overlay",
            description = "Display the tick counter overlay",
            position = 2,
            section = generalSection
    )
    default boolean showOverlay() {
        return true;
    }

    @ConfigItem(
            keyName = "npcNames",
            name = "NPC Names",
            description = "Comma-separated list of NPC names to track",
            position = 1,
            section = npcSection
    )
    default String npcNames() {
        return "";
    }

    @ConfigItem(
            keyName = "trackInteractions",
            name = "Track Interactions",
            description = "Track when NPCs are clicked or interacted with",
            position = 2,
            section = npcSection
    )
    default boolean trackInteractions() {
        return true;
    }

    @ConfigItem(
            keyName = "maxTickCount",
            name = "Max Tick Count",
            description = "Maximum number of ticks to count before resetting (0 = no limit)",
            position = 3,
            section = generalSection
    )
    default int maxTickCount() {
        return 0;
    }
}