package WizardTD;

import java.util.*;

/**
 * Towers can be built by the user to  target and shoot fireballs at nearby monsters.
 */
public class Tower extends Drawable {
    private App app;
    private float range;
    private float firingSpeed;
    private float damage;
    private float initDamage;
    private float firerate;
    private float defaultFirerate;
    private float TwoXFirerate;
    private ArrayList<Monster> monsters;
    private Monster target;
    private int frameCounter;
    private ArrayList<Fireball> fireballs;

    private float towerCenterX;
    private float towerCenterY;

    private int speedLvl = 0;
    private int rangeLvl = 0;
    private int damageLvl = 0;

    /**
     * Constructs a new Tower with initial values as stated in the configuration file.
     *
     * @param app       The main application instance.
     * @param x         The x-coordinate of the tower's position.
     * @param y         The y-coordinate of the tower's position.
     * @param initCost  The initial cost of the tower.
     * @param initRange The initial range of the tower.
     * @param initSpeed The initial firing speed of the tower.
     * @param initDamage The initial damage of the tower.
     */
    public Tower(App app, int x, int y, float initCost, float initRange, float initSpeed, 
    float initDamage) {
        super(x, y); // Need to make the x and y equal to the top left values (round)
        this.app = app;
        this.range = initRange;
        this.firingSpeed = initSpeed;
        this.damage = initDamage;
        this.initDamage = initDamage;
        this.sprite = app.images.get("tower0");
        this.monsters = new ArrayList<Monster>();
        this.target = null;
        this.frameCounter = 0;
        this.firerate = (App.FPS/this.firingSpeed);
        this.defaultFirerate = (App.FPS/this.firingSpeed);
        this.TwoXFirerate = (App.FPS/this.firingSpeed)/2;
        this.fireballs = new ArrayList<Fireball>();
        this.towerCenterX = getXPixel() + sprite.width / 2;
        this.towerCenterY = getYPixel() + sprite.height / 2;
        

    }

    /**
     * Updates the tower's state during the game, including firing fireballs
     *  and targeting monsters.
     *
     * @param app The main application instance.
     */
    public void tick(App app) {

        if (this.app.fastforward) {
            this.firerate = this.TwoXFirerate;
        }
        else {
            this.firerate = this.defaultFirerate;
        }

        this.findMonster();

        // Need to round to avoid modular arithmetic with decimal
        if (frameCounter%Math.round(this.firerate) == 0){
            if (!this.monsters.isEmpty() && (this.target != null)) {
                this.shootFireball();
                this.frameCounter = 0;
            }

        }
        // Update time passed
        this.frameCounter++;
        
        
    }

    /**
     * Draws the fireballs fired by the tower.
     */
    public void drawFireballs() {
        // Draw and tick fireballs that belong to this tower


        for (Fireball fireball : this.fireballs) {
            if (fireball.isActive()){
                if (!this.app.paused && !this.app.dead) {
                    fireball.tick();
                }
                fireball.draw(this.app);
                
            }
        }

        // Remove inactive fireballs
        Iterator<Fireball> itr = this.fireballs.iterator();
        while (itr.hasNext()) {
            Fireball fireball = itr.next();
            if (!fireball.isActive()) {
                itr.remove();
            }
        }
    }

   /**
     * Finds monsters within the tower's range and assigns a monster as the tower's target.
     */
    public void findMonster() {

        // Clear previous information
        monsters.clear();
        this.target = null;

        // Get the tower's centre coordinates
        float towerCenterX = getXPixel() + sprite.width / 2;
        float towerCenterY = getYPixel() + sprite.height / 2;

        // Find current monsters on the map
        for (Wave curWave : this.app.wavemanager.getWaves()) {
            for (Monster curMonster : curWave.getSpawnedMonsters()) {
                this.monsters.add(curMonster);
            }
        }

        for (Monster curMonster : this.monsters) {
            // Calculate if the monster is within the range
            float distance = (float)(Math.sqrt(Math.pow(towerCenterX - curMonster.getXPixel(), 2) +
             Math.pow(towerCenterY - curMonster.getYPixel(), 2)));

            if (distance <= this.range) {
                if (curMonster.getHp() > 0) {
                    this.target = curMonster;
                    break;
                }
                
            }
            
        }
        
    }

