package WizardTD;

import processing.data.JSONObject;
import processing.data.JSONArray;
import java.util.*;

/**
 * The Wave Manager controls the waves of monsters in the game, including wave spawning 
 * and timings as well as game completion.
 * @see WizardTD.Wave Wave
 */
public class WaveManager {
    private int number_of_waves;
    private int waveCount;
    private float nextWaveTimer;
    private int nextWave;
    private JSONObject config;
    private ArrayList<Wave> waves;
    private App app;
    private int frameCounter;
    private int spawnFrameCounter;
    private JSONArray waves_config;
    private boolean player_won;

    // Endless mode monster values
    private float EndlessHp = 100;
    private float EndlessSpeed = 1;
    private float EndlessArmour = 1;
    private float EndlessManaGainedOnKill = 10;
    private int EndlessQuantity = 20;

    /**
     * Constructs a new WaveManager for the game and constructs Wave objects based on 
     * the configuration file.
     *
     * @param app The main application instance.
     */
    public WaveManager(App app) {
        this.app = app;
        this.config = app.config;
        this.waves = new ArrayList<Wave>();
        this.frameCounter = 0;
        this.spawnFrameCounter = 0;
        this.waveCount = 0;
        this.nextWave = 1;
        this.nextWaveTimer = 0;
        this.waves_config = this.config.getJSONArray("waves"); // Get waves
        this.number_of_waves = this.waves_config.size();

        
        // Create waves
        for (int i = 0; i<(this.number_of_waves); i++) {
            JSONObject currentWave = waves_config.getJSONObject(i);

            double duration = currentWave.getDouble("duration");
            double pre_wave_pause = currentWave.getDouble("pre_wave_pause");
            HashMap<Monster, Integer>  monsters_quantity = new HashMap<Monster, Integer>();

            JSONArray monsters = currentWave.getJSONArray("monsters");
            // Create monsters in the current wave
            for (int j = 0; j<(monsters.size()); j++) {
                JSONObject currentMonster = monsters.getJSONObject(j); 

                String type = currentMonster.getString("type");
                float hp = currentMonster.getFloat("hp");
                float speed = currentMonster.getFloat("speed");
                float armour = currentMonster.getFloat("armour");
                float mana_gained_on_kill = currentMonster.getFloat("mana_gained_on_kill");
                int quantity = currentMonster.getInt("quantity");

                // Generate spawn points using possible spawn tiles
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
                // Pick a new spawn - Arbitrary - spawn is regenerated when spawned in Wave class
                Tile newSpawnTile = spawnTiles.get(0);

                int spawnX = newSpawnTile.getX();
                int spawnY = newSpawnTile.getY();

                Monster newMonster = new Monster(app, type, spawnX, spawnY, hp, speed, 
                armour, mana_gained_on_kill);

                monsters_quantity.put(newMonster, quantity);
            }

            // Create wave
            Wave newWave = new Wave(duration, pre_wave_pause, monsters_quantity);
            this.waves.add(newWave);

            
        }

        // Starting timer
        this.nextWaveTimer = (60*(float)(this.waves.get(0).getPreWavePause()));
  
    }  

   /**
    * Starts the game and is called every frame to update waves and monsters and the timer.
    * Also checks if the player has won or lost.
    */
    public void start_game() {

        Wave currentWave = this.waves.get(this.waveCount);
        float spawnRate = Math.round((float)(currentWave.getDuration()*App.FPS/currentWave.getTotalMonsters())); 
        float startFrame = Math.round((float)(currentWave.getPreWavePause()*App.FPS));

        // Check for correct spawn frames and that pre-wave pause has passed
        if (!app.paused && !app.dead) {
            // Account for potential even/odd modular arithmetic when FF
            if (this.app.fastforward) {
                if ((frameCounter%spawnRate == 0 || (frameCounter-1)%spawnRate == 0) && 
                spawnFrameCounter > startFrame) {
                    currentWave.spawn_monster(this.app);
                    frameCounter = 0;
                }
            }
            else {
                if (frameCounter%spawnRate == 0 && spawnFrameCounter > startFrame) {
                    currentWave.spawn_monster(this.app);
                    frameCounter = 0;
                }
            }

            // Update Wave timer

            if (this.nextWaveTimer > 0) {
                if (this.app.fastforward) {
                    this.nextWaveTimer -= 2;
                }
                else {
                    this.nextWaveTimer--;
                }
                
            }
            
            // Update frame
            if (this.app.fastforward) {
                frameCounter += 2;
                spawnFrameCounter += 2;
            }
            else {
                frameCounter++;
                spawnFrameCounter++;
            }
            
            
        }

        // Update monsters in every wave
        for (int i = 0; i < waveCount + 1; i++) {
            this.waves.get(i).wave_action(this.app);
        }

        
        // Increment wave
        if (currentWave.isComplete()) {

            if (this.app.endless) {
                CreateEndlessWave();
            }
            
            if (this.waveCount < (this.waves.size() - 1)) {
                this.waveCount++;
                spawnFrameCounter = 0;
            }
            
        }
        
        // Check next wave timer
        if (this.nextWaveTimer <= 0) {
            if (waveCount < (this.waves.size() - 1)) {
                this.nextWaveTimer = (60*(float)(currentWave.getDuration() + 
                this.waves.get(this.waveCount+1).getPreWavePause()));
            }
            if (nextWave < this.waves.size() + 1) {
                this.nextWave++;
            }
        }  
        
        // Check loss
        if (this.app.gui.playerLost()) {
            this.app.dead = true;
        }
        
        // Check win
        if (!this.app.endless && this.waveCount == this.waves.size() - 1) {
            checkWin();
        }
    
        
    }

