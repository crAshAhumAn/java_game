package event;

import main.GamePanel;
import javax.imageio.ImageIO;

import entity.ENEMY_Boss01;
import entity.Entity;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class DoorEvent extends EventObject {
    private int nextMap;
    private BufferedImage doorImage;
    // New flag: becomes true when the event’s dialogue has been displayed.
    private boolean triggered = false;

    public DoorEvent(int worldx, int worldy, int width, int height, int nextMap, GamePanel gamePanel, boolean isBlackhole) {
        super(worldx, worldy, width, height);
        this.nextMap = nextMap;
        if (!isBlackhole) {
            try {
                doorImage = ImageIO.read(getClass().getResourceAsStream("/object/door.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            doorImage = null;
        }
        // Adjust collision bounds: start half a tile above the door and extend 1.5 tiles tall.
        collisionBounds = new Rectangle(-width/2, -height / 2, width, height + (int)(gamePanel.tileSize));
    }

    @Override
    public void triggerEvent(GamePanel gp) {
    	 if (!triggered) {
    	        triggered = true;
    	        gp.ui.optionText = promptMessage;
    	        
    	        // Check if this is the first map and boss is not required
    	        if (gp.currentMap == 0) {
    	            gp.ui.showDialogueOptions = true;
    	            gp.gameState = gp.dialogueState;
    	        } 
    	        // Check if boss is defeated for other maps
    	        else {
    	            // Find the boss in the NPC array
    	            boolean bossDefeated = false;
    	            for (Entity npc : gp.npc[gp.currentMap]) {
    	                if (npc instanceof ENEMY_Boss01) {
    	                    bossDefeated = npc.isBeatened;
    	                    break;
    	                }
    	            }
    	            
    	            if (bossDefeated) {
    	                gp.ui.showDialogueOptions = true;
    	                gp.gameState = gp.dialogueState;
    	            } else {
    	                gp.ui.showItemNotification(promptMessage);
    	            }
    	        }
    	 }
    }

    /**
     * Resets the triggered flag so that the door event can trigger again.
     * Call this when the player leaves the door’s collision area.
     */
    public void resetTriggered() {
        triggered = false;
    }

    public boolean isTriggered() {
        return triggered;
    }

    /**
     * When YES is selected, this method is called to initiate the map transition.
     */
    public void triggerDoorTransition(GamePanel gp) {
        if (!gp.isTransitionActive()) {
            System.out.println("Door triggered: transitioning to map " + nextMap);
            gp.startTransition(nextMap, 1000);
            gp.currentDoorEvent = null; // Clear the current door event
            gp.ui.showDialogueOptions = false; // Close the dialogue options
            gp.gameState = gp.playState;
        }
    }

    @Override
    public void draw(Graphics2D g2, GamePanel gp) {
        int screenX = worldx - gp.player.worldx + gp.player.screenX;
        int screenY = worldy - gp.player.worldy + gp.player.screenY;
        if (doorImage != null) {
            g2.drawImage(doorImage, screenX, screenY, gp.tileSize, gp.tileSize, null);
        }
        // Optionally draw collision bounds for debugging.
        // drawCollisionBounds(g2, gp);
    }

    public void drawCollisionBounds(Graphics2D g2, GamePanel gp) {
        int screenX = worldx - gp.player.worldx + gp.player.screenX + collisionBounds.x;
        int screenY = worldy - gp.player.worldy + gp.player.screenY + collisionBounds.y;
        g2.setColor(Color.RED);
        g2.drawRect(screenX, screenY, collisionBounds.width, collisionBounds.height);
    }

    public int getNextMap() {
        return nextMap;
    }


}