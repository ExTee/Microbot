package net.runelite.client.plugins.microbot.XTScripts.AutoSaplings;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.magic.Rs2Magic;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.skillcalculator.skills.MagicAction;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

enum State {
    SETUP,
    UPDATE_JOB,
    RETRIEVE_SEED,
    RETRIEVE_FILLED_PLANT_POTS,
    SAPLING_CREATION,
    HUMIDIFY,
    DEPOSIT_SAPLINGS,
}



public class AutoSaplingsScript extends Script {

    public static final String ASTRAL_RUNE = "Astral rune";
    public static final String MYSTIC_STEAM_STAFF = "Mystic steam staff";
    public static final String GARDENING_TROWEL = "Gardening trowel";
    public static final String FILLED_PLANT_POT = "Filled plant pot";

    public Queue<SaplingJob> createJobQueue(AutoSaplingsConfig config){


        final SaplingJob yewSaplingJob = new SaplingJob(
                "Yew",
                "Yew seed",
                "Yew seedling",
                "Yew seedling (w)",
                "Yew sapling"
        );

        final SaplingJob palmSaplingJob = new SaplingJob(
                "Palm",
                "Palm tree seed",
                "Palm seedling",
                "Palm seedling (w)",
                "Palm sapling"
        );

        final SaplingJob magicSaplingJob = new SaplingJob(
                "Magic",
                "Magic seed",
                "Magic seedling",
                "Magic seedling (w)",
                "Magic sapling"
        );

        final SaplingJob celastrusSaplingJob = new SaplingJob(
                "Celastrus",
                "Celastrus seed",
                "Celastrus seedling",
                "Celastrus seedling (w)",
                "Celastrus sapling"
        );

        final SaplingJob dragonfruitSaplingJob = new SaplingJob(
                "Dragonfruit",
                "Dragonfruit tree seed",
                "Dragonfruit seedling",
                "Dragonfruit seedling (w)",
                "Dragonfruit sapling"
        );

        Queue<SaplingJob> jobQueue = new LinkedList<SaplingJob>();

        if (config.enableYew()) {
            jobQueue.add(yewSaplingJob);
        }

        if (config.enablePalm()) {
            jobQueue.add(palmSaplingJob);
        }

        if (config.enableMagic()) {
            jobQueue.add(magicSaplingJob);
        }

        if (config.enableCelastrus()) {
            jobQueue.add(celastrusSaplingJob);
        }

        if (config.enableDragonfruit()) {
            jobQueue.add(dragonfruitSaplingJob);
        }
        return jobQueue;
    };

    public void setup(){
        // Open Bank
        sleepUntil(Rs2Bank::openBank, 20000);

        Rs2Bank.depositEquipment();
        sleep(1000,1300);
        Rs2Bank.depositAll();
        sleep(1000,1300);


        // Withdraw staff, equip it
        if (!Rs2Equipment.isWearing(MYSTIC_STEAM_STAFF)) {
            Rs2Bank.withdrawAndEquip(MYSTIC_STEAM_STAFF);
            sleepUntil(() -> Rs2Equipment.isWearing(MYSTIC_STEAM_STAFF), 20000);
        }

        // Withdraw seed, astral runes, trowel
        if (!Rs2Inventory.contains(ASTRAL_RUNE)){
            Rs2Bank.withdrawAll(ASTRAL_RUNE);
            sleepUntil(() -> Rs2Inventory.contains(ASTRAL_RUNE));
        }
        if (!Rs2Inventory.contains(GARDENING_TROWEL)){
            Rs2Bank.withdrawOne(GARDENING_TROWEL);
            sleepUntil(() -> Rs2Inventory.contains(GARDENING_TROWEL));
        }

    }

    public void retrieveSeed(SaplingJob currentJob){
        String seed = currentJob.getSEED();
        if (Rs2Bank.hasItem(seed, true)){
            Rs2Bank.withdrawAll(seed);
            sleepUntil(() -> Rs2Inventory.hasItem(seed, true));

            Rs2Inventory.moveItemToSlot(Rs2Inventory.get(seed), 27);
            sleep(1000);
        }
        else{
            Microbot.stopPlugin(AutoSaplingsPlugin.class);
            shutdown();
        }
    }

