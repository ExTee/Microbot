package net.runelite.client.plugins.microbot.XTScripts.AutoColosseum;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.ui.overlay.OverlayManager;
import javax.inject.Inject;
import java.awt.*;



@PluginDescriptor(
        name = "SolHereditAttackIndicator",
        description = "Highlights safe tiles for Sol Heredit",
        tags = {},
        enabledByDefault = false
)
@Slf4j
public class SolHereditAttackIndicatorPlugin extends Plugin {
//    @Inject
//    private ExampleConfig config;
//    @Provides
//    ExampleConfig provideConfig(ConfigManager configManager) {
//        return configManager.getConfig(ExampleConfig.class);
//    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private SolHereditAttackIndicatorOverlay attackIndicatorOverlay;

//    @Inject
//    ExampleScript exampleScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
//            attackIndicatorOverlay.setPreviousAttack(ATTACK.NOATTACK);
//            attackIndicatorOverlay.setCurrentAttack(ATTACK.NOATTACK);
            attackIndicatorOverlay.attackStack.push(ATTACK.NOATTACK);
//            attackIndicatorOverlay.isAnimating = false;
            overlayManager.add(attackIndicatorOverlay);
//            attackIndicatorOverlay.previousAttack = null;
//            attackIndicatorOverlay.myButton.hookMouseListener();
        }
//        exampleScript.run(config);
    }

    protected void shutDown() {
//        exampleScript.shutdown();
        overlayManager.remove(attackIndicatorOverlay);
//        attackIndicatorOverlay.myButton.unhookMouseListener();
    }

