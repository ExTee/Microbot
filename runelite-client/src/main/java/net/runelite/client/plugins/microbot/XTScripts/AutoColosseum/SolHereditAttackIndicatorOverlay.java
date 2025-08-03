package net.runelite.client.plugins.microbot.XTScripts.AutoColosseum;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.npc.Rs2NpcModel;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;

enum ATTACK {
    SPEAR_1,
    SPEAR_2,
    SHIELD_1,
    SHIELD_2
}

public class SolHereditAttackIndicatorOverlay extends Overlay {

    @Inject
    private Client client;
    public ATTACK previousAttack;
    public boolean isAnimating = false;

//    public final ButtonComponent myButton;
    @Inject
    SolHereditAttackIndicatorOverlay(SolHereditAttackIndicatorPlugin plugin)
    {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(PRIORITY_MED);

//        super(plugin);
//        setPosition(OverlayPosition.TOP_CENTER);
//        setNaughty();
//        myButton = new ButtonComponent("Testing");
//        myButton.setPreferredSize(new Dimension(100, 30));
//        myButton.setParentOverlay(this);
//        myButton.setFont(FontManager.getRunescapeBoldFont());
//        myButton.setOnClick(() -> Microbot.openPopUp("Microbot", String.format("S-1D:<br><br><col=ffffff>%s Popup</col>", "Example")));
    }



