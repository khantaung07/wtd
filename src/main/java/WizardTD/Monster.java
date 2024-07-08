package WizardTD;

import processing.core.PImage;
import java.util.*;

/**
 * The Monster class represents the game's monsters that move through the 
 * game board towards the wizard's house via path tiles.
 */
public class Monster extends Drawable {

    private float hp;
    private float startingHp;
    private float speed;
    private float defaultSpeed;
    private float TwoXSpeed;
    private float armour;
    private float mana_gained_on_kill;
    private boolean alive;
    
    private PImage[] deathAnimationFrames;
    private int deathAnimationCount;
    private int DeathFrameCount; // Frame count to make each image last 4 frames

    private Path path;
    private ArrayList<Tile> steps;
    private int stepCount;

    private String type;
    private App app;
    
    /**
     * Constructor for the Monster class. This includes basic attributes and 
     * constructing the shortest path from its spawn to the wizard's house.
     *
     * @param app The main application object.
     * @param type The type of monster.
     * @param x The x-coordinate on the game board.
     * @param y The y-coordinate on the game board.
     * @param hp The monster's health points.
     * @param speed The monster's movement speed in pixels per frame.
     * @param armour The monster's armor value, the percentage of damage taken.
     * @param mana_gained_on_kill The amount of mana gained by the player when the monster is killed.
     */
    public Monster(App app, String type, int x, int y, float hp, float speed,
     float armour, float mana_gained_on_kill) {
        super(x, y); // x and y are grid reference, whereas pixel is centred
        this.hp = hp;
        this.startingHp = hp;
        this.speed = speed;
        this.defaultSpeed = speed;
        this.TwoXSpeed = 2*speed;
        this.armour = armour;
        this.mana_gained_on_kill = mana_gained_on_kill;
        this.app = app;
        this.type = type;
        this.deathAnimationCount = 0;
        this.DeathFrameCount = 0;
        this.alive = true;
        this.stepCount = 0;

        // Generate path using Path class
        this.path = new Path(this.x, this.y , app.gameboard.getLayoutArray(), app.gameboard);
        this.steps = this.path.getSteps();

        // Adjust pixel location to be off screen depending on which edge tile the monster spawns
        if (this.x == 0) {    
            this.xPixel -= App.CELLSIZE;
        } else if (this.x == 19) {
            this.xPixel += App.CELLSIZE;
        } else if (this.y == 0) {
            this.yPixel -= App.CELLSIZE;
        } else if (this.y == 19) {
            this.yPixel += App.CELLSIZE;
        } else {
            System.err.println("Error in monster spawn.");
        }
       
        this.type = type;
        // Assign correct sprite
        if (this.type.equals("gremlin")) {
            this.sprite = app.images.get("gremlin");
        } else if (this.type.equals("beetle")) {
            this.sprite = app.images.get("beetle");
        } else if (this.type.equals("worm")) {
            this.sprite = app.images.get("worm");
        } else {
            System.err.println("Unknown monster type detected in config file.");
        }

        // Assign correct death animations
        deathAnimationFrames = new PImage[4];
        for (int i = 0; i < 4; i++) {
            // Images are named "type" + i
            deathAnimationFrames[i] = app.images.get(type + Integer.toString(i+1)); 
        }
        
        }
     /**
     * Update the state of the monster. This includes movement and updating the sprite upon death.
     * @see WizardTD.Monster#updateHealth(float) updateSprite
     */    
    public void tick() {

            if (this.app.fastforward) {
                this.speed = this.TwoXSpeed;
            }
            else {
                this.speed = this.defaultSpeed;
            }
        
            if (this.hp > 0 && this.steps != null && stepCount < this.steps.size()) {

                // Get the destination tile
                Tile destinationTile = this.steps.get(stepCount);
        
                // Calculate the distance between the current position and the destination tile
                double deltaX = destinationTile.getXPixel() - this.xPixel;
                double deltaY = destinationTile.getYPixel() - this.yPixel;
                double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        
                // Define the speed based on config
                double moveSpeed = this.speed; 
        
                // If the distance is greater than the speed, move towards the destination
                if (distance > moveSpeed) {
                    double ratio = moveSpeed / distance;
                    this.xPixel += ratio * deltaX;
                    this.yPixel += ratio * deltaY;
                } else {
                    // If the distance is smaller than the speed, directly reach the destination
                    this.xPixel = destinationTile.getXPixel();
                    this.yPixel = destinationTile.getYPixel();
                    this.x = destinationTile.getX();
                    this.y = destinationTile.getY();
        
                    // Move to the next step if available
                    if (stepCount < this.steps.size() - 1) {
                        stepCount++;
                    }
                }

                // If the monster reaches the wizard's house, it should respawn and deal damage
                Tile finalTile = this.steps.get(this.steps.size()-1);

                if (this.xPixel == finalTile.getXPixel() && this.yPixel == finalTile.getYPixel()) {
                    this.respawn();
                    this.app.gui.decreaseMana(this.hp);
                }

            }
        
        
         else {
            this.updateSprite();
        }
    }
    
    

