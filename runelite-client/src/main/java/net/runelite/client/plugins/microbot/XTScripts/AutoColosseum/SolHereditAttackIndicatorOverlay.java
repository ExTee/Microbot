package net.runelite.client.plugins.microbot.XTScripts.AutoColosseum;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.npc.Rs2NpcModel;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

@Getter
@RequiredArgsConstructor
enum ATTACK {
    NOATTACK(-1, -1),
    SPEAR_1(10883, 6),
    SPEAR_2(10883, 6),
    SHIELD_1(10885, 4),
    SHIELD_2(10885, 4),
    GRAPPLE(-2,-2),
    COMBO_2TICK(10887, 11),
    COMBO_3TICK(10886, 12);

    private final int animationId;
    private final int duration;
}

public class SolHereditAttackIndicatorOverlay extends Overlay {

    @Inject
    private Client client;

    Rs2NpcModel npc;

//     = Rs2Npc.getNpc("Town Crier");

//    @Setter
//    @Getter
//    private ATTACK previousAttack = ATTACK.NOATTACK;
//
//    @Setter
//    @Getter
//    private ATTACK currentAttack = ATTACK.NOATTACK;

    // Attack Stack
    public Stack<ATTACK> attackStack = new Stack<>();

//    boolean isAnimating = false;

    @Inject
    SolHereditAttackIndicatorOverlay(SolHereditAttackIndicatorPlugin plugin)
    {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(PRIORITY_MED);
    }



