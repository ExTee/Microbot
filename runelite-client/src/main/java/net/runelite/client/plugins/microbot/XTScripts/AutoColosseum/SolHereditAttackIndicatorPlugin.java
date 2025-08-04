package net.runelite.client.plugins.microbot.XTScripts.AutoColosseum;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
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
            attackIndicatorOverlay.isAnimating = false;
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

    @Subscribe
    public void onGameTick(GameTick tick)
    {
        ATTACK previous_atk = attackIndicatorOverlay.attackStack.peek();

        Microbot.log( "Previous Attack : " + previous_atk
                + "  |  "
                + "Current Animation: " + attackIndicatorOverlay.npc.getAnimation()
        );

        if (isIdle()){
            attackIndicatorOverlay.isAnimating = false;
            return;
        }
        if (attackIndicatorOverlay.isAnimating){
            return;
        }

        if (isSpearAttackAnimation()){
            if (previous_atk == ATTACK.SPEAR_1){
                Microbot.log("Previous attack was SPEAR_1, setting current attack to SPEAR_2.");
                attackIndicatorOverlay.attackStack.push(ATTACK.SPEAR_2);
            }
            else{
                // If the previous attack is shield, or none, set to spear 1
                Microbot.log("Previous attack was not SPEAR_1, setting current attack to SPEAR_1.");
                attackIndicatorOverlay.attackStack.push(ATTACK.SPEAR_1);
            }
            attackIndicatorOverlay.isAnimating = true;
            return;
        }

        if (isShieldAttackAnimation()){
            if (previous_atk == ATTACK.SHIELD_1){
                Microbot.log("Previous attack was SHIELD_1, setting current attack to SHIELD_2.");
                attackIndicatorOverlay.attackStack.push(ATTACK.SHIELD_2);
            }
            else{
                // If the previous attack is shield, or none, set to shield 1
                Microbot.log("Previous attack was not SHIELD_1, setting current attack to SHIELD_1.");
                attackIndicatorOverlay.attackStack.push(ATTACK.SHIELD_1);
            }
            attackIndicatorOverlay.isAnimating = true;
            return;
        }




//         At this point, only possibility is special attack
        Microbot.log("Currently special attack. Resetting previous attack.");

        // Set previous and current to None (Special attack resets normal attacks)
        attackIndicatorOverlay.attackStack.push(ATTACK.NOATTACK);
        // Log that we're currently animating
        attackIndicatorOverlay.isAnimating = true;



//        Microbot.log(
//                "Previous Attack : " + attackIndicatorOverlay.getPreviousAttack()
//                + "  |  "
//                + "Current Attack: " + attackIndicatorOverlay.getCurrentAttack()
//                + "  |  "
//                + "isAnimating: " + attackIndicatorOverlay.isAnimating
//                + "  |  "
//                + "Animation: " + attackIndicatorOverlay.npc.getAnimation()
//        );
//        if (isIdle()){
//
//            if (attackIndicatorOverlay.getCurrentAttack() != ATTACK.NOATTACK){
//                //If current attack is a noattack, don't do anything
//
//                Microbot.log("NPC is idle. Setting previous attack to " + attackIndicatorOverlay.getCurrentAttack());
//                // Set previous attack to current attack
//                attackIndicatorOverlay.setPreviousAttack(attackIndicatorOverlay.getCurrentAttack());
//
//                // Set current attack to none
//                attackIndicatorOverlay.setCurrentAttack(ATTACK.NOATTACK);
//
//            }
//            // Set isAnimating to False;
//            attackIndicatorOverlay.isAnimating = false;
//            return;
//
//
//        }
//
//        if (alreadyAnimating()){
//            Microbot.log("Already Animating. No state change required.");
//            return;
//        }
//
//        if (isSpearAttackAnimation()){
//            if (attackIndicatorOverlay.getPreviousAttack() == ATTACK.SPEAR_1){
//                Microbot.log("Previous attack was SPEAR_1, setting current attack to SPEAR_2.");
//                attackIndicatorOverlay.setCurrentAttack(ATTACK.SPEAR_2);
//            }
//            else{
//                // If the previous attack is shield, or none, set to spear 1
//                Microbot.log("Previous attack was not SPEAR_1, setting current attack to SPEAR_1.");
//                attackIndicatorOverlay.setCurrentAttack(ATTACK.SPEAR_1);
//            }
//            attackIndicatorOverlay.isAnimating = true;
//            return;
//        }
//
//        if (isShieldAttackAnimation()){
//            if (attackIndicatorOverlay.getPreviousAttack() == ATTACK.SHIELD_1){
//                Microbot.log("Previous attack was SHIELD_1, setting current attack to SHIELD_2.");
//                attackIndicatorOverlay.setCurrentAttack(ATTACK.SHIELD_2);
//            }
//            else{
//                // If the previous attack is shield, or none, set to shield 1
//                Microbot.log("Previous attack was not SHIELD_1, setting current attack to SHIELD_1.");
//                attackIndicatorOverlay.setCurrentAttack(ATTACK.SHIELD_1);
//            }
//            attackIndicatorOverlay.isAnimating = true;
//            return;
//        }
//
//        // At this point, only possibility is special attack
//        Microbot.log("Currently special attack. Resetting previous attack.");
//
//        // Set previous and current to None (Special attack resets normal attacks)
//        attackIndicatorOverlay.setPreviousAttack(ATTACK.NOATTACK);
//        attackIndicatorOverlay.setCurrentAttack(ATTACK.NOATTACK);
//
//        // Log that we're currently animating
//        attackIndicatorOverlay.isAnimating = true;

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