//    @Subscribe
//    public void onGameTick(GameTick tick)
//    {
//        ATTACK previous_atk = attackIndicatorOverlay.attackStack.peek();
//
//        Microbot.log( "Previous Attack : " + previous_atk
//                + "  |  "
//                + "Current Animation: " + attackIndicatorOverlay.npc.getAnimation()
//        );
//
//        if (attackIndicatorOverlay.npc.getAnimation() != previous_atk.getAnimationId()){
//            Microbot.log("Animation is not the same as most recent attack. Setting isAnimating = False.");
//            attackIndicatorOverlay.isAnimating = false;
//        }
//        if (isIdle()){
//            Microbot.log("Animation is idle. Setting isAnimating = False.");
//            attackIndicatorOverlay.isAnimating = false;
//            return;
//        }
//        if (attackIndicatorOverlay.isAnimating){
//            Microbot.log("Already animating. Doing nothing.");
//            return;
//        }
//
//        if (isSpearAttackAnimation()){
//            if (previous_atk == ATTACK.SPEAR_1){
//                Microbot.log("Previous attack was SPEAR_1, setting current attack to SPEAR_2.");
//                attackIndicatorOverlay.attackStack.push(ATTACK.SPEAR_2);
//            }
//            else{
//                // If the previous attack is shield, or none, set to spear 1
//                Microbot.log("Previous attack was not SPEAR_1, setting current attack to SPEAR_1.");
//                attackIndicatorOverlay.attackStack.push(ATTACK.SPEAR_1);
//            }
//            //Sleep for 5 more ticks
//            attackIndicatorOverlay.isAnimating = true;
//
//            return;
//        }
//
//        if (isShieldAttackAnimation()){
//            if (previous_atk == ATTACK.SHIELD_1){
//                Microbot.log("Previous attack was SHIELD_1, setting current attack to SHIELD_2.");
//                attackIndicatorOverlay.attackStack.push(ATTACK.SHIELD_2);
//            }
//            else{
//                // If the previous attack is shield, or none, set to shield 1
//                Microbot.log("Previous attack was not SHIELD_1, setting current attack to SHIELD_1.");
//                attackIndicatorOverlay.attackStack.push(ATTACK.SHIELD_1);
//            }
//            attackIndicatorOverlay.isAnimating = true;
//            return;
//        }
//
////         At this point, only possibility is special attack
//        Microbot.log("Currently special attack. Resetting previous attack by adding NOATTACK.");
//
//        // Set previous and current to None (Special attack resets normal attacks)
//        attackIndicatorOverlay.attackStack.push(ATTACK.NOATTACK);
//        // Log that we're currently animating
//        attackIndicatorOverlay.isAnimating = true;
//
//
//    }
    @Subscribe
    public void onGameTick(GameTick tick)
    {
        ATTACK previousAttack = attackIndicatorOverlay.attackStack.peek();
        int currentAnimation = attackIndicatorOverlay.npc.getAnimation();

        Microbot.log( "Previous Attack : " + previousAttack + "  |  " + "Current Animation: " + currentAnimation);

        // Idle? Do nothing
        if (currentAnimation == -1){
            Microbot.log("Idling");
            return;
        }

        // If Spear Animation
        if (isSpearAttackAnimation()){
            if (previousAttack == ATTACK.SPEAR_1){
                Microbot.log("Previous attack was SPEAR_1, setting current attack to SPEAR_2.");
                attackIndicatorOverlay.attackStack.push(ATTACK.SPEAR_2);
            }
            else{
                // If the previous attack is shield, or none, set to spear 1
                Microbot.log("Previous attack was not SPEAR_1, setting current attack to SPEAR_1.");
                attackIndicatorOverlay.attackStack.push(ATTACK.SPEAR_1);
            }
            //Sleep for 5 more ticks
            Microbot.log("- Sleeping for 5 ticks");
            sleepUntilTick(5);

//            attackIndicatorOverlay.isAnimating = true;

            return;
        }

        if (isShieldAttackAnimation()){
            if (previousAttack == ATTACK.SHIELD_1){
                Microbot.log("Previous attack was SHIELD_1, setting current attack to SHIELD_2.");
                attackIndicatorOverlay.attackStack.push(ATTACK.SHIELD_2);
            }
            else{
                // If the previous attack is shield, or none, set to shield 1
                Microbot.log("Previous attack was not SHIELD_1, setting current attack to SHIELD_1.");
                attackIndicatorOverlay.attackStack.push(ATTACK.SHIELD_1);
            }
            //Sleep for 5 more ticks
            Microbot.log("- Sleeping for 3 ticks");
            sleepUntilTick(3);
            return;
        }

        if (attackIndicatorOverlay.npc.getAnimation() == ATTACK.COMBO_2TICK.getAnimationId()){
            Microbot.log("Normal Combo Attack - Sleeping for 10 ticks");
            attackIndicatorOverlay.attackStack.push(ATTACK.NOATTACK);
            sleepUntilTick(10);
            return;
        }

        if (attackIndicatorOverlay.npc.getAnimation() == ATTACK.COMBO_2TICK.getAnimationId()){
            Microbot.log("Combo Attack under 50% - Sleeping for 11 ticks");
            attackIndicatorOverlay.attackStack.push(ATTACK.NOATTACK);
            sleepUntilTick(11);
            return;
        }

    //         At this point, only possibility is special attack
        Microbot.log("Currently special attack. Resetting previous attack by adding NOATTACK.");

        // Set previous and current to None (Special attack resets normal attacks)
        attackIndicatorOverlay.attackStack.push(ATTACK.NOATTACK);


    }

    private boolean isIdle(){
        return (attackIndicatorOverlay.npc.getAnimation() == -1);
    }
//    private boolean alreadyAnimating(){
//        return attackIndicatorOverlay.isAnimating;
//    }
//
    private boolean isSpearAttackAnimation(){
        return (attackIndicatorOverlay.npc.getAnimation() == 10883);
    }
    private boolean isShieldAttackAnimation(){
        return (attackIndicatorOverlay.npc.getAnimation() == 10885);
    }

    public boolean sleepUntilTick(int ticksToWait) {
        int startTick = Microbot.getClient().getTickCount();
        return Global.sleepUntil(() -> Microbot.getClient().getTickCount() >= startTick + ticksToWait, ticksToWait * 600 + 2000);
    }

    /*
    For Testing purposes on Town Crier
     */

//    private boolean isSpearAttackAnimation(){
//        return (attackIndicatorOverlay.npc.getAnimation() == 6863);
//    }
//    private boolean isShieldAttackAnimation(){
//        return (attackIndicatorOverlay.npc.getAnimation() == 6865);
//    }



}