    @Override
    public Dimension render(Graphics2D graphics) {

        try {
            final WorldPoint playerPos = client.getLocalPlayer().getWorldLocation();
            final LocalPoint playerPosLocal = LocalPoint.fromWorld(client, playerPos);

            Microbot.log(String.valueOf(playerPosLocal));
            Microbot.log(String.valueOf(playerPos));

            Rs2NpcModel npc = Rs2Npc.getNpc("Town Crier");
            if (npc.getAnimation() != -1) {

                WorldPoint npc_wp = npc.getWorldLocation();
//                LocalPoint npc_lp = LocalPoint.fromWorld(client, npc_wp);

//                ArrayList<LocalPoint> npc_lp_tiles = getTilesWithinDistance(npc_wp, 2);

                ArrayList<LocalPoint> npc_lp_tiles = null;
                if (previousAttack == ATTACK.SPEAR_1){
                    npc_lp_tiles = getSafeTiles(npc_wp, SPEAR_2_OFFSETS);
                    npc_lp_tiles.forEach(lp -> renderTile(graphics, lp, Color.BLACK, 1, new Color(15, 173, 15, 50)));
                    previousAttack = ATTACK.SPEAR_2;
                }
                else{
                    npc_lp_tiles = getSafeTiles(npc_wp, SPEAR_1_OFFSETS);
                    npc_lp_tiles.forEach(lp -> renderTile(graphics, lp, Color.BLACK, 1, new Color(15, 173, 15, 50)));
                    previousAttack = ATTACK.SPEAR_1;
                }


            }
            else{
                isAnimating = false;
            }


//            renderTile(graphics, npc_lp, Color.BLACK, 1, new Color(15,173,15,50));


//            renderTile(graphics, playerPosLocal, Color.BLACK, 1, new Color(15,173,15,50));


        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
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

    public ArrayList<LocalPoint> getTilesWithinDistance(WorldPoint center, int distance)
    {
        ArrayList<LocalPoint> tiles = new ArrayList<>();
        for (int dx = -distance; dx <= distance; dx++)
        {
            for (int dy = -distance; dy <= distance; dy++)
            {
                WorldPoint tile = new WorldPoint(center.getX() + dx, center.getY() + dy, center.getPlane());
                // Manhattan distance
                if (Math.abs(dx) + Math.abs(dy) <= distance)
                {
                    LocalPoint lp = LocalPoint.fromWorld(client, tile);
//                    tiles.add(tile);
                    tiles.add(lp);
                }
            }
        }
        return tiles;
    }

    public ArrayList<LocalPoint> getSafeTiles(WorldPoint center, int[][] OFFSETS)
    {
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

    int[][] SPEAR_1_OFFSETS = {
            {-1, -1}, {-2, -2}, {-3, -3}, {5, -1}, {6, -2}, {7, -3}, {5, 5}, {6, 6}, {7, 7}, {-1, 5}, {-2, 6}, {-3, 7},
            {-2, 0}, {-3, 0}, {-4, 0}, {-3, -1}, {-4, -1}, {-4, -2}, {0, -2}, {0, -3}, {-1, -3}, {2, -2}, {2, -3}, {4, -2}, {4, -3}, {5, -3},
            {6, 0}, {7, 0}, {8, 0}, {7, -1}, {8, -1}, {8, -2}, {-4, -4}, {-2, -4}, {-1, -4}, {0, -4}, {2, -4}, {4, -4}, {5, -4}, {6, -4}, {8, -4},
            {-4, 2}, {-3, 2}, {-2, 2}, {6, 2}, {7, 2}, {8, 2}, {-4, 4}, {-3, 4}, {-2, 4}, {6, 4}, {7, 4}, {8, 4}, {-4, 5}, {-3, 5}, {7, 5}, {8, 5},
            {-4, 6}, {0, 6}, {2, 6}, {4, 6}, {8, 6}, {-1, 7}, {0, 7}, {2, 7}, {4, 7}, {5, 7}, {-4, 8}, {-2, 8}, {-1, 8}, {0, 8}, {2, 8}, {4, 8},
            {5, 8}, {6, 8}, {8, 8}
    };

    int[][] SPEAR_2_OFFSETS = {
            {-3, 8}, {-2, 8}, {1, 8}, {3, 8}, {6, 8}, {7, 8}, {-4, 7}, {-2, 7}, {-1, 7}, {1, 7}, {3, 7}, {5, 7}, {6, 7}, {8, 7},
            {-4, 6}, {-3, 6}, {-1, 6}, {1, 6}, {3, 6}, {5, 6}, {7, 6}, {8, 6}, {-3, 5}, {-2, 5}, {6, 5}, {7, 5},
            {-4, 3}, {-3, 3}, {-2, 3}, {6, 3}, {7, 3}, {8, 3}, {-4, 1}, {-3, 1}, {-2, 1}, {6, 1}, {7, 1}, {8, 1},
            {-3, -1}, {-2, -1}, {6, -1}, {7, -1}, {-4, -2}, {-3, -2}, {-1, -2}, {1, -2}, {3, -2}, {5, -2}, {7, -2}, {8, -2},
            {-4, -3}, {-2, -3}, {-1, -3}, {1, -3}, {3, -3}, {5, -3}, {6, -3}, {8, -3}, {-3, -4}, {-2, -4}, {1, -4}, {3, -4}, {6, -4}, {7, -4}
    };

    int[][] SHIELD_1_OFFSETS = {
            {-2, 6}, {-1, 6}, {0, 6}, {1, 6}, {2, 6}, {3, 6}, {4, 6}, {5, 6}, {6, 6},
            {-2, -2}, {-1, -2}, {0, -2}, {1, -2}, {2, -2}, {3, -2}, {4, -2}, {5, -2}, {6, -2},
            {-2, -1}, {-2, 0}, {-2, 1}, {-2, 2}, {-2, 3}, {-2, 4}, {-2, 5},
            {6, -1}, {6, 0}, {6, 1}, {6, 2}, {6, 3}, {6, 4}, {6, 5}
    };

    int[][] SHIELD_2_OFFSETS = {
            {-3, 7}, {-2, 7}, {-1, 7}, {0, 7}, {1, 7}, {2, 7}, {3, 7}, {4, 7}, {5, 7}, {6, 7}, {7, 7},
            {-3, -3}, {-2, -3}, {-1, -3}, {0, -3}, {1, -3}, {2, -3}, {3, -3}, {4, -3}, {5, -3}, {6, -3}, {7, -3},
            {-3, -2}, {-3, -1}, {-3, 0}, {-3, 1}, {-3, 2}, {-3, 3}, {-3, 4}, {-3, 5}, {-3, 6},
            {7, -2}, {7, -1}, {7, 0}, {7, 1}, {7, 2}, {7, 3}, {7, 4}, {7, 5}, {7, 6}
    };




}
