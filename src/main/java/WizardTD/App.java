package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONObject;
import processing.event.MouseEvent;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import ddf.minim.*;

import java.util.*;

/**
 * The main application class for the Wizard Tower Defense game.
 * This class extends PApplet to create the game's graphical interface and 
 * handles the main game loop.
 */
public class App extends PApplet {

    public static final int CELLSIZE = 32;
    public static final int SIDEBAR = 120;
    public static final int TOPBAR = 40;
    public static final int BOARD_WIDTH = 20;

    public static int WIDTH = CELLSIZE*BOARD_WIDTH+SIDEBAR;
    public static int HEIGHT = BOARD_WIDTH*CELLSIZE+TOPBAR;

    public static final int FPS = 60;

    public String configPath;
    /**
     * The configuration file includes default values required for the game.
     * These include the map, default wave information, initial tower attributes 
     * and upgrade costs.
     */
    public JSONObject config; // Initialise JSONObject
    public GameBoard gameboard; // Initialise GameBoard
    
    public HashMap<String,PImage> images; // Hashmap to store String, Image pairs

    public WaveManager wavemanager; // Initialise WaveManager

    public GUI gui; // Intialise GUI

    public boolean paused; // Initalise pause state

    public boolean dead; // Intialise death state

    public boolean fastforward; // Initliase fastforward state

    public Random random = new Random();

    public boolean mouseReleased = true;

    // Extension variables
    public StartMenu startmenu;
    public boolean start;
    public boolean endless;

    public Minim minim;
    public AudioPlayer player;

   /**
     * Constructor for the App class.
     * Initializes the configuration path.
     */
    public App() {
        this.configPath = "config.json";
    }
    /**
     * Pauses the game.
     */
    public void pause() {
        this.paused = true;
    }

    /**
     * Unpauses the game.
     */
    public void unpause() {
        this.paused = false;
    }

    /**
     * Activates the "fast-forwarded" state of the game.
     */
    public void fastforward() {
        this.fastforward = true;
    }
    /**
     * Resets the "fast-forwarded" state of the game.
     */

    public void resetFastForward() {
        this.fastforward = false;
    }


    /**
     * Initialise the setting of the window size.
     */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player,
     *  enemies and map elements.
     */
	@Override
    public void setup() {


        this.config = loadJSONObject(this.configPath); // Load in config file as JSON Object

        frameRate(FPS);

        // Load images during setup

        this.images = new HashMap<>();

        this.images.put("fireball", loadImage("src/main/resources/WizardTD/fireball.png"));
        this.images.put("grass", loadImage("src/main/resources/WizardTD/grass.png"));
        this.images.put("shrub", loadImage("src/main/resources/WizardTD/shrub.png"));
        this.images.put("path0", loadImage("src/main/resources/WizardTD/path0.png"));
        this.images.put("path1",loadImage("src/main/resources/WizardTD/path1.png"));
        this.images.put("path2", loadImage("src/main/resources/WizardTD/path2.png"));
        this.images.put("path3", loadImage("src/main/resources/WizardTD/path3.png"));
        this.images.put("wizard_house", loadImage("src/main/resources/WizardTD/wizard_house.png"));
        this.images.put("beetle", loadImage("src/main/resources/WizardTD/beetle.png"));
        this.images.put("beetle1", loadImage("src/main/resources/WizardTD/beetle1.png"));
        this.images.put("beetle2", loadImage("src/main/resources/WizardTD/beetle2.png"));
        this.images.put("beetle3", loadImage("src/main/resources/WizardTD/beetle3.png"));
        this.images.put("beetle4", loadImage("src/main/resources/WizardTD/beetle4.png"));
        this.images.put("gremlin", loadImage("src/main/resources/WizardTD/gremlin.png"));
        this.images.put("gremlin1", loadImage("src/main/resources/WizardTD/gremlin1.png"));
        this.images.put("gremlin2", loadImage("src/main/resources/WizardTD/gremlin2.png"));
        this.images.put("gremlin3", loadImage("src/main/resources/WizardTD/gremlin3.png"));
        this.images.put("gremlin4", loadImage("src/main/resources/WizardTD/gremlin4.png"));
        this.images.put("worm", loadImage("src/main/resources/WizardTD/worm.png"));
        this.images.put("worm1", loadImage("src/main/resources/WizardTD/worm1.png"));
        this.images.put("worm2", loadImage("src/main/resources/WizardTD/worm2.png"));
        this.images.put("worm3", loadImage("src/main/resources/WizardTD/worm3.png"));
        this.images.put("worm4", loadImage("src/main/resources/WizardTD/worm4.png"));
        this.images.put("tower0", loadImage("src/main/resources/WizardTD/tower0.png"));
        this.images.put("tower1", loadImage("src/main/resources/WizardTD/tower1.png"));
        this.images.put("tower2", loadImage("src/main/resources/WizardTD/tower2.png"));
        this.images.put("background", loadImage("src/main/resources/WizardTD/background.png"));

        // Load audio
        this.minim = new Minim(this);
        this.player = minim.loadFile("src/main/resources/WizardTD/music.mp3");
       

        this.gameboard = new GameBoard(this); // Create new gameboard
        this.wavemanager = new WaveManager(this);
        this.gui = new GUI(this);
        this.startmenu = new StartMenu(this);
        this.paused = false;
        this.fastforward = false;
        this.mouseReleased = true;
        this.dead = false;
        this.start = false;
        this.endless = false;
        
         // Load ellipse shape
         ellipse(WIDTH/2,HEIGHT/2,100,100);
        

    }

    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(){
        
    }