    /**
     * Creates a fireball that tracks the target monster.
     */
    public void shootFireball() {
        Fireball newFireball = new Fireball(this.app, this.getX(), this.getY(), 
        this.damage, this.target);

        this.fireballs.add(newFireball);


    }

     /**
     * Checks if the mouse is currently over the tower.
     *
     * @param app The main application instance.
     * @return true if the mouse is over the tower, false otherwise.
     */
     public boolean isMouseOver(App app) {
        return (app.mouseX >= this.xPixel && app.mouseX <= this.xPixel + App.CELLSIZE &&
         app.mouseY >= this.yPixel && app.mouseY <= this.yPixel + App.CELLSIZE); 
    }

    /**
     * Draws the range of the tower when the mouse is hovering over.
     */
    public void drawRadius() {
        
        if (this.isMouseOver(this.app)) {

            // Set the outline to yellow and no fill

            float size = range + range;
            
            this.app.stroke(255, 255, 0); // Yellow color
            this.app.strokeWeight(2);
            this.app.noFill();
            this.app.ellipse(this.towerCenterX, this.towerCenterY, size, size);
            
        }
        

    }

    /**
     * Upgrades the towers based on whether or not the upgrade buttons are selected.
     */
    public void upgradeTower() {
        // Check if mouse is over tower and clicked
        if (this.isMouseOver(this.app) && app.mousePressed) {

            if (app.mouseReleased) {

            // Check upgrade flags
            if (this.app.gui.getUpgradeRange()) {

                float range_price = 20 + 10*(this.rangeLvl);

                if (this.app.gui.getCurrentMana() >= range_price) {
                    this.rangeLvl += 1;
                    this.range += 32;
                    this.app.gui.decreaseMana(range_price);
                }
                
            }

            if (this.app.gui.getUpgradeSpeed()) {

                float speed_price = 20 + 10*(this.speedLvl);

                if (this.app.gui.getCurrentMana() >= speed_price) {
                    this.speedLvl += 1;
                    this.firingSpeed += 0.5;
                    // Re-calculate firerates
                    this.firerate = (App.FPS/this.firingSpeed);
                    this.defaultFirerate = (App.FPS/this.firingSpeed);
                    this.TwoXFirerate = (App.FPS/this.firingSpeed)/2;
                    this.app.gui.decreaseMana(speed_price);
                }
                
            }

            if (this.app.gui.getUpgradeDamage()) {

                float damage_price = 20 + 10*(this.damageLvl);

                if (this.app.gui.getCurrentMana() >= damage_price) {
                    this.damageLvl += 1;
                    this.damage += 0.5*this.initDamage;
                    this.app.gui.decreaseMana(damage_price);
                }
                
            }

            app.mouseReleased = false;
        }

        }

    }

    /**
     * Draws the tower's upgrades.
     */
    public void drawUpgrades() {
        // Update main tower sprite
        if (this.speedLvl >= 2 && this.damageLvl >= 2 && this.rangeLvl >= 2) {
            this.sprite = app.images.get("tower2");
            // Draw individual upgrades
            this.drawIndividiualUpgrades(2);
            
        }
        else if (this.speedLvl >= 1 && this.damageLvl >= 1 && this.rangeLvl >= 1) {
            this.sprite = app.images.get("tower1");
            this.drawIndividiualUpgrades(1);
        }
        else {
            this.drawIndividiualUpgrades(0);
            
        }
        
    }

    /**
     * Draws individual upgrade indicators for range, speed and damage.
     *
     * @param upgradeLevel The tower's overall upgrade level. All upgrades must be this 
     * level or higher. For example, speed = 1, range = 2, damage = 2, is a level 1 tower.
     */
    public void drawIndividiualUpgrades(int upgradeLevel) {
        this.app.textAlign(App.LEFT, App.TOP);
        if (this.speedLvl > upgradeLevel) {

            float centerX = getXPixel() + this.sprite.width / 2;
            float centerY = getYPixel() + this.sprite.height / 2;

            this.app.stroke(175, 235, 255);
            this.app.strokeWeight((float)0.6 * this.speedLvl);
            this.app.noFill();
        
            // Calculate the size based on upgrade level
            float size = (float)(20 + 0.6*(this.speedLvl - upgradeLevel));
        
            // Calculate the coordinates for the top-left corner of the rectangle
            float X = (centerX - size / 2);
            float Y = (centerY - size / 2);
        
            this.app.rect(X, Y, size, size);

        }
        this.app.textSize(8);
        for (int i = 0; i < this.rangeLvl - upgradeLevel; i++) {
            this.app.fill(255,0,255);
            this.app.text("O", this.xPixel + i*6, this.yPixel);
        }
        for (int i = 0; i < this.damageLvl - upgradeLevel; i++) {
            this.app.fill(255,0,255);
            this.app.text("X", this.xPixel + i*6, this.yPixel + 24);
        }

    }

