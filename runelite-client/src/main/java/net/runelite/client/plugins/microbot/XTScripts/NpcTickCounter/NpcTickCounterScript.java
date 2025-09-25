package net.runelite.client.plugins.microbot.XTScripts.NpcTickCounter;

import net.runelite.api.NPC;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.plugins.microbot.util.antiban.Rs2AntibanSettings;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

enum CounterState {
    IDLE,
    COUNTING,
    PAUSED
}

public class NpcTickCounterScript extends Script {

    public static final String version = "1.0.0";
    private CounterState state = CounterState.IDLE;
    private int tickCount = 0;
    private Map<String, Integer> npcInteractionCounts = new HashMap<>();
    private List<String> trackedNpcNames;
    private long lastInteractionTime = 0;

    public boolean run(NpcTickCounterConfig config) {
        Rs2Antiban.resetAntibanSettings();
        Rs2Antiban.antibanSetupTemplates.applyGeneralBasicSetup();
        Rs2AntibanSettings.actionCooldownChance = 0.1;

        updateTrackedNpcs(config.npcNames());

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.run()) return;
                if (!Microbot.isLoggedIn()) return;
                if (Rs2AntibanSettings.actionCooldownActive) return;

                if (!config.enableCounting()) {
                    if (state == CounterState.COUNTING) {
                        state = CounterState.PAUSED;
                        Microbot.log("Tick counting paused");
                    }
                    return;
                }

                if (state == CounterState.PAUSED || state == CounterState.IDLE) {
                    state = CounterState.COUNTING;
                    Microbot.log("Tick counting started");
                }

                if (state == CounterState.COUNTING) {
                    handleGameTick(config);

                    if (config.trackInteractions()) {
                        checkNpcInteractions(config);
                    }
                }

            } catch (Exception ex) {
                Microbot.log("NpcTickCounter Script error: " + ex.getMessage());
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
        return true;
    }

    private void handleGameTick(NpcTickCounterConfig config) {
        tickCount++;

        if (config.maxTickCount() > 0 && tickCount >= config.maxTickCount()) {
            Microbot.log("Max tick count reached (" + config.maxTickCount() + "), resetting counter");
            resetCounter();
        }
    }

    private void checkNpcInteractions(NpcTickCounterConfig config) {
        if (trackedNpcNames.isEmpty()) return;

        for (NPC npc : Rs2Npc.getNpcs()) {
            if (npc != null && trackedNpcNames.contains(npc.getName())) {
                String npcName = npc.getName();

                if (npc.getInteracting() != null ||
                    (System.currentTimeMillis() - lastInteractionTime < 3000)) {

                    npcInteractionCounts.merge(npcName, 1, Integer::sum);
                    lastInteractionTime = System.currentTimeMillis();
                }
            }
        }
    }

    private void updateTrackedNpcs(String npcNamesConfig) {
        if (npcNamesConfig == null || npcNamesConfig.trim().isEmpty()) {
            trackedNpcNames = Arrays.asList();
        } else {
            trackedNpcNames = Arrays.stream(npcNamesConfig.split(","))
                    .map(String::trim)
                    .filter(name -> !name.isEmpty())
                    .collect(Collectors.toList());
        }
    }

    public void resetCounter() {
        tickCount = 0;
        npcInteractionCounts.clear();
        Microbot.log("Tick counter and NPC interactions reset");
    }

    public void resetCounterByAnimation(int animationId) {
        tickCount = 0;
        npcInteractionCounts.clear();
        Microbot.log("Tick counter reset by animation ID: " + animationId);
    }

    public int getTickCount() {
        return tickCount;
    }

    public Map<String, Integer> getNpcInteractionCounts() {
        return new HashMap<>(npcInteractionCounts);
    }

    public CounterState getState() {
        return state;
    }

    @Override
    public void shutdown() {
        super.shutdown();
        state = CounterState.IDLE;
        Microbot.log("NpcTickCounter shutdown");
    }
}