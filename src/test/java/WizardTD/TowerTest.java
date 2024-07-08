package WizardTD;

import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) 
public class TowerTest {
    private App app;

    @BeforeAll
    public void setup() {
        this.app = new Appstub();
        this.app.configPath = "testconfig.json";
        PApplet.runSketch(new String[] { "App" }, app);
        this.app.setup();
        this.app.player.mute();
        this.app.loop();
        this.app.delay(1000);
    }

    // Test constructor
    @Test
    public void TowerConstructor() {
        Tower test = new Tower(app, 0, 1, 100, 96, 5, 10);
        assertNotNull(test);
    }

    // Test finding monster
    @Test
    public void TestFindingMonster() {
        this.app.start = true;

        Tower test = new Tower(app, 0, 2, 100, 96, 5, 10);
        
        // Create monster in range and add to wave
        Monster testMonster = new Monster(app, "gremlin", 0, 3, 10, 1, 1, 1);
        this.app.wavemanager.getWaves().get(0).getSpawnedMonsters().add(testMonster);

        testMonster.tick();
        test.tick(app);

        // Test it can detect monster in range
        assertNotNull(test.getTarget());

    }

    // Test upgrading
    @Test 
    public void TowerUpgrade() {
        // Increase mana to allow upgrades
        this.app.gui.increaseMana(1000);

        // Set upgrade states to true
        app.gui.upgradeDamage();
        app.gui.upgradeRange();
        app.gui.upgradeSpeed();

        Tower test = new Tower(app, 0, 2, 100, 96, 5, 10);

        // Move mouse and click - radius is also drawn
        app.mouseX = (int)test.getXPixel() + 5;
        app.mouseY = (int)test.getYPixel() + 5;
        app.mousePressed = true;

        // Update tower state
        test.tick(app);
        test.drawRadius();
        test.upgradeTower();

        // Check if upgraded
        assertTrue(test.getSpeed() > 0);
        assertTrue(test.getDamage() > 0);
        assertTrue(test.getRange() > 0);

        // Draw upgrade
        test.drawUpgradeTip();
        test.drawUpgrades();
        
        
        // Test further levels
        test.setRange(2);
        // Draw upgrade
        test.drawUpgradeTip();
        test.drawUpgrades();

        test.setSpeed(2);
        // Draw upgrade
        test.drawUpgradeTip();
        test.drawUpgrades();

        test.setDamage(2);
        // Draw upgrade
        test.drawUpgradeTip();
        test.drawUpgrades();


    }
    
    @Test
    public void SimpleFireball() {
        Monster testMonster = new Monster(app, "worm", 0, 3, 1, 1, 1, 10);

        Fireball test = new Fireball(app, 0, 0, 100, testMonster);

        double xDistance = testMonster.getXPixel() - test.getXPixel();
        double yDistance = testMonster.getYPixel() - test.getYPixel();

        double initial_distance = Math.round(Math.sqrt(xDistance * xDistance + yDistance * yDistance));

        // Update fireball
        test.tick();

        // Check distance matches fireball speed (5 pixels per frame)

        xDistance = testMonster.getXPixel() - test.getXPixel();
        yDistance = testMonster.getYPixel() - test.getYPixel();

        double final_distance = Math.round(Math.sqrt(xDistance * xDistance + yDistance * yDistance));

        assertEquals(initial_distance, final_distance + 5);


    }

}