    /**
     * Draws the wave timer on the game screen, indicating the time remaining before the next wave starts.
     */
    public void drawWaveTimer() {
        app.textAlign(App.LEFT, App.BASELINE);
        app.textSize(24);
        app.fill(0, 0, 0);
    
        if (nextWave < waves.size()+1) {
            String displayWave = "Wave " + (nextWave) + " starts: " + (int)(nextWaveTimer / 60);
            app.text(displayWave, 10, 30);
        }
    }
    
    /**
     * Retrieves the list of waves in the game.
     *
     * @return An array list of Wave objects.
     */
    public ArrayList<Wave> getWaves() {
        return this.waves;
    }
    
    /**
     * Checks if the player has won the game by killing every monster in all waves.
     */
    public void checkWin() {
        this.player_won = true; // Assume the player won
    
        for (int i = 0; i < this.waves.size(); i++) {
            if (!this.waves.get(i).getSpawnedMonsters().isEmpty() || 
            !this.waves.get(i).getMonstersToSpawn().isEmpty()) {
                this.player_won = false;
                break; // There are still monsters, player has not won
            }
        }
    }

    /**
     * Draws "YOU WIN!" to the screen when the player wins the game.
     */
    public void drawWin() {
        if (this.player_won) {
            this.app.fill(255, 255, 0);
            this.app.textSize(100);
            this.app.textAlign(App.CENTER, App.CENTER);
            this.app.text("YOU WIN!", App.CELLSIZE*App.BOARD_WIDTH/2, App.CELLSIZE*App.BOARD_WIDTH/2);
        }
    }
    
    /**
     * Allows the player to restart the game by pressing 'r' after losing.
     */
    public void allowRestart() {

        if (this.app.dead) {

            this.app.fill(255, 0, 0);
            this.app.textSize(100);
            this.app.textAlign(App.CENTER, App.CENTER);
            this.app.text("YOU LOST!", App.CELLSIZE*App.BOARD_WIDTH/2, App.CELLSIZE*App.BOARD_WIDTH/2);
            this.app.textSize(24);
            this.app.text("Press 'r' to restart",App.CELLSIZE*App.BOARD_WIDTH/2, 
            App.CELLSIZE*App.BOARD_WIDTH/2 - 80);

            if (app.keyPressed && app.key == 'r') {
                // reset game();
                app.resetGame();
            }
        }
    }

    public float getTimer() {
        return this.nextWaveTimer;
    }
    
    /**
     * Creates a wave to be used in 'Endless mode' with progressively stronger monsters.
     */
    public void CreateEndlessWave() {
        float duration = 8;
        float pre_wave_pause = 10;
        HashMap<Monster, Integer>  monsters_quantity = new HashMap<Monster, Integer>();

        // Create random monster
        String[] monsters = {"gremlin", "beetle", "worm"};
        Random random = new Random();
        String type = monsters[random.nextInt(3)];

        // Generate spawn points using possible spawn tiles
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
        // Pick a new spawn - Arbitrary - spawn is regenerated when spawned in Wave class
        Tile newSpawnTile = spawnTiles.get(0);

        int spawnX = newSpawnTile.getX();
        int spawnY = newSpawnTile.getY();

        Monster newMonster = new Monster(app, type, spawnX, spawnY, EndlessHp, 
        EndlessSpeed, EndlessArmour, EndlessManaGainedOnKill);

        monsters_quantity.put(newMonster, EndlessQuantity);

        // Create wave
        Wave newWave = new Wave(duration, pre_wave_pause, monsters_quantity);
        this.waves.add(newWave);

        // Update endless values

        this.EndlessHp += 10;
        this.EndlessSpeed += 0.1;
        if (this.EndlessArmour > 0.1) this.EndlessArmour -= 0.02;
        this.EndlessManaGainedOnKill += 3;
        this.EndlessQuantity += 5;

    }

    /**
     * Sets the frame counter to a specific value.
     *
     * @param frame The value to set the frame counter to.
     */
    public void setFrameCounter(int frame) {
        this.frameCounter = frame;
        this.spawnFrameCounter = frame;
    }

    /**
     * Gets the current wave index.
     *
     * @return The current wave index.
     */
    public int getWaveCount() {
        return this.waveCount;
    }

    public boolean playerWon() {
        return this.player_won;
    }

}
