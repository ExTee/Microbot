package net.runelite.client.plugins.microbot.XTScripts.ShipwreckSalvaging;

import net.runelite.api.DynamicObject;
import net.runelite.api.NPC;
import net.runelite.api.ScriptID;
import net.runelite.api.VarClientInt;
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
}


public class ShipwreckSalvagingScript extends Script {

    State state;

    public static final String SALVAGE= "Fremennik salvage";

    public static final int SALVAGING_HOOK_RUNE = 60495;
    public static final int SAILING_SALVAGING_STATION_3X8 = 59701;
    public static final int SAILING_CRYSTAL_EXTRACTOR_ACTIVATED = 59702;
    public static final int SAILING_CRYSTAL_EXTRACTOR_DEACTIVATED = 59703;

    public static final int SAILING_FREMENNIK_SHIPWRECK = 60476;
    public static final int SAILING_FREMENNIK_SHIPWRECK_STUMP = 60477;

    public static final String[] ALCH_ITEMS = {
        "Fremennik helm",
        "Berserker helm",
        "Archer helm",
        "Farseer helm",
        "Warrior helm",
    };

    public static final String[] KEPT_ITEMS = {
        SALVAGE,
        "Coins",
        "Divine rune pouch",
        "Crystal shard",
        "Astral rune",
        "Adamantite nails",
        "Rune nails",
        "Rune cannonball",
        "Cotton seed",
        "Fremennik helm",
        "Berserker helm",
        "Archer helm",
        "Farseer helm",
        "Warrior helm",
        "Ironwood seed",
        "Smashed mirror",
        "Rosewood seed",
        "Sailors' amulet (inert)"
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
                            if (state != State.CRYSTAL){state = State.RETRIEVE;}
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
                        if (state != State.CRYSTAL){state = State.IDLE;}
                        break;
                    case CRYSTAL:
                        harvestCrystalExtractor();
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
            sleep(3*60*1000 + 2000);
//            sleep(30000);
        }
    }
    public void useSalvagingStation(){
        if (!Rs2Player.isAnimating()){
            Rs2GameObject.interact(SAILING_SALVAGING_STATION_3X8, "Sort-salvage");
            sleep(2000);
            sleepUntil(() -> (!Rs2Inventory.contains(SALVAGE) | !Rs2Player.isAnimating()), 2*60*1000);
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
        sleep(2000);
        sleepUntil(()-> !Rs2Player.isAnimating());
        sleep(1200);
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