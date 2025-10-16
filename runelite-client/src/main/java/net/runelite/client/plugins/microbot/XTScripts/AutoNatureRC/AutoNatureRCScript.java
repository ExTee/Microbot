package net.runelite.client.plugins.microbot.XTScripts.AutoNatureRC;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.globval.enums.InterfaceTab;
import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.plugins.microbot.util.antiban.Rs2AntibanSettings;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2ItemModel;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;


import java.awt.*;
import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.XTScripts.AutoNatureRC.Entities.*;

public class AutoNatureRCScript extends Script {

    public static final String version = "1.0.0";
    private State state = State.STARTUP;
    private long lastActionTime = 0;

    public boolean run(AutoNatureRCConfig config) {



        state = State.BANKING;
        Rs2Antiban.resetAntibanSettings();
        Rs2Antiban.antibanSetupTemplates.applyGeneralBasicSetup();
        Rs2AntibanSettings.actionCooldownChance = 0.1;

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.run()) return;
                if (!Microbot.isLoggedIn()) return;
                if (Rs2AntibanSettings.actionCooldownActive) return;

                long currentTime = System.currentTimeMillis();

                Microbot.log(String.valueOf(state));
                switch (state) {
                    case STARTUP:
                        state = State.BANKING;
                        break;

                    case BANKING:
                        bank();
                        state = State.TELEPORT_TO_KARAMJA;
                        break;

                    case TELEPORT_TO_KARAMJA:
                        teleportToKaramja();
                        state = State.ROCK_SHORTCUT;
                        break;

                    case ROCK_SHORTCUT:
                        climbRocks();
                        state = State.ENTER_RUINS;
                        break;

                    case ENTER_RUINS:
                        enterRuins();
                        state = State.CRAFT_RUNES;
                        break;

                    case CRAFT_RUNES:
                        craftRunes();
                        state = State.TELEPORT_TO_CRAFTING_GUILD;
                        break;

                    case TELEPORT_TO_CRAFTING_GUILD:
                        teleportToCraftingGuild();
                        state = State.BANKING;
                        break;

                    case FINISHED:
                        Microbot.log("AutoNatureRC plugin finished!");
                        state = State.FINISHED;
                        shutdown();
                        break;
                }

            } catch (Exception ex) {
                Microbot.log("AutoNatureRC Script error: " + ex.getMessage());
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
        return true;
    }



    private void teleportToCraftingGuild(){
        Rs2Tab.switchTo(InterfaceTab.EQUIPMENT);
//        Rs2Equipment.invokeMenu(Rs2Equipment.get(MAX_CAPE), "Crafting Guild");
        Rs2Equipment.interact("Max cape", "Crafting Guild");
//        sleepUntil(() -> Rs2Player.isInArea(CRAFTING_GUILD_POINT, 5));
        Rs2Tab.switchTo(InterfaceTab.INVENTORY);
        sleepUntil(() -> Rs2GameObject.exists(CRAFTING_GULID_BANK));

    }
    private void pouchDelay(){
//        sleep(800, 1000);
        sleep(300,400);
    }
    private void bank(){
        Rs2GameObject.interact("Bank chest", "Use");
        sleepUntil(Rs2Bank::isOpen);

        if (Rs2Inventory.contains("Nature rune")){
            Microbot.log("Depositing nature runes");
            Rs2Bank.depositAll("Nature rune");
        }

        Microbot.log("Withdrawing pure essence 1/3");
        Rs2Bank.withdrawAll("Pure essence");
        sleepUntil(Rs2Inventory::isFull);
        Rs2Inventory.fillPouches();

        pouchDelay();

        Microbot.log("Withdrawing pure essence 2/3");
        Rs2Bank.withdrawAll("Pure essence");
        sleepUntil(Rs2Inventory::isFull);
        Rs2Inventory.fillPouches();

        pouchDelay();

        Microbot.log("Withdrawing pure essence 3/3");
        Rs2Bank.withdrawAll("Pure essence");

//        pouchDelay();
        sleep(200,350);
        sleepUntil(Rs2Bank::closeBank);
    }

    private void teleportToKaramja(){
        Rectangle bounds = Rs2Inventory.itemBounds(Rs2Inventory.get(ACHIEVEMENT_CAPE));
        Microbot.getMouse().click(bounds);
        sleepUntil(() -> Rs2Player.isInArea(SHILO_TELEPORT_POINT, 10));
        Rs2GameObject.interact(SHILO_LADDER, "Climb-down");
        sleepUntil(() -> Rs2GameObject.exists(SHILO_ROCKS));
    }

    private void climbRocks(){
        Rs2GameObject.interact(SHILO_ROCKS, "Climb");
        sleepUntil(() -> (Rs2Player.getWorldLocation() == SHILO_AFTER_ROCKS_POINT));
//        sleepUntilTick(3);
        sleepGaussian(2000, 200);
    }

    private void enterRuins(){
        Rs2GameObject.interact(NATURE_RUINS, "Enter");
        sleep(2000);
        if (!Rs2Player.isMoving()){
            Rs2GameObject.interact(NATURE_RUINS, "Enter");
        }
        sleepUntil(() -> Rs2Player.isInArea(INSIDE_NATURE_ALTAR_POINT, 1));
    }

    private void craftRunes(){
        Rs2GameObject.interact(NATURE_ALTAR, "Smart Craft-rune");
        sleepUntil(()-> Rs2Inventory.hasItemAmount("Nature rune", 210));
//        sleepUntilTick(1);
    }

    @Override
    public void shutdown() {
        super.shutdown();
        state = State.FINISHED;
    }
}

enum State {
    STARTUP,
    TELEPORT_TO_CRAFTING_GUILD,
    BANKING,
    TELEPORT_TO_KARAMJA,
    ROCK_SHORTCUT,
    ENTER_RUINS,
    CRAFT_RUNES,
    FINISHED
}
