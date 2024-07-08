package WizardTD;

import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import java.util.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) 
public class WaveManagerTest {
    private App app; 
    
    @BeforeAll
    public void setup() {
        this.app = new Appstub();
        this.app.configPath = "testconfig.json";
        PApplet.runSketch(new String[] { "App" }, this.app);
        this.app.setup();
        this.app.player.mute();
        this.app.loop();
        this.app.delay(1000);
    }

    // Test constructor and start game
    // Tick and make sure monsters are being spawned and timer is ticking down
    @Test
    public void WaveManagerCycle() {
        app.fastforward = false;

        assertEquals(app.wavemanager.getWaves().size(),5);

        Wave wave1 = app.wavemanager.getWaves().get(0);

        assertEquals(wave1.getDuration(), 8);
        assertEquals(wave1.getPreWavePause(), 3);
        assertEquals(wave1.getTotalMonsters(), 10);

        float startTime = app.wavemanager.getTimer();
        ArrayList<Monster> monstersSpawned = app.wavemanager.getWaves().get(0).getSpawnedMonsters();
        int numMonsters = monstersSpawned.size();

        assertEquals(numMonsters, 0);

        float endTime = startTime - 1;
        int expectedNumMonsters = 1;

        // Increment wave manager
        app.wavemanager.start_game();

        float actualTime = app.wavemanager.getTimer();
        
        assertEquals(endTime, actualTime);
        app.wavemanager.drawWaveTimer();

        // Set frame to spawn time
        app.wavemanager.setFrameCounter(192);

        // Start game when spawnRate is met and spawnFrame is met
        app.wavemanager.start_game();
            
        ArrayList<Monster> NewMonstersSpawned = app.wavemanager.getWaves().get(0).getSpawnedMonsters();

        int NewNumMonsters = NewMonstersSpawned.size();

        assertEquals(NewNumMonsters, expectedNumMonsters);

    }

    // Test start game when FF
    @Test
    public void ManagerTimeFF() {
        this.app.fastforward = true;
        float startTime = app.wavemanager.getTimer();
        float endTime = startTime - 2;

        // Increment wave manager
        app.wavemanager.start_game();

        float actualTime = app.wavemanager.getTimer();

        assertEquals(endTime, actualTime);

    }
    
    @Test
    public void RestartTest() {
        app.dead = true;

        // Update frame
        app.wavemanager.start_game();

        // User inputs
        app.keyPressed = true;
        app.key = 'r';
        app.wavemanager.allowRestart();

        // Check game has restarted 
        assertFalse(app.dead);


    }
}
