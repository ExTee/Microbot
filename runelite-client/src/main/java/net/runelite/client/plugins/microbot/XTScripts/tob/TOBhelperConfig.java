package net.runelite.client.plugins.microbot.XTScripts.tob;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("TOBhelperConfig")
public interface TOBhelperConfig extends Config {

    @ConfigItem(
            keyName = "RangedProjectileIDs",
            name = "Pray ranged against:",
            description = "Comma-separated list of projectile IDs"
    )
    default String RANGED_PROJECTILE_IDS() {
        return "";
    }
    @ConfigItem(
            keyName = "MagicProjectileIDs",
            name = "Pray magic against:",
            description = "Comma-separated list of projectile IDs"
    )
    default String MAGIC_PROJECTILE_IDS() {
        return "";
    }

    @ConfigItem(
            keyName = "MeleeProjectileIDs",
            name = "Pray melee against:",
            description = "Comma-separated list of projectile IDs"
    )
    default String MELEE_PROJECTILE_IDS() {
        return "";
    }
}