    /**
     * Draws an upgrade tooltip to show the cost of available upgrades if
     * the mouse is hovering over the tower.
     */
    public void drawUpgradeTip() {

        this.app.textAlign(App.LEFT, App.TOP);
        
        // Draw tooltip box if mouse is hovered and options are selected
        if (this.isMouseOver(this.app)) {
            if (this.app.gui.getUpgradeRange() || this.app.gui.getUpgradeSpeed() ||
             this.app.gui.getUpgradeDamage()) {
    
                int numUpgrades = 0;
                int upgradeCost = 0;
    
                int textY = 517; // Initial Y position for text
                int boxHeight = 38; // Initial box height
    
                this.app.fill(255, 255, 255);
                this.app.strokeWeight(1);
    
                if (this.app.gui.getUpgradeRange()) {
                    numUpgrades++;
                    upgradeCost += 20 + 10 * this.rangeLvl;
                }
                if (this.app.gui.getUpgradeSpeed()) {
                    numUpgrades++;
                    upgradeCost += 20 + 10 * this.speedLvl;
                }
                if (this.app.gui.getUpgradeDamage()) {
                    numUpgrades++;
                    upgradeCost += 20 + 10 * this.damageLvl;
                }
    
                // Calculate box height based on the number of upgrades
                boxHeight += 10 * numUpgrades;
    
                this.app.rect(App.WIDTH - 110, 500, 72, boxHeight);
    
                this.app.fill(0);
                this.app.textSize(10);
    
                if (this.app.gui.getUpgradeRange()) {
                    this.app.text("range:     " + Integer.toString(20 + 10 * this.rangeLvl), 
                    App.WIDTH - 108, textY);
                    textY += 10; // Move the Y position down
                }
                if (this.app.gui.getUpgradeSpeed()) {
                    this.app.text("speed:     " + Integer.toString(20 + 10 * this.speedLvl), 
                    App.WIDTH - 108, textY);
                    textY += 10; // Move the Y position down
                }
                if (this.app.gui.getUpgradeDamage()) {
                    this.app.text("damage:  " + Integer.toString(20 + 10 * this.damageLvl), 
                    App.WIDTH - 108, textY);
                }
    
                this.app.text("Upgrade cost", App.WIDTH - 108, 502);
                this.app.line(App.WIDTH - 110, 515, App.WIDTH - 38, 515);
                this.app.line(App.WIDTH - 110, 512 + 10 * (numUpgrades + 1), 
                App.WIDTH - 38, 512 + 10 * (numUpgrades + 1));
                this.app.text("Total:      " + Integer.toString(upgradeCost), 
                App.WIDTH - 108, 514 + 10 * (numUpgrades + 1));
            }
        }
    }

    public Monster getTarget() {
        return this.target;
    }

    public int getDamage() {
        return this.damageLvl;
    }

    public int getSpeed() {
        return this.speedLvl;
    }
    
    public int getRange() {
        return this.rangeLvl;
    }

    // Setters for testing drawing
    // Are not implemented in the game and do not increase tower stats
    public void setDamage(int lvl) {
        this.damageLvl = lvl;
    }

    public void setRange(int lvl) {
        this.rangeLvl = lvl;
    }

    public void setSpeed(int lvl) {
        this.speedLvl = lvl;
    }

    /**
     * Gets the list of fireballs belonging to this tower.
     *
     * @return An array list of fireballs.
     */
    public ArrayList<Fireball> getFireballs() {
        return this.fireballs;
    }
    
    
    
    
}