    public void createSaplings(SaplingJob currentJob){

        if (Rs2Inventory.contains(currentJob.getSEED())){
            if (!Rs2Inventory.contains(FILLED_PLANT_POT) || (Rs2Inventory.emptySlotCount() > 0)){
                Rs2Bank.withdrawAll(FILLED_PLANT_POT);
                sleepUntil(() -> Rs2Inventory.contains(FILLED_PLANT_POT));
            }

        } else {
            Microbot.stopPlugin(AutoSaplingsPlugin.class);
            shutdown();
        }
        sleepUntil(Rs2Bank::closeBank, 20000);
        Rs2Inventory.open();
        sleepUntil(Rs2Inventory::isOpen, 20000);

        // Combine items in inventory
        while(Rs2Inventory.containsAll(FILLED_PLANT_POT, ASTRAL_RUNE, GARDENING_TROWEL) && Rs2Inventory.contains(currentJob.getSEED())){
            Rs2Inventory.slotInteract(26, "Use");
            Rs2Inventory.slotInteract(27);
        }
    }

    public void castHumidify(SaplingJob currentJob){
        // 4. Cast humidify

        // If seedlings present
        boolean inventoryContainsSeedling = Rs2Inventory.contains(currentJob.getSEEDLING_UNWATERED());
        if (inventoryContainsSeedling){
            // Cast humidify
            Rs2Magic.cast(MagicAction.HUMIDIFY);
            sleep(1000,1200);
            sleepUntil(() -> !Rs2Player.isAnimating());

        }

        Rs2Inventory.open();
    }

    public void depositSaplings(SaplingJob currentJob){
        String seedling_watered = currentJob.getSEEDLING_WATERED();
        String sapling = currentJob.getSAPLING();

        sleepUntil(Rs2Bank::openBank, 20000);

        if (Rs2Inventory.contains(seedling_watered)){
            Rs2Bank.depositAll(seedling_watered);
        }
        if (Rs2Inventory.contains(sapling)){
            Rs2Bank.depositAll(sapling);
        }
        sleep(500,1200);
    }

    public void updateJob(){
        if (currentJob == null){
            if (jobQueue.isEmpty()){
                // Stop script if no jobs in queue and no active jobs
                System.out.println("Job Queue is empty. Stopping Script ... ");
                Microbot.stopPlugin(AutoSaplingsPlugin.class);
                shutdown();
            }
            else{
                currentJob = jobQueue.poll();
                System.out.println("Currently Processing : " + currentJob);
            }
        }

    }

    State state = null;
    Queue<SaplingJob> jobQueue = null;
    SaplingJob currentJob = null;

    public boolean run(AutoSaplingsConfig config) {

        Microbot.log("== Sapling Creation Script loaded ==");

        state = State.SETUP;
        currentJob = null;
        jobQueue = null;


        // Create job queue
        jobQueue = createJobQueue(config);
        System.out.print("Jobs in the queue: ");
        for (SaplingJob job : jobQueue) {
            System.out.print(job + " , ");
        }
        System.out.println();


        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            System.out.println("Current State: " + state);
            switch (state){
                case SETUP:
                    Microbot.log("State : SETUP");
                    setup();
                    state = State.UPDATE_JOB;
                    break;
                case UPDATE_JOB:
                    Microbot.log("State : UPDATE_JOB");
                    updateJob();
                    Microbot.log("Updating job. Current Job : " + currentJob);
                    state = State.RETRIEVE_SEED;
                    break;
                case RETRIEVE_SEED:
                    Microbot.log("State : RETRIEVE_SEED");
                    retrieveSeed(currentJob);
                    state = State.SAPLING_CREATION;
                    break;
                case SAPLING_CREATION:
                    Microbot.log("State : SAPLING_CREATION");
                    createSaplings(currentJob);
                    state = State.HUMIDIFY;
                    break;
                case HUMIDIFY:
                    Microbot.log("State : HUMIDIFY");
                    castHumidify(currentJob);
                    state = State.DEPOSIT_SAPLINGS;
                    break;
                case DEPOSIT_SAPLINGS:
                    Microbot.log("State : DEPOSIT_SAPLINGS");
                    depositSaplings(currentJob);

                    if (Rs2Inventory.contains(currentJob.getSEED())){
                        state = State.SAPLING_CREATION;
                    }
                    else {
                        currentJob = null;
                        state = State.UPDATE_JOB;
                    }
                    break;

            }
        }, 0, 100, TimeUnit.MILLISECONDS);
        return true;
    }
    
    @Override
    public void shutdown() {
        super.shutdown();
    }
}