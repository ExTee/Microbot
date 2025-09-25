package net.runelite.client.plugins.microbot.XTScripts.AutoHerbRun;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigDescriptor;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.pluginscheduler.api.SchedulablePlugin;
import net.runelite.client.plugins.microbot.pluginscheduler.condition.logical.AndCondition;
import net.runelite.client.plugins.microbot.pluginscheduler.condition.logical.LockCondition;
import net.runelite.client.plugins.microbot.pluginscheduler.condition.logical.LogicalCondition;
import net.runelite.client.plugins.microbot.pluginscheduler.event.PluginScheduleEntryPostScheduleTaskEvent;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@Slf4j
@PluginDescriptor(
        name = "<html>[<font color=#ff69b4>\uD83D\uDC30</font>] " + "AutoHerbRun",
        description = "Herb runner",
        tags = {"herb", "farming", "money making", "skilling"},
        enabledByDefault = false
)
public class AutoHerbrunPlugin extends Plugin implements SchedulablePlugin{
    @Inject
    private AutoHerbrunConfig config;
    @Provides
    AutoHerbrunConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoHerbrunConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AutoHerbrunOverlay AutoHerbrunOverlay;

    @Inject
    AutoHerbrunScript autoHerbrunScript;

    static String status;
    private LockCondition lockCondition;
    private LogicalCondition stopCondition = null;
    

    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(AutoHerbrunOverlay);
        }
        autoHerbrunScript.run();
    }

    protected void shutDown() {
        autoHerbrunScript.shutdown();
        overlayManager.remove(AutoHerbrunOverlay);
//        Microbot.stopPlugin(this);
        status = null; // Reset status on shutdown
    }

    @Subscribe
    public void onPluginScheduleEntryPostScheduleTaskEvent(PluginScheduleEntryPostScheduleTaskEvent event) {
        try {
            if (event.getPlugin() == this) {
                // Check if lock is active before stopping
                if (lockCondition != null && lockCondition.isLocked()) {
                    log.info("Soft stop deferred - plugin is locked: {}", lockCondition.getReason());
                    // Defer the stop operation to respect the lock
                    Microbot.getClientThread().invokeLater(() -> {
                        // Re-check lock state when invokeLater executes
                        if (lockCondition == null || !lockCondition.isLocked()) {
                            log.info("Lock released, proceeding with deferred stop");
                            Microbot.stopPlugin(this);
                        } else {
                            log.warn("Lock still active, stop operation cancelled");
                        }
                        return true;
                    });
                } else {
                    log.info("Stopping plugin immediately - no lock active");
                    Microbot.stopPlugin(this);
                }
            }
        } catch (Exception e) {
            log.error("Error stopping plugin: ", e);
        }
    }

    @Override
    public LogicalCondition getStopCondition() {
        if (this.stopCondition == null) {
            this.lockCondition = new LockCondition("Herb run in progress");
            AndCondition andCondition = new AndCondition();
            andCondition.addCondition(lockCondition);
            this.stopCondition = andCondition;
        }
        return this.stopCondition;
    }
    @Override
    public ConfigDescriptor getConfigDescriptor() {
        if (Microbot.getConfigManager() == null) {
            return null;
        }
        AutoHerbrunConfig conf = Microbot.getConfigManager().getConfig(AutoHerbrunConfig.class);
        return Microbot.getConfigManager().getConfigDescriptor(conf);
    }

}
