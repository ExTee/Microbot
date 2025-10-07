package net.runelite.client.plugins.microbot.XTScripts.AutoProjectilePrayer;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("AutoProjectilePrayer")
public interface AutoProjectilePrayerConfig extends Config {
/*    @ConfigItem(
            keyName = "Ore",
            name = "Ore",
            description = "Choose the ore",
            position = 0
    )
    default List<String> ORE()
    {
        return Rocks.TIN;
    }*/

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

    @ConfigItem(
            keyName = "TargetNpc",
            name = "Npc to pray against:",
            description = "one npc name"
    )
    default String NpcID() {
        return "";
    }

    @ConfigItem(
            keyName = "RangedSpotAnimationIDs",
            name = "Pray ranged against the following SpotAnimationIDs:",
            description = "Comma-separated list of Spot Animation IDs"
    )
    default String RangedSpotAnimationIDs() {
        return "";
    }

    @ConfigItem(
            keyName = "MagicSpotAnimationIDs",
            name = "Pray magic against the following SpotAnimationIDs:",
            description = "Comma-separated list of Spot Animation IDs"
    )
    default String MagicSpotAnimationIDs() {
        return "";
    }
}
