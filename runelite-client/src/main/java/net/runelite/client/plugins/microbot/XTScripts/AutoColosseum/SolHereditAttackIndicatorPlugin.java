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
        name = "<html>[<font color=#ff69b4>\uD83D\uDC30</font>] " + "SolHereditAttackIndicator",
        description = "Highlights safe tiles for Sol Heredit",
        tags = {},
        enabledByDefault = false
)
@Slf4j
public class SolHereditAttackIndicatorPlugin extends Plugin {

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private SolHereditAttackIndicatorOverlay attackIndicatorOverlay;

    private int tickCounter = 1;

    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            attackIndicatorOverlay.attackStack.push(ATTACK.NOATTACK);
            overlayManager.add(attackIndicatorOverlay);

        }

    }

    protected void shutDown() {
        overlayManager.remove(attackIndicatorOverlay);
    }

    @Subscribe
    public void onGameTick(GameTick tick)
    {
        ATTACK previousAttack = attackIndicatorOverlay.attackStack.peek();
        int currentAnimation = attackIndicatorOverlay.npc.getAnimation();

        Microbot.log( "Previous Attack : " + previousAttack + "  |  " + "Current Animation: " + currentAnimation + "  |  " + "Tick Counter: " + tickCounter);

        // Idle? Do nothing
        if (tickCounter > 0){
            Microbot.log("Idling");
            tickCounter -= 1;
            return;
        }

        if (isIdle()){
            Microbot.log("Idling");
            tickCounter = 0;
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
            tickCounter = 5;
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
            tickCounter = 3;
            return;
        }

        if (attackIndicatorOverlay.npc.getAnimation() == ATTACK.COMBO_2TICK.getAnimationId()){
            Microbot.log("Normal Combo Attack - Sleeping for 10 ticks");
            attackIndicatorOverlay.attackStack.push(ATTACK.NOATTACK);
            tickCounter = 10;
            return;
        }

        if (attackIndicatorOverlay.npc.getAnimation() == ATTACK.COMBO_3TICK.getAnimationId()){
            Microbot.log("Combo Attack under 50% - Sleeping for 11 ticks");
            attackIndicatorOverlay.attackStack.push(ATTACK.NOATTACK);
            tickCounter = 11;
            return;
        }

    //         At this point, only possibility is special attack
        Microbot.log("Currently special attack. Resetting previous attack by adding NOATTACK.");

        // Set previous and current to None (Special attack resets normal attacks)
        attackIndicatorOverlay.attackStack.push(ATTACK.NOATTACK);

        tickCounter = 0;
    }

    private boolean isIdle(){
        return (attackIndicatorOverlay.npc.getAnimation() == -1);
    }
    private boolean isSpearAttackAnimation(){
        return (attackIndicatorOverlay.npc.getAnimation() == 10883);
    }
    private boolean isShieldAttackAnimation(){
        return (attackIndicatorOverlay.npc.getAnimation() == 10885);
    }
}
