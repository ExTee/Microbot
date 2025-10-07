package net.runelite.client.plugins.microbot.XTScripts.ExamplePlugin;

import net.runelite.client.config.*;

@ConfigGroup("Example")
@ConfigInformation("<h2>Example Plugin</h2>" +
        "<h3>Version: " + ExampleScript.version + "</h3>" +
        "<p>This is an example plugin that demonstrates the basic structure for XTScripts plugins.</p>" +
        "<p>1. <strong>Enable Plugin:</strong> Toggle the plugin on/off</p>" +
        "<p>2. <strong>Example Setting:</strong> Configure example behavior</p>")
public interface ExampleConfig extends Config {

    @ConfigSection(
            name = "General",
            description = "General settings",
            position = 0
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "exampleSetting",
            name = "Example Setting",
            description = "This is an example configuration setting",
            position = 1,
            section = generalSection
    )
    default boolean exampleSetting() {
        return true;
    }

    @ConfigItem(
            keyName = "exampleNumber",
            name = "Example Number",
            description = "This is an example number setting",
            position = 2,
            section = generalSection
    )
    default int exampleNumber() {
        return 10;
    }

    @ConfigItem(
            keyName = "exampleText",
            name = "Example Text",
            description = "This is an example text setting",
            position = 3,
            section = generalSection
    )
    default String exampleText() {
        return "Example";
    }
}