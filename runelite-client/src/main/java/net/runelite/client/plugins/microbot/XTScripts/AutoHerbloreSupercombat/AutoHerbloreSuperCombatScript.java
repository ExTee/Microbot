package net.runelite.client.plugins.microbot.XTScripts.AutoHerbloreSupercombat;

import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;

public class AutoHerbloreSuperCombatScript extends Script {

    private final String[] ingredients = new String[]{"Torstol", "Super strength(4)", "Super attack(4)", "Super defence(4)"};

    public boolean run() {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;

                if (!Rs2Equipment.isWearing("Prescription goggles")){
                    shutdown();
                }


                if (Rs2Bank.isOpen()){
                    for (String ingredient : ingredients){
                        if (!Rs2Bank.hasItem(ingredient)){
                            shutdown();
                        }
                    }
                }


                if (!Rs2Inventory.containsAll(ingredients) & Rs2Bank.isOpen()){
                    withdrawSuppliesAndCloseBank();
                    return;
                }

                if (Rs2Inventory.containsAll(ingredients) & !Rs2Bank.isOpen()){
                    combineItems();
                    return;
                }

                if (!Rs2Inventory.containsAll(ingredients) & !Rs2Bank.isOpen()){
                    openBankAndDepositAll();
                    return;
                }




            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
        return true;
    }
    
    @Override
    public void shutdown() {
        super.shutdown();
    }

    private void openBankAndDepositAll(){
        sleepUntil(Rs2Bank::openBank, 20000);
//        sleep(1000,1300);
//        sleep(500,800);
        Rs2Bank.depositAll();
//        sleepUntil(Rs2Inventory::isEmpty);
        sleepGaussian(200,20);
    }

    private void withdrawSuppliesAndCloseBank(){

        for (String ingredient : ingredients){
            Rs2Bank.withdrawX(ingredient, 7);
            sleepGaussian(100,20);
        }

        sleepUntil(Rs2Bank::closeBank, 20000);
    }

    private void combineItems(){
        Rs2Inventory.combine(ingredients[0], ingredients[1]);
        sleepUntil(() -> Rs2Widget.getWidget(17694734) != null, 5000);
        sleepGaussian(200, 50);
        Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);

        sleepUntil(() -> !Rs2Inventory.contains(ingredients[1]), 20000);
    }
}