package net.runelite.client.plugins.microbot.XTScripts.ExamplePlugin;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.plugins.microbot.util.antiban.Rs2AntibanSettings;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.util.concurrent.TimeUnit;

enum ExampleState {
    IDLE,
    WORKING,
    FINISHED
}

public class ExampleScript extends Script {

    public static final String version = "1.0.0";
    private ExampleState state = ExampleState.IDLE;
    private long lastActionTime = 0;

    public boolean run(ExampleConfig config) {
        Rs2Antiban.resetAntibanSettings();
        Rs2Antiban.antibanSetupTemplates.applyGeneralBasicSetup();
        Rs2AntibanSettings.actionCooldownChance = 0.1;

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.run()) return;
                if (!Microbot.isLoggedIn()) return;
                if (Rs2AntibanSettings.actionCooldownActive) return;

                long currentTime = System.currentTimeMillis();

                switch (state) {
                    case IDLE:
                        if (config.exampleSetting()) {
                            Microbot.log("Example plugin starting...");
                            state = ExampleState.WORKING;
                            lastActionTime = currentTime;
                        }
                        break;

                    case WORKING:
                        if (currentTime - lastActionTime > config.exampleNumber() * 1000) {
                            Microbot.log("Example action performed with setting: " + config.exampleText());
                            lastActionTime = currentTime;

                            if (Math.random() < 0.1) {
                                state = ExampleState.FINISHED;
                            }
                        }
                        break;

                    case FINISHED:
                        Microbot.log("Example plugin finished!");
                        state = ExampleState.IDLE;
                        break;
                }

            } catch (Exception ex) {
                Microbot.log("Example Script error: " + ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
        state = ExampleState.IDLE;
    }
}