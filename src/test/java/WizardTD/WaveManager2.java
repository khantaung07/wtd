package WizardTD;

// Separate class to avoid errors in changing config and adding waves during test

import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WaveManager2 {
    private App app;

    // Test wave complete and player win conditions
    @Test 
    public void ManagerWaveComplete() {
        // Load config
        // 2 waves - one monster each
        app = new Appstub();
        app.configPath = "test2config.json";
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.player.mute();
        app.delay(1000);

        app.wavemanager.start_game();

        app.wavemanager.setFrameCounter(60);

        // Update wave
        app.wavemanager.start_game();

        // Check wave complete
        assertTrue(app.wavemanager.getWaves().get(0).isComplete());
        assertEquals(app.wavemanager.getWaveCount(), 1);

        // Spawn next monster
        app.wavemanager.setFrameCounter(120);
        app.wavemanager.start_game();

        // Check win
        // Kill monsters
        app.wavemanager.getWaves().get(0).getSpawnedMonsters().get(0).updateHealth(1000);
        app.wavemanager.getWaves().get(1).getSpawnedMonsters().get(0).updateHealth(1000);
        
        // Update states - 16 frames needed for monster to die
        int i = 0;
        while (i < 17) {
            app.wavemanager.start_game();
            i++;
        }

        assertTrue(app.wavemanager.getWaves().get(0).getSpawnedMonsters().size() == 0);
        assertTrue(app.wavemanager.getWaves().get(1).getSpawnedMonsters().size() == 0);
        assertTrue(app.wavemanager.getWaves().get(0).getMonstersToSpawn().size() == 0);

        assertTrue(app.wavemanager.playerWon());
        app.wavemanager.drawWin();
    }

    // Test endless move wave creation
    @Test
    public void EndlessWaveTest() {
        // Load in config
        this.app = new Appstub();
        this.app.configPath = "testconfig.json";
        PApplet.runSketch(new String[] { "App" }, this.app);
        this.app.setup();
        this.app.player.mute();
        this.app.loop();
        this.app.delay(1000);
        app.wavemanager.CreateEndlessWave();
        app.wavemanager.CreateEndlessWave();

        // Get endless waves (6 and 7th after config waves)
        Wave endless1 = app.wavemanager.getWaves().get(5);
        Wave endless2 = app.wavemanager.getWaves().get(6);

        assertEquals(endless1.getDuration(), 8);
        assertEquals(endless1.getPreWavePause(), 10);

        // Compare endless waves
        assertTrue(endless2.getTotalMonsters() - endless1.getTotalMonsters() == 5);
        assertEquals(endless2.getMonstersToSpawn().get(0).getHp() - endless1.getMonstersToSpawn().get(0).getHp(), 10);

    }
    
}