    /**
     * Receive key released signal from the keyboard.
     */
	@Override
    public void keyReleased(){

    }
    
    /**
     * Receive mouse pressed signal from the user.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        
    }

    /**
     * Receive mouse released signal from the user.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        this.mouseReleased = true;

    }

    /*@Override
    public void mouseDragged(MouseEvent e) {

    }*/

    /**
     * Draw all elements in the game by current frame and updates
     * state of all game objects to create the game loop.
     */
	@Override
    public void draw() {

        //System.out.println(frameRate);
        
        this.player.setGain(-30);
        if (!player.isPlaying()) {
            player.rewind();
            player.play();
          }

        if (start) {
            // Green rectangle behind main board
            fill(147,170,132);
            noStroke();
            rect(0, 40, 640, 640);

            // Gameboard
            this.gameboard.drawBoard(this);

            this.wavemanager.start_game();

            // Draw towers and fireballs
            for (Tower tower : this.gui.getTowers()) {
                tower.draw(this);
                tower.drawFireballs();
                tower.drawRadius();
                tower.upgradeTower();
                tower.drawUpgrades();
            }

            // Draw side bars
            fill(136,116,76);
            noStroke();
            rect(0, 0, App.WIDTH, App.TOPBAR);
            rect(App.WIDTH - App.SIDEBAR, 0, App.SIDEBAR , App.HEIGHT);
            
            // Update GUI
            this.gui.draw();
            this.gui.tick(this);

            this.wavemanager.drawWaveTimer();

            for (Tower tower : this.gui.getTowers()) {
                tower.drawUpgradeTip();
            }

            // Draw Wizard's house last
            this.gameboard.drawWizard(this);

            // Win/Lose conditions
            this.wavemanager.allowRestart();
            this.wavemanager.drawWin();

            if (endless) {
                this.textSize(12);
                this.fill(0);
                this.textAlign(LEFT, BASELINE);
                this.text("ENDLESS", WIDTH - 55, HEIGHT - 10);
            }

    }
    else {
        
        this.startmenu.drawMenu();

    }
        
    }

    /**
     * Resets the game to starting state. Gamemode remains the same as 
     * what as initially chosen in the start screen (Normal/Endless).
     * 
     * @see WizardTD.StartMenu startMenu
     */
    public void resetGame() {
        boolean keepEndless = false;
        if (endless) keepEndless = true;

        this.player.close();
    
        setup();

        this.start = true;
        if (keepEndless) this.endless = true;
    }


    public static void main(String[] args) {
        PApplet.main("WizardTD.App");
    }

    /**
     * Source: https://stackoverflow.com/questions/37758061/rotate-a-buffered-image-in-java
     * @param pimg The image to be rotated
     * @param angle between 0 and 360 degrees
     * @return the new rotated image
     */
    public PImage rotateImageByDegrees(PImage pimg, double angle) {
        BufferedImage img = (BufferedImage) pimg.getNative();
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        PImage result = this.createImage(newWidth, newHeight, ARGB);
        //BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        BufferedImage rotated = (BufferedImage) result.getNative();
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        for (int i = 0; i < newWidth; i++) {
            for (int j = 0; j < newHeight; j++) {
                result.set(i, j, rotated.getRGB(i, j));
            }
        }

        return result;
    }
}
