package WizardTD;

import java.util.*;

/**
 * The GUI class handles the game's user interface, including buttons and mana and tower management.
 * It also determines whether the play has lost the game (mana less than 0).
 */
public class GUI {
    private int frameCounter = 0;

    private float currentMana;
    private float passiveManaGain;
    private float maxMana;
    private float currentManaPoolCost;
    private float manaPoolCostIncreasePerUse;
    private float manaPoolCapMulitpler;
    private float manaGainedMulitpler;

    private ArrayList<Button> buttons;
    private App app;
    private ArrayList<Tower> towers;

    private boolean upgradeRange = false;
    private boolean upgradeSpeed = false;
    private boolean upgradeDamage = false;

    private boolean player_lost = false;

    /**
     * Constructs the GUI class. This involves initialising the player mana, buttons and list of towers.
     *
     * @param app The main game application.
     */
    public GUI(App app) {
        this.currentMana = (app.config.getFloat("initial_mana"));
        this.maxMana = (app.config.getFloat("initial_mana_cap"));
        this.currentManaPoolCost = (app.config.getFloat("mana_pool_spell_initial_cost"));
        this.passiveManaGain = (app.config.getFloat("initial_mana_gained_per_second"));
        this.manaPoolCostIncreasePerUse = (app.config.getFloat("mana_pool_spell_cost_increase_per_use"));
        this.manaPoolCapMulitpler = (app.config.getFloat("mana_pool_spell_cap_multiplier"));
        this.manaGainedMulitpler = (app.config.getFloat("mana_pool_spell_mana_gained_multiplier"));

        this.buttons = new ArrayList<Button>();
        this.towers = new ArrayList<Tower>();
        this.app = app;

        // Create buttons on GUI
        Button ff = new Button(App.WIDTH - 110, 50, "FF", "2x speed", app);
        buttons.add(ff);
        Button p = new Button(App.WIDTH - 110, 100, "P" , "PAUSE", app);
        buttons.add(p);
        Button t = new Button(App.WIDTH - 110, 150, "T" , "Build \ntower", app);
        buttons.add(t);
        Button u1 = new Button(App.WIDTH - 110, 200, "U1" , "Upgrade \nrange", app);
        buttons.add(u1);
        Button u2 = new Button(App.WIDTH - 110, 250, "U2" , "Upgrade \nspeed", app);
        buttons.add(u2);
        Button u3 = new Button(App.WIDTH - 110, 300, "U3" , "Upgrade \ndamage", app);
        buttons.add(u3);
        Button m = new Button(App.WIDTH - 110, 350, "M" , "Mana pool \ncost: " +
         Integer.toString((int)this.currentManaPoolCost), app);
        buttons.add(m);
        Button mute = new Button(App.WIDTH - 110, 400, "m" , "Mute \nmusic", app);
        buttons.add(mute);
    }
    
    /**
     * Draws the game's user interface, including mana display and buttons.
     */
    public void draw() {
        
        app.textAlign(App.LEFT, App.BASELINE);

        // Draw Mana Box(es)
        app.fill(255);
        app.stroke(0);
        app.strokeWeight(2);
        app.rect(App.WIDTH/2 + 5, 10, 330, 22);

        app.fill(0, 255, 255);
        app.rect(App.WIDTH/2 + 5, 10, 330*(this.currentMana/this.maxMana), 22);

        // Draw Mana text
        app.fill(0,0,0);
        app.textSize(20);
        app.text("MANA: ", App.WIDTH/2 - 65, 30);
        String displayMana = (Integer.toString((int)this.currentMana) + " / " + 
        Integer.toString((int)this.maxMana));
        app.text(displayMana, App.WIDTH/2 + 112, 30);

    
        // Draw buttons
        for (Button button : this.buttons) {
            button.draw(app);
        }

    }

    /**
     * Updates the state of buttons, towers, and player mana per frame.
     *
     * @param app The main game application.
     */
    public void tick(App app) {

        for (Button button: this.buttons) {
            button.tick();
        }

        if (!app.paused && !app.dead) {
            for (Tower tower : this.towers) {
                tower.tick(app);
            }

            this.frameCounter++;

            // Update player mana
            if (this.app.fastforward) {
                if (this.frameCounter%30 == 0) {
                    increaseMana(this.passiveManaGain);
                    this.frameCounter = 0;
                }
            }
            else {
                if (this.frameCounter%60 == 0) {
                    increaseMana(this.passiveManaGain);
                    this.frameCounter = 0;
                }
            }
        }
    }