    /**
     * Update the monster's sprite for death animation.
     */
    public void updateSprite() {
        
        // first checks if the monster is still alive
        if (this.alive) {
        this.DeathFrameCount++;
        
        // Only update sprite if 4 frames have passed
        if (this.DeathFrameCount%4 == 0) {
        this.sprite = this.deathAnimationFrames[deathAnimationCount];
        this.deathAnimationCount++;
        }

        // If all frames have been drawn, monster is dead and increase player mana
        if (this.deathAnimationCount == 4) {
            this.alive = false;
            this.app.gui.increaseMana(this.mana_gained_on_kill);
        }
    }
        
        
    }

   /**
     * Draw the health bar for the monster above its sprite.
     */
    public void drawHealth() {
        
        this.app.noStroke();
        this.app.fill(255,0,0);
        this.app.rect(this.xPixel, this.yPixel, App.CELLSIZE, 2);

        this.app.fill(0, 255, 0);
        this.app.rect(this.xPixel, this.yPixel, App.CELLSIZE*(this.hp/this.startingHp), 2);

    }

    /**
     * Update the monster's health after taking damage.
     *
     * @param damage The amount of damage taken by the monster.
     */
    public void updateHealth(float damage){
        this.hp -= (this.armour*damage);
    }

    /**
     * Respawn the monster at a random spawn tile after reaching the wizard's house.
     */
    public void respawn() {

        ArrayList<Tile> spawnTiles = new ArrayList<Tile>();

        // Iterate through tiles to get spawn tiles
        for (int x = 0; x < App.BOARD_WIDTH; x++) {
            for (int y = 0; y < App.BOARD_WIDTH; y++) {
                Tile current_tile = this.app.gameboard.getTile(x, y);
                // Check if tile is on the edge and is a path
                if (current_tile.getEdge() && (current_tile.getType().equals("path"))) {
                    spawnTiles.add(current_tile);
                }
            }
        }
        // Pick a new spawn 
        Tile newSpawnTile = spawnTiles.get((int)(app.random(spawnTiles.size())));

        // Set x and y coordinates to spawn point
        this.x = newSpawnTile.getX();
        this.y = newSpawnTile.getY();

        // Set x and y Pixel locations to spawn point
        this.xPixel = newSpawnTile.getXPixel();
        this.yPixel = newSpawnTile.getYPixel();

        // Adjust pixel locations according to edge
        // Adjust pixel location to be off screen depending on which edge tile the monster spawns
        if (this.x == 0) {    
            this.xPixel -= App.CELLSIZE;
        } else if (this.x == 19) {
            this.xPixel += App.CELLSIZE;
        } else if (this.y == 0) {
            this.yPixel -= App.CELLSIZE;
        } else if (this.y == 19) {
            this.yPixel += App.CELLSIZE;
        } else {
            System.err.println("Error in monster spawn.");
        }

        // Find new path
        this.path = new Path(this.x, this.y, app.gameboard.getLayoutArray(), app.gameboard);
        this.steps = this.path.getSteps();
        this.stepCount = 0;
    }
    
    // Getter methods
    public App getApp() {
        return app;
    }

    /**
     * Get the list of steps defining the monster's movement path.
     *
     * @return A array list of steps (tiles) on the path from its spawn to the wizard's house.
     */
    public ArrayList<Tile> getSteps() {
        return this.steps;
    }

    public String getType() {
        return type;
    }

    public float getHp() {
        return hp;
    }

    public boolean isAlive() {
        return this.alive;
    }

    public float getSpeed() {
        return speed;
    }

    public float getArmour() {
        return armour;
    }

    public float getManaGainedOnKill() {
        return mana_gained_on_kill;
    }
}
