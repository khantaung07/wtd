package WizardTD;

import processing.core.PApplet;

/**
 * The Button represents a graphical button in the game that takes user
 * input to perform various tasks based on its assigned "shorthand."
 */
public class Button extends Drawable {
    /**
     * A string used to denote the type and purpose of the button.
     */
    private String shorthand; 
    private String description;
    private int[] fill;
    private boolean pressed;
    /**
     * A boolean storing the previous state of the mouse press in the previous frame.
     */
    private boolean  wasMousePressed = false; 
      /**
     * A boolean storing the previous state of the key press in the previous frame.
     */
    private boolean wasKeyPressed = false; 
    private App app;
    
    /**
     * Constructor to create a new Button instance.
     *
     * @param x           The x-coordinate of the button's position.
     * @param y           The y-coordinate of the button's position.
     * @param shorthand   A shorthand code to denote the button type.
     * @param description The description of the button's function to be displayed on screen.
     * @param app         A reference to the main application.
     */
    public Button(int x, int y, String shorthand, String description, App app) {
        super(x, y);
        this.shorthand = shorthand;
        this.description = description;
        this.pressed = false;
        this.fill = new int[] {136,116,76};
        this.app = app;
    }
    /**
     * Draws the button on the screen with its current state and properties.
     *
     * @param app The PApplet object for rendering.
     */
   @Override
    public void draw(PApplet app) {
        // Draw the square
        app.fill(this.fill[0], this.fill[1], this.fill[2]);
        app.rect(this.x, this.y, 40, 40); 

        // Set text size and alignment for shorthand
        app.textAlign(App.LEFT, App.TOP);
        app.textSize(24); 
        
        // Draw big letters inside the square
        app.fill(0);
        app.text(this.shorthand, this.x + 5, this.y + 5);

        // Set text size and alignment for description
        app.textAlign(App.LEFT, App.TOP);
        app.textSize(12);

        // Draw the description next to the square
        app.fill(0);
        app.text(this.description, this.x + 45, this.y + 2, App.WIDTH, App.HEIGHT);



    }
    /**
     * Updates the button's state and behavior based on user interactions.
     * This includes changing colour when the mouse is hovering over the button 
     * and displaying tooltips for the mana cost of certain user action.
     * @see WizardTD.Button#buttonAction buttonAction
     */
    public void tick() {

       updatePress(this.app);

        // Change colour to grey if hovering
        if (isMouseOver(this.app)) {
        this.fill[0] = 200;
        this.fill[1] = 200;
        this.fill[2] = 200;
        
        // If the button is a Tower button or Mana button display cost
        if (this.shorthand.equals("T")) {
            this.app.fill(255);
            this.app.strokeWeight((float)0.5);
            this.app.rect(this.x - 72, this.y, 60, 16);

            float initCost = app.config.getFloat("tower_cost");
            if (app.gui.getUpgradeDamage()) {
                initCost += 20;
            }
            if (app.gui.getUpgradeRange()) {
                initCost += 20;
            }
            if (app.gui.getUpgradeSpeed()) {
                initCost += 20;
            }
            
            this.app.textSize(11);
            this.app.fill(0);
            this.app.text("Cost: " + (int)initCost, this.x - 67, this.y + 2, 100, 100);
        }
        else if (this.shorthand.equals("M")) {

            this.app.fill(255);
            this.app.strokeWeight((float)0.5);
            this.app.rect(this.x - 72, this.y, 60, 16);
            
            this.app.textSize(11);
            this.app.fill(0);
            this.app.text("Cost: " + (int)this.app.gui.getCurrentManaPoolCost(), this.x - 67,
             this.y + 2, 100, 100);
        }

       }
       // Default colour
       else {
        this.fill[0] = 136;
        this.fill[1] = 116;
        this.fill[2] = 76;
       }

       // Change colour if pressed
       if (pressed) {
        if (!this.shorthand.equals("M")) {
        this.fill[0] = 255;
        this.fill[1] = 255;
        this.fill[2] = 0;
        }
        }
    
        // Performs button's method
        this.buttonAction(this.app);
    
        // Update the previous mouse press flag
        wasMousePressed = app.mousePressed;
        wasKeyPressed = app.keyPressed;

    }

    /**
     * Checks if the mouse is currently over the button.
     *
     * @param app The App object for mouse position checking.
     * @return true if the mouse is over the button, false otherwise.
     */
    public boolean isMouseOver(App app) {
        return (app.mouseX >= this.x && app.mouseX <= this.x + 40 && app.mouseY >= this.y
         && app.mouseY <= this.y + 40); // 40 is button height and width
    }