    @Override
    public Dimension render(Graphics2D graphics) {

        try {
            npc = Rs2Npc.getNpc("Sol Heredit");
//            npc = Rs2Npc.getNpc("Town Crier");
            ATTACK currentAttack = attackStack.peek();
            if ((currentAttack != ATTACK.NOATTACK) && (npc.getAnimation() != -1)){
                renderSafeTiles(client, graphics, npc.getWorldLocation(), currentAttack);
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    private void renderSafeTiles(Client client, Graphics2D graphics, WorldPoint npcWorldPoint, ATTACK currentAttack){
        SafeTileOffsets safeTileOffsets = new SafeTileOffsets();
        ArrayList<LocalPoint> safeTiles = null;

        switch (currentAttack){
            case SPEAR_1:
                safeTiles = getSafeTiles(client, npcWorldPoint, safeTileOffsets.SPEAR_1_OFFSETS);
                break;
            case SPEAR_2:
                safeTiles = getSafeTiles(client, npcWorldPoint, safeTileOffsets.SPEAR_2_OFFSETS);
                break;
            case SHIELD_1:
                safeTiles = getSafeTiles(client, npcWorldPoint, safeTileOffsets.SHIELD_1_OFFSETS);
                break;
            case SHIELD_2:
                safeTiles = getSafeTiles(client, npcWorldPoint, safeTileOffsets.SHIELD_2_OFFSETS);
                break;
        }
        safeTiles.forEach(lp -> renderTile(graphics, lp, Color.BLACK, 1, new Color(15, 173, 15, 50)));
    }

    private void renderTile(final Graphics2D graphics, final LocalPoint dest, final Color color, final double borderWidth, final Color fillColor)
    {
        if (dest == null)
        {
            return;
        }

        final Polygon poly = Perspective.getCanvasTilePoly(client, dest);

        if (poly == null)
        {
            return;
        }

        OverlayUtil.renderPolygon(graphics, poly, color, fillColor, new BasicStroke((float) borderWidth));
    }

    private ArrayList<LocalPoint> getSafeTiles(Client client, WorldPoint center, int[][] OFFSETS)
    {
        /*
        Given a WorldPoint and an int[][] of offsets, returns am arraylist of localpoints representing safe tiles

        */
        ArrayList<LocalPoint> tiles = new ArrayList<>();

        for (int[] offset : OFFSETS) {
            int dx = offset[0];
            int dy = offset[1];

            WorldPoint wp = new WorldPoint(center.getX() + dx, center.getY() + dy, center.getPlane());
            LocalPoint lp = LocalPoint.fromWorld(client, wp);
            tiles.add(lp);

        }
        return tiles;
    }

}

class SafeTileOffsets {
    final int[][] SPEAR_1_OFFSETS = {
            {-1, -1}, {-2, -2}, {-3, -3}, {5, -1}, {6, -2}, {7, -3}, {5, 5}, {6, 6}, {7, 7}, {-1, 5}, {-2, 6}, {-3, 7},
            {-2, 0}, {-3, 0}, {-4, 0}, {-3, -1}, {-4, -1}, {-4, -2}, {0, -2}, {0, -3}, {-1, -3}, {2, -2}, {2, -3}, {4, -2}, {4, -3}, {5, -3},
            {6, 0}, {7, 0}, {8, 0}, {7, -1}, {8, -1}, {8, -2}, {-4, -4}, {-2, -4}, {-1, -4}, {0, -4}, {2, -4}, {4, -4}, {5, -4}, {6, -4}, {8, -4},
            {-4, 2}, {-3, 2}, {-2, 2}, {6, 2}, {7, 2}, {8, 2}, {-4, 4}, {-3, 4}, {-2, 4}, {6, 4}, {7, 4}, {8, 4}, {-4, 5}, {-3, 5}, {7, 5}, {8, 5},
            {-4, 6}, {0, 6}, {2, 6}, {4, 6}, {8, 6}, {-1, 7}, {0, 7}, {2, 7}, {4, 7}, {5, 7}, {-4, 8}, {-2, 8}, {-1, 8}, {0, 8}, {2, 8}, {4, 8},
            {5, 8}, {6, 8}, {8, 8}
    };

    final int[][] SPEAR_2_OFFSETS = {
            {-3, 8}, {-2, 8}, {1, 8}, {3, 8}, {6, 8}, {7, 8}, {-4, 7}, {-2, 7}, {-1, 7}, {1, 7}, {3, 7}, {5, 7}, {6, 7}, {8, 7},
            {-4, 6}, {-3, 6}, {-1, 6}, {1, 6}, {3, 6}, {5, 6}, {7, 6}, {8, 6}, {-3, 5}, {-2, 5}, {6, 5}, {7, 5},
            {-4, 3}, {-3, 3}, {-2, 3}, {6, 3}, {7, 3}, {8, 3}, {-4, 1}, {-3, 1}, {-2, 1}, {6, 1}, {7, 1}, {8, 1},
            {-3, -1}, {-2, -1}, {6, -1}, {7, -1}, {-4, -2}, {-3, -2}, {-1, -2}, {1, -2}, {3, -2}, {5, -2}, {7, -2}, {8, -2},
            {-4, -3}, {-2, -3}, {-1, -3}, {1, -3}, {3, -3}, {5, -3}, {6, -3}, {8, -3}, {-3, -4}, {-2, -4}, {1, -4}, {3, -4}, {6, -4}, {7, -4}
    };

    final int[][] SHIELD_1_OFFSETS = {
            {-2, 6}, {-1, 6}, {0, 6}, {1, 6}, {2, 6}, {3, 6}, {4, 6}, {5, 6}, {6, 6},
            {-2, -2}, {-1, -2}, {0, -2}, {1, -2}, {2, -2}, {3, -2}, {4, -2}, {5, -2}, {6, -2},
            {-2, -1}, {-2, 0}, {-2, 1}, {-2, 2}, {-2, 3}, {-2, 4}, {-2, 5},
            {6, -1}, {6, 0}, {6, 1}, {6, 2}, {6, 3}, {6, 4}, {6, 5}
    };

    final int[][] SHIELD_2_OFFSETS = {
            {-3, 7}, {-2, 7}, {-1, 7}, {0, 7}, {1, 7}, {2, 7}, {3, 7}, {4, 7}, {5, 7}, {6, 7}, {7, 7},
            {-3, -3}, {-2, -3}, {-1, -3}, {0, -3}, {1, -3}, {2, -3}, {3, -3}, {4, -3}, {5, -3}, {6, -3}, {7, -3},
            {-3, -2}, {-3, -1}, {-3, 0}, {-3, 1}, {-3, 2}, {-3, 3}, {-3, 4}, {-3, 5}, {-3, 6},
            {7, -2}, {7, -1}, {7, 0}, {7, 1}, {7, 2}, {7, 3}, {7, 4}, {7, 5}, {7, 6}
    };

}