package WizardTD;

import processing.core.PImage;

import java.util.*;

/**
 * The Start Menu is what is first launched when the game opens and allows the
 * user to choose the game mode. Music is played and an artwork is displayed.
 */
public class StartMenu {
    private PImage background;
    private App app;
    private ArrayList<Button> buttons;

    /**
     * Constructor to create a start menu. Loads in the background image and 
     * creates two buttons; start game, endless mode.
     * @param app The main application instance.
     */
    public StartMenu(App app) {
        this.background = app.images.get("background");
        this.background.resize(App.WIDTH, App.HEIGHT);
        this.app = app;
        this.buttons = new ArrayList<Button>();

        Button start = new Button(App.WIDTH - 50, App.WIDTH/2 + 180, "GO",
         "", this.app);

        Button endless = new Button(App.WIDTH - 50, App.WIDTH/2 + 230,
         Character.toString('\u221e'), "", this.app);
         
        this.buttons.add(start);
        this.buttons.add(endless);
    }

    /**
     * Draws the menu to the screen, including the buttons and
     * displaying information to the player.
     */
    public void drawMenu() {
        this.app.image(this.background, 0, 0);
        this.app.fill(255,255,0);
        this.app.textSize(60);
        this.app.textAlign(App.LEFT, App.CENTER);
        this.app.text("Wizard Tower Defence", App.BOARD_WIDTH/2 + 40, 50);
        this.app.textSize(30);
        this.app.text("Start Game", App.WIDTH - 215, App.WIDTH/2 + 195);
        this.app.fill(255,0,0);
        this.app.text("Endless Mode", App.WIDTH - 253, App.WIDTH/2 + 245);
       
        for (Button button: this.buttons) {
            button.tick();
            button.draw(this.app);
        }

    }

    
    
}