    /**
     * Updates the button's press state based on mouse and key
     * interactions. Different keys toggle different buttons.
     *
     * @param app The App object for mouse press handling.
     */
    public void updatePress(App app) {
        if (isMouseOver(app) && app.mousePressed && !wasMousePressed) {
            pressed = !pressed;
        } 

        // Handles key presses
        if (app.keyPressed && !wasKeyPressed) {
            if (this.shorthand.equals("FF") && app.key == 'f') {
                pressed = !pressed;   
            }
            else if (this.shorthand.equals("P") && app.key == 'p') {
                pressed = !pressed;   
            }
            else if (this.shorthand.equals("T") && app.key == 't') {
                pressed = !pressed;   
            }
            else if (this.shorthand.equals("U1") && app.key == '1') {
                pressed = !pressed;   
            }
            else if (this.shorthand.equals("U2") && app.key == '2') {
                pressed = !pressed;   
            }
            else if (this.shorthand.equals("U3") && app.key == '3') {
                pressed = !pressed;   
            }
            else if (this.shorthand.equals("M") && app.key == 'm') {
                pressed = !pressed;   
            }
        }
    }

   /**
     * Performs different actions that affect the game state depending 
     * on the button's shorthand code. These actions in include starting and choosing the gamemode,
     * pausing the game, fastforwarding the game, placing towers, upgrading towers,
     * casting the mana pool spell and muting game music.
     *
     * @param app The App object for handling button actions.
     */
    public void buttonAction(App app) {
        if (this.shorthand.equals("FF")) {
            if (this.pressed) {
                app.fastforward();
            }
            else {
                app.resetFastForward();
            }
        }
        else if (this.shorthand.equals("P")) {
            if (this.pressed) {
                app.pause();
            }
            else {
                app.unpause();
            }
        }
        else if (this.shorthand.equals("T")) {

            if (this.pressed) {
                
                // If mouse is pressed again, create and place tower

                if (app.mousePressed) {
                    
                float initCost = app.config.getFloat("tower_cost");
                float initRange = app.config.getFloat("initial_tower_range");
                float initSpeed = app.config.getFloat("initial_tower_firing_speed");
                float initDamage = app.config.getFloat("initial_tower_damage");
                
                /** Uses mouseReleased variable to determine if a tower can be placed
                 *  mouseReleased is set to false after first click, 
                 * therefore until mouse is next released, it will stay false
                 *  This prevents multiple presses from mouse being held down
                 * Also check if player has enough mana for tower
                 */
                if (app.mouseReleased && (app.gui.getCurrentMana() >= initCost)) {

                // Find the x and y coordinates of the current mouse position
                Tile current_tile =  app.gameboard.getTileMouse(app.mouseX, app.mouseY);
                // Ensure the tile is a valid tile
                if (current_tile != null && (current_tile.getSprite().equals(app.images.get("grass")))
                    && (!current_tile.hasTower())) {
                    
                    Tower newTower = new Tower(app, current_tile.getX(), current_tile.getY(), initCost, 
                    initRange, initSpeed, initDamage);
                    app.gui.addTower(newTower);
                    current_tile.placeTower();

                    app.gui.decreaseMana(initCost);
                    // Upgrade tower automatically if selected
                    newTower.upgradeTower();
                }

                app.mouseReleased = false;

                }

                }

            }
                
        }
        else if (this.shorthand.equals("U1")) {
            if (this.pressed) {
                this.app.gui.upgradeRange();
            }
            else {
                this.app.gui.resetUpgradeRange();
            }
        }
        else if (this.shorthand.equals("U2")) {
            if (this.pressed) {
                this.app.gui.upgradeSpeed();
            }
            else {
                this.app.gui.resetUpgradeSpeed();
            }
        }
        else if (this.shorthand.equals("U3")) {
            if (this.pressed) {
                this.app.gui.upgradeDamage();
            }
            else {
                this.app.gui.resetUpgradeDamage();
            }
        }
        else if (this.shorthand.equals("M")) {
            if (this.pressed) {
                // Call mana pool speel
                this.app.gui.manaPoolSpell();
                this.description = "Mana pool \ncost: " + 
                Integer.toString((int)this.app.gui.getCurrentManaPoolCost());

                // Reset pressed state
                this.pressed = false;
            }
        }
        // Extension buttons
        else if (this.shorthand.equals("GO")) {
            if (this.pressed) {
                this.app.start = true;
            }
            
        }
        // Endless mode - infinity symbol
        else if (this.shorthand.equals(Character.toString('\u221e'))) {
            if (this.pressed) {
                this.app.endless = true;
                this.app.start = true;
            }
        }
        else if (this.shorthand.equals("m")) {
            if (this.pressed) {
                this.app.player.mute();
            }
            else {
                if (this.app.start) {
                    this.app.player.unmute();
                }
                
            }
        }
        }
    
    /**
     * Gets the current pressed state of the button.
     *
     * @return true if the button is pressed, false if not.
     */
    public boolean getPressed() {
        return this.pressed;
    }

    /**
     * Gets the current fill colour the the button
     * 
     * @return An integer array that describes the RGB code of the fill colour.
     */
    public int[] getFill() {
        return this.fill;
    }

    
}
