package net.runelite.client.plugins.microbot.XTScripts.AutoSaplings;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("AutoSaplings")
public interface AutoSaplingsConfig extends Config {
    @ConfigItem(
            keyName = "enableYew",
            name = "Yew",
            description = "Enable Yew sapling job"
    )
    default boolean enableYew() {
        return false;
    }

    @ConfigItem(
            keyName = "enableMagic",
            name = "Magic",
            description = "Enable Magic sapling job"
    )
    default boolean enableMagic() {
        return false;
    }

    @ConfigItem(
            keyName = "enablePalm",
            name = "Palm",
            description = "Enable Palm sapling job"
    )
    default boolean enablePalm() {
        return false;
    }

    @ConfigItem(
            keyName = "enableCelastrus",
            name = "Celastrus",
            description = "Enable Celastrus sapling job"
    )
    default boolean enableCelastrus() {
        return false;
    }

    @ConfigItem(
            keyName = "enableDragonfruit",
            name = "Dragonfruit",
            description = "Enable Dragonfruit sapling job"
    )
    default boolean enableDragonfruit() {
        return false;
    }

    @ConfigItem(
            keyName = "enableMaple",
            name = "Maple",
            description = "Enable Maple sapling job"
    )
    default boolean enableMaple() {
        return false;
    }

}
