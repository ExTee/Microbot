package net.runelite.client.plugins.microbot.XTScripts.AutoNatureRC;

import net.runelite.client.config.*;

@ConfigGroup("AutoNatureRC")
@ConfigInformation("<h2>AutoNatureRC</h2>" +
        "<h3>Version: " + AutoNatureRCScript.version + "</h3>" +
        "<p>This plugin demonstrates the configuration structure for XTScripts plugins.</p>" +
        "<p>1. <strong>Enable Plugin:</strong> Toggle the plugin on/off</p>" +
        "<p>2. <strong>Example Setting:</strong> Configure plugin behavior</p>")
public interface AutoNatureRCConfig extends Config {

    @ConfigSection(
            name = "General",
            description = "General settings",
            position = 0
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "exampleSetting",
            name = "Example Setting",
            description = "Enable or disable example feature",
            position = 1,
            section = generalSection
    )
    default boolean exampleSetting() {
        return true;
    }

    @ConfigItem(
            keyName = "exampleNumber",
            name = "Example Number",
            description = "Example numeric setting",
            position = 2,
            section = generalSection
    )
    default int exampleNumber() {
        return 10;
    }

    @ConfigItem(
            keyName = "exampleText",
            name = "Example Text",
            description = "Example text setting",
            position = 3,
            section = generalSection
    )
    default String exampleText() {
        return "Example";
    }
}