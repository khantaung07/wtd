package WizardTD;

import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.util.*;

public class WaveTest {

    private App app; 
    
    @BeforeEach
    public void setup() {
        this.app = new Appstub();
        app.configPath = "edgeconfig.json";
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.player.mute();
        app.delay(1000);
    }

    @AfterEach
    public void close() {
        app.wavemanager.getWaves().clear();
        app = null;
    }
    
    // Default test to ensure wave is constructed correctly
    @Test
    public void testConstructor() {

        HashMap<Monster, Integer>  monsters_quantity = new HashMap<Monster, Integer>();

        Monster test = new Monster(app, "gremlin", 19, 0, 1, 1, 1, 1);

        monsters_quantity.put(test, 10);

        Wave testWave = new Wave(5, 5, monsters_quantity);

        // Test different elements of the intial wave
        assertEquals(testWave.getDuration(), 5);
        assertEquals(testWave.getPreWavePause(), 5);
        assertEquals(testWave.getTotalMonsters(), 10);
        assertTrue(testWave.getSpawnedMonsters().isEmpty());
        assertEquals(testWave.getMonstersToSpawn().size(), 10);
        assertEquals(testWave.getMonstersToSpawn().get(0).getType(), "gremlin");
        assertFalse(testWave.hasStarted());
        assertFalse(testWave.isComplete());

    }

    // Test Wave spawning
    @Test 
    public void spawnMonster() {
        HashMap<Monster, Integer>  monsters_quantity = new HashMap<Monster, Integer>();

        Monster test = new Monster(app, "gremlin", 19, 0, 1, 1, 1, 1);

        monsters_quantity.put(test, 2);

        Wave testWave = new Wave(5, 5, monsters_quantity);

        testWave.spawn_monster(app);

        assertTrue(testWave.hasStarted());
        assertEquals(testWave.getSpawnedMonsters().size(), 1);
        assertEquals(testWave.getSpawnedMonsters().get(0).getType(), "gremlin");
        assertEquals(testWave.getMonstersToSpawn().size(), 1);

        // Spawn last monster
        testWave.spawn_monster(app);
        assertEquals(testWave.getSpawnedMonsters().size(), 2);
        assertTrue(testWave.getMonstersToSpawn().isEmpty());

        // Check complete
        testWave.spawn_monster(app);
        assertTrue(testWave.isComplete());

    }

}
