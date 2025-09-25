package net.runelite.client.plugins.microbot.XTScripts.AutoProjectilePrayer;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Projectile;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer;
import net.runelite.client.plugins.microbot.util.prayer.Rs2PrayerEnum;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;
import java.util.Arrays;

@PluginDescriptor(
        name = "AutoProjectilePrayer",
        description = "Highlights projectiles",
        tags = {"XT"},
        enabledByDefault = false
)
@Slf4j
public class AutoProjectilePrayerPlugin extends Plugin {
    @Inject
    private AutoProjectilePrayerConfig config;
    @Provides
    AutoProjectilePrayerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoProjectilePrayerConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AutoProjectilePrayerOverlay autoProjectilePrayerOverlay;

//    @Inject
//    ProjectileHighlighterScript projectileHighlighterScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(autoProjectilePrayerOverlay);
        }

        rangedProjectileIDs = parseStringToIntArray(config.RANGED_PROJECTILE_IDS());
        magicProjectileIDs = parseStringToIntArray(config.MAGIC_PROJECTILE_IDS());
        meleeProjectileIDs = parseStringToIntArray(config.MELEE_PROJECTILE_IDS());
    }

    protected void shutDown() {
        overlayManager.remove(autoProjectilePrayerOverlay);
    }

    enum PROJECTILE_TYPE{
        NONE_PROJECTILE,
        RANGED_PROJECTILE,
        MAGIC_PROJECTILE,
        MELEE_PROJECTILE
    }

    public static int[] parseStringToIntArray(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new int[0];
        }

        String[] parts = input.split(",");
        int[] result = new int[parts.length];

        for (int i = 0; i < parts.length; i++) {
            result[i] = Integer.parseInt(parts[i].trim());
        }

        return result;
    }

    public static boolean foundInArray(int id, int[] arr){
        return Arrays.stream(arr).anyMatch(x -> x == id);
    }

    int[] rangedProjectileIDs;
    int[] magicProjectileIDs;
    int[] meleeProjectileIDs;



    PROJECTILE_TYPE activeProjectile = PROJECTILE_TYPE.NONE_PROJECTILE;

    @Subscribe
    public void onGameTick(GameTick tick)
    {
        System.out.println("Protecting against Ranged for: " + Arrays.toString(rangedProjectileIDs));

        activeProjectile = PROJECTILE_TYPE.NONE_PROJECTILE;

        for (Projectile p : Microbot.getClient().getProjectiles()) {
            System.out.println("Projectile ID: " + p.getId());

            if (foundInArray(p.getId(), rangedProjectileIDs)){
                activeProjectile = PROJECTILE_TYPE.RANGED_PROJECTILE;
            }
            if (foundInArray(p.getId(), magicProjectileIDs)){
                activeProjectile = PROJECTILE_TYPE.MAGIC_PROJECTILE;
            }
            if (foundInArray(p.getId(), meleeProjectileIDs)){
                activeProjectile = PROJECTILE_TYPE.MELEE_PROJECTILE;
            }

        }
        switch(activeProjectile){
            case NONE_PROJECTILE:
//                Rs2Prayer.toggle(Rs2Prayer.getActiveProtectionPrayer(), false);
                Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MELEE, true);
                break;
            case RANGED_PROJECTILE:
                Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_RANGE, true);
                break;
            case MAGIC_PROJECTILE:
                Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MAGIC, true);
                break;
            case MELEE_PROJECTILE:
                Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MELEE, true);
                break;
        }
    }

}