    /**
     * Allows the player to upgrade tower damage.
     */
    public void upgradeDamage() {
        this.upgradeDamage = true;
    }
    /**
     * Allows the player to upgrade tower range.
     */
    public void upgradeRange() {
        this.upgradeRange = true;
    }

    /**
     * Allows the player to upgrade tower speed.
     */
    public void upgradeSpeed() {
        this.upgradeSpeed = true;
    }

    /**
     * Resets the player's ability to upgrade tower damage.
     */
    public void resetUpgradeDamage() {
        this.upgradeDamage = false;
    }
    
    /**
     * Resets the player's ability to upgrade tower range.
     */
    public void resetUpgradeRange() {
        this.upgradeRange = false;
    }
    
     /**
     * Resets the player's ability to upgrade tower speed.
     */
    public void resetUpgradeSpeed() {
        this.upgradeSpeed = false;
    }
    
    /**
     * Checks whether the tower damage can be currently upgraded.
     *
     * @return true if tower damage can be upgraded, false otherwise. 
     */
    public boolean getUpgradeDamage() {
        return this.upgradeDamage;
    }

    /**
     * Checks whether the tower range can be currently upgraded.
     *
     * @return true if tower range can be upgraded, false otherwise.
     */
    public boolean getUpgradeRange() {
        return this.upgradeRange;
    }

    /**
     * Checks whether the tower speed can be currently upgraded.
     *
     * @return true if tower speed can be upgraded, false otherwise.
     */
    public boolean getUpgradeSpeed() {
        return this.upgradeSpeed;
    }

    /**
     * Adds a tower to the list of active towers.
     *
     * @param t The tower to add.
     */
    public void addTower(Tower t) {
        this.towers.add(t);
    }

    public float getCurrentMana() {
        return this.currentMana;
    }

    /**
     * Gets the current rate of passive mana gain per second.
     * 
     * @return The rate of passive mana gain per second.
     */
    public float getPassiveMana() {
        return this.passiveManaGain;
    }

    /**
     * Casts the mana pool spell, increasing the player's maximum mana, passive mana gain,
     * and increase the cost of the subsequent mana pool spell.
     */
    public void manaPoolSpell() {
        if (this.currentMana >= this.currentManaPoolCost) {
            this.currentMana -= this.currentManaPoolCost;
            this.currentManaPoolCost += this.manaPoolCostIncreasePerUse;
            this.maxMana = this.maxMana * this.manaPoolCapMulitpler;
            this.passiveManaGain += this.passiveManaGain*(this.manaGainedMulitpler - 1);
        }
    }   
 
    /**
     * Decrease the player's mana. This can occur when placing/upgrading towers, 
     * casting the mana pool spell or banishing monsters.
     * 
     * @param cost The amount of mana taken from the player.
     */
    public void decreaseMana(float cost) {
        this.currentMana -= cost;
        // Check if player lost
        if (this.currentMana < 0) {
            this.player_lost = true;
            // To simply display 0
            this.currentMana = 0;
        }
        
    }

    /**
     * Increase the player's mana.
     * 
     * @param amount The amount of mana to add to the player.
     */
    public void increaseMana(float amount) {
        if (this.currentMana < this.maxMana) {
            float remainingManaToGain = this.maxMana - this.currentMana;
            this.currentMana += Math.min(remainingManaToGain, amount);
        }
    }

    public float getCurrentManaPoolCost() {
        return this.currentManaPoolCost;
    }

    public float getMaxMana() {
        return this.maxMana;
    }

    public ArrayList<Button> getButtons() {
        return this.buttons;
    }

    public ArrayList<Tower> getTowers() {
        return this.towers;
    }

    /**
     * Checks if the player has lost the game.
     * 
     * @return <code>true</code> if the player has lost, <code>false</code> otherwise.
     */
    public boolean playerLost() {
        return this.player_lost;
    }
    
}
