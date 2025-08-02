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
    RETRIEVE_SEED,
    RETRIEVE_FILLED_PLANT_POTS,
    SAPLING_CREATION,
    HUMIDIFY,
    DEPOSIT_SAPLINGS,
}



public class AutoSaplingsScript extends Script {


//    static AutoSaplingsConfig config;


//    State state = State.SETUP;





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
        // Withdraw staff, equip it
        Rs2Bank.withdrawAndEquip("Mystic steam staff");
        sleepUntil(() -> Rs2Equipment.isWearing("Mystic steam staff"), 20000);

        // Withdraw seed, astral runes, trowel
        Rs2Bank.withdrawX("Astral rune", 100);
        Rs2Bank.withdrawOne("Gardening trowel");
//        Rs2Bank.withdrawX(PLANT_POT, 25);

    }

    public void retrieveSeed(SaplingJob currentJob){
        String seed = currentJob.getSEED();
        if (Rs2Bank.hasItem(seed)){
            Rs2Bank.withdrawAll(seed);
            sleepUntil(() -> Rs2Inventory.hasItem(seed));

            Rs2Inventory.moveItemToSlot(Rs2Inventory.get(seed), 27);
            sleep(1000);
        }
    }

//    public void depositSaplings(SaplingJob currentJob){
//        sleepUntil(Rs2Bank::openBank, 20000);
//
//        if (Rs2Inventory.contains(SEEDLING_WATERED)){
//            Rs2Bank.depositAll(SEEDLING_WATERED);
//        }
//
//        if (Rs2Inventory.contains((SEEDLING))){
//            Rs2Bank.depositAll(SEEDLING);
//        }
//
//        if (Rs2Inventory.contains((PLANT_POT))){
//            Rs2Bank.depositAll(PLANT_POT);
//        }
//
//        Rs2Bank.withdrawX(PLANT_POT, 25);
//        Rs2Bank.withdrawAll(true, SEED);
//
//        Rs2Bank.closeBank();
//        state = State.SAPLING_CREATION;
//    }

    public void createSaplings(){

        sleepUntil(Rs2Bank::closeBank, 20000);
        Rs2Inventory.open();
        sleepUntil(Rs2Inventory::isOpen, 20000);

        // while there are seeds and filled plant pots, use seeds on filled plant pots
        while(Rs2Inventory.contains("Filled plant pot") && Rs2Inventory.contains(SEED)){
//            Rs2Inventory.combineClosest(PLANT_POT, SEED);
            Rs2Inventory.slotInteract(26, "Use");
            Rs2Inventory.slotInteract(27);
        }

        state = State.HUMIDIFY;
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

        if (Rs2Inventory.contains(seedling_watered)){
            Rs2Bank.depositAll(seedling_watered);
        }
        if (Rs2Inventory.contains(sapling)){
            Rs2Bank.depositAll(sapling);
        }
    }

    State state = State.SETUP;
    SaplingJob currentJob = null;

    public boolean run(AutoSaplingsConfig config) {

        Microbot.log("A quick script! This is the Saplings Script! -- Reloaded!!!!");

        // Create job queue
        Queue<SaplingJob> jobQueue = createJobQueue(config);
        System.out.print("Jobs in the queue: ");
        for (SaplingJob job : jobQueue) {
            System.out.print(job + " , ");
        }
        System.out.println();


        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (currentJob == null){
                if (jobQueue.isEmpty()){
                    // Stop script if no jobs in queue and no active jobs
                    System.out.println("Job Queue is empty. Stopping Script ... ");
                    Microbot.stopPlugin(AutoSaplingsPlugin.class);
                    shutdown();
                    return;
                }
                else{
                    currentJob = jobQueue.poll();
                    System.out.println("Currently Processing : " + currentJob);
                }
            }

            switch (state){
                case SETUP:
                    System.out.println("Starting SETUP");
                    setup();
                    state = State.RETRIEVE_SEED;
                    break;
                case RETRIEVE_SEED:
                    System.out.println("Starting RETRIEVE_SEED");
                    retrieveSeed(currentJob);
                    state = State.SAPLING_CREATION;
                    break;
                case SAPLING_CREATION:
                    createSaplings();
                    state = State.HUMIDIFY;
                    break;
                case HUMIDIFY:
                    castHumidify(currentJob);
                    state = State.DEPOSIT_SAPLINGS;
                    break;
                case DEPOSIT_SAPLINGS:

            }
        }, 0, 100, TimeUnit.MILLISECONDS);
        return true;
    }
    
    @Override
    public void shutdown() {
        super.shutdown();
    }
}