package net.runelite.client.plugins.microbot.XTScripts.ShipwreckSalvaging;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.antiban.Rs2AntibanSettings;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2ObjectModel;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2ItemModel;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.magic.Rs2Magic;
import net.runelite.client.plugins.microbot.util.mouse.Mouse;
import net.runelite.client.plugins.microbot.util.mouse.naturalmouse.api.MouseMotion;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.sailing.Rs2Sailing;
import net.runelite.client.plugins.microbot.util.security.LoginManager;
import net.runelite.client.plugins.microbot.util.tileobject.Rs2TileObjectApi;
import net.runelite.client.plugins.microbot.util.tileobject.Rs2TileObjectModel;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static net.runelite.api.widgets.ComponentID.BANK_ITEM_CONTAINER;
import static net.runelite.client.plugins.microbot.util.player.Rs2Player.isMember;

enum State {
    IDLE,
    SETUP,
    CRYSTAL,
    RETRIEVE,
    SALVAGING,
    SORTING,
    ALCH_DROP,
    WORLDHOP,
    SELF_SALVAGE,
}


public class ShipwreckSalvagingScript extends Script {

    State state;

    public static final String SALVAGE= "Opulent salvage";

    public static final int SALVAGING_HOOK_RUNE_RIGHT = 60507;
    public static final int SALVAGING_HOOK_RUNE_LEFT = 60508;
    public static final int SAILING_SALVAGING_STATION_3X8 = 59701;
    public static final int SAILING_CRYSTAL_EXTRACTOR_ACTIVATED = 59702;
    public static final int SAILING_CRYSTAL_EXTRACTOR_DEACTIVATED = 59703;

    public static final int SAILING_MERCHANT_SHIPWRECK = 60478;
    public static final int SAILING_MERCHANT_SHIPWRECK_STUMP = 60479;

    public static final String[] ALCH_ITEMS = {
        "Rune spear",
        "Dragon spear",
        "Shield left half"
    };

    public static final String[] KEPT_ITEMS = {
        SALVAGE,
        "Coins",
        "Divine rune pouch",
        "Crystal shard",
        "Dragon nails",
        "Mouldy doll",
        "Dragon cannon barrel",
        "Sailor's amulet (inert)",
        "Rune cannonball",
        "Dragon cannonball",
        "Snapdragon seed",
        "Snape grass seed",
        "Torstol seed",
        "Platinum token",
        "Loop half of key",
        "Tooth half of key",
        "Crystal key"
    };


    public boolean run(ShipwreckSalvagingConfig config) {

        state = State.SETUP;

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                Microbot.log("Current State : " + String.valueOf(state));
                if (!super.run()) return;
                if (!Microbot.isLoggedIn()) return;
                if (Rs2AntibanSettings.actionCooldownActive) return;

                switch (state) {
                    case SETUP:
                        if (activateCrystalExtractor()){
                            openSailingMenu();
                            scrollDown();
                            assignCrew();
                            Rs2Inventory.open();
                        }
                        if (state != State.CRYSTAL){
                            state = State.IDLE;
                        }
                        break;
                    case IDLE:
                        if (Rs2Inventory.isFull()){
                            if (Rs2Inventory.contains(SALVAGE)){
                                if (state != State.CRYSTAL){state = State.SORTING;}
                            }
                            else{
                                if (state != State.CRYSTAL){state = State.ALCH_DROP;}
                            }
                        }
                        else{
                            //TODO
                            if (state != State.CRYSTAL){
                                state = State.SELF_SALVAGE;

                                if(Rs2Widget.getWidget(15007747).getText().equals("This shipwreck has already been plundered. Perhaps there's another<br>one nearby?")){
                                    state = State.WORLDHOP;
                                }

                            }
                        }
                        break;
                    case RETRIEVE:
                        withdrawSalvage();
                        if (state != State.CRYSTAL){state = State.IDLE;}
                        break;
                    case SORTING:
                        useSalvagingStation();
                        if (state != State.CRYSTAL){state = State.ALCH_DROP;}
                        break;
                    case ALCH_DROP:
                        handleAlchAndDrop();
                        if (state != State.CRYSTAL){state = State.RETRIEVE;}
                        break;
                    case CRYSTAL:
                        harvestCrystalExtractor();
                        state = State.IDLE;
                        break;
                    case SELF_SALVAGE:
                        selfSalvagingHook();
                        if (state != State.CRYSTAL){state = State.IDLE;}
                        break;
                    case WORLDHOP:
                        sleep(60000);
                        state = State.IDLE;
                        break;

                }
            } catch (Exception ex) {
                Microbot.log("Example Script error: " + ex.getMessage());
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
        return true;
    }

    public void withdrawSalvage(){
//        Rs2Sailing.openCargo();
        Rs2GameObject.interact(60283, "Open");
        sleepUntil(() -> Rs2Widget.isWidgetVisible(164,16));
        sleep(1000);

//        Widget salvage = Rs2Widget.getWidget(943, 10).getChild(0);
        Widget salvage = Rs2Widget.getWidget(61800458).getChild(0);
        if (salvage.getItemQuantity() > 50){
            Microbot.getMouse().move(salvage.getBounds());
            Microbot.getMouse().click();
            sleep(600);
            Microbot.getMouse().click();
            sleep(1000);
            Rs2Keyboard.keyPress(KeyEvent.VK_ESCAPE);
        }
        else{
            Rs2Keyboard.keyPress(KeyEvent.VK_ESCAPE);
//            sleep(3*60*1000 + 2000);
//            sleep(1000);
//            sleep(30000);
        }
    }

    public void selfSalvagingHook(){

        if (!Rs2Player.isAnimating()){
            Rs2GameObject.interact(SALVAGING_HOOK_RUNE_LEFT, "Deploy");
            sleep(2000);
        }
        sleepUntil(() -> !Rs2Player.isAnimating());
    }



    public void useSalvagingStation(){
        if (!Rs2Player.isAnimating()){
            Rs2GameObject.interact(SAILING_SALVAGING_STATION_3X8, "Sort-salvage");
            sleep(2000);


            if (Rs2Npc.getNpc(15344).getAnimation() == -1){

                openSailingMenu();
                scrollDown();
                Widget SALVAGING_HOOK_LEFT_MENU = Rs2Widget.getWidget(937, 25).getChild(47);
                Rs2Widget.clickWidget(SALVAGING_HOOK_LEFT_MENU);
                sleep(1200);
                Widget CREW_ASSIGNATION_ROW_CABIN_BOY = Rs2Widget.getWidget(937, 20).getChild(2);
                Rs2Widget.clickWidget(CREW_ASSIGNATION_ROW_CABIN_BOY);
            }
            sleep(5000);
            sleepUntil(() -> (!Rs2Inventory.contains(SALVAGE) | !Rs2Player.isAnimating(1200)), 2*60*1000);
        }
        else{
            sleepUntil(() -> (!Rs2Inventory.contains(SALVAGE) | !Rs2Player.isAnimating(1200)), 2*60*1000);
        }
    }

    public boolean activateCrystalExtractor(){
        sleep(600,1000);
        if (Rs2GameObject.interact(SAILING_CRYSTAL_EXTRACTOR_DEACTIVATED, "Activate")){
            sleep(1000);
            return true;
        }
        return false;
    }

    public void harvestCrystalExtractor(){
        sleep(600,1000);
        Rs2GameObject.interact(SAILING_CRYSTAL_EXTRACTOR_ACTIVATED, "Harvest");
        sleep(1200);
        sleepUntil(()-> !Rs2Player.isAnimating(), 2000);
//        sleep(1200);
        if (!Rs2Inventory.isFull()) {
            Rs2GameObject.interact(SALVAGING_HOOK_RUNE_LEFT, "Deploy");
            sleep(2000);
        }
    }

    public void handleAlchAndDrop(){

        for ( Rs2ItemModel item : Rs2Inventory.items().collect(Collectors.toList())){
            String itemName = item.getName();
            if (Arrays.stream(ALCH_ITEMS).anyMatch(s -> s.equalsIgnoreCase(itemName))){
                Rs2Magic.alch(itemName, 1200,1800);
                Microbot.log("Alching " + itemName);
            }
        }

        Rs2Inventory.dropAllExcept(KEPT_ITEMS);

    }

    public void hopWorld(){
        int randomWorld = LoginManager.getRandomWorld(isMember());
        Microbot.hopToWorld(randomWorld);
    }

    public void openSailingMenu(){
        sleep(1200);
        Widget SAILING_MENU = Rs2Widget.getWidget(164,73);
        Widget SAILING_ICON = Rs2Widget.getWidget(164,52);

        if (SAILING_ICON.getSpriteId() == -1){
            Rs2Widget.clickWidget(SAILING_ICON);
        }

        sleep(1200);
    }

    public void scrollDown() {
        int scrollY = 45;
        Widget w = Microbot.getClient().getWidget(937, 32);
        if (w == null) return;

        Microbot.getClientThread().invoke(() -> {
            Microbot.getClient().runScript(ScriptID.UPDATE_SCROLLBAR, 61407264, 61407256, scrollY);
        });
        w.setScrollY(scrollY);
    }


    public void assignCrew(){

        sleep(699);

        Widget SALVAGING_HOOK_RIGHT_MENU = Rs2Widget.getWidget(937, 25).getChild(43);
        Widget SALVAGING_HOOK_LEFT_MENU = Rs2Widget.getWidget(937, 25).getChild(47);

        Rs2Widget.clickWidget(SALVAGING_HOOK_RIGHT_MENU);
        sleep(1200);
        Widget CREW_ASSIGNATION_ROW_CAPTAIN_SIAD = Rs2Widget.getWidget(937, 20).getChild(1);
        Rs2Widget.clickWidget(CREW_ASSIGNATION_ROW_CAPTAIN_SIAD);

        sleep(1200);

        Rs2Widget.clickWidget(SALVAGING_HOOK_LEFT_MENU);
        sleep(1200);
        Widget CREW_ASSIGNATION_ROW_CABIN_BOY = Rs2Widget.getWidget(937, 20).getChild(2);
        Rs2Widget.clickWidget(CREW_ASSIGNATION_ROW_CABIN_BOY);

        sleep(1200);
    }


    @Override
    public void shutdown() {
        super.shutdown();
//        if (mainScheduledFuture != null && !mainScheduledFuture.isDone()) {
//
//            mainScheduledFuture.cancel(true); // <----- this will stop the thread from running
//
//        }
////        state = State.IDLE;
    }
}