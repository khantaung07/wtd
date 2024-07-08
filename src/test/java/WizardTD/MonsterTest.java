package WizardTD;

import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

public class MonsterTest {
    private App app; 
    
    @BeforeEach
    public void setup() {
        this.app = new Appstub();
        this.app.configPath = "edgeconfig.json";
        PApplet.runSketch(new String[] { "App" }, app);
        this.app.setup();
        this.app.player.mute();
        this.app.loop();
        this.app.delay(1000);
    }
    

    // Test monster spawn pixel location (off the map)
    @Test
    public void monsterSpawn() {
        Monster left = new Monster(app, "gremlin", 0, 0, 1, 1, 1, 1);
        assertTrue(left.getXPixel() <= 0);
        assertEquals(left.getYPixel(), App.TOPBAR);

        Monster right = new Monster(app, "gremlin", 19, 0, 1, 1, 1, 1);
        assertTrue(right.getXPixel() >= (App.CELLSIZE * (App.BOARD_WIDTH)));
        assertEquals(right.getYPixel(), App.TOPBAR);

        Monster top = new Monster(app, "gremlin", 1, 0, 1, 1, 1, 1);
        assertEquals(top.getXPixel(),App.CELLSIZE);
        assertTrue(top.getYPixel() <= App.TOPBAR);

        Monster bottom = new Monster(app, "gremlin", 1, 19, 1, 1, 1, 1);
        assertEquals(bottom.getXPixel(), App.CELLSIZE);
        assertTrue(bottom.getYPixel() >= App.HEIGHT);

    }

    // Test monster correct sprite
    @Test
    public void monsterSprite() {
        Monster gremlin = new Monster(app, "gremlin", 0, 0, 1, 1, 1, 1);
        assertEquals(gremlin.getSprite(), app.images.get("gremlin"));

        Monster beetle = new Monster(app, "beetle", 0, 0, 1, 1, 1, 1);
        assertEquals(beetle.getSprite(), app.images.get("beetle"));

        Monster worm = new Monster(app, "worm", 0, 0, 1, 1, 1, 1);
        assertEquals(worm.getSprite(), app.images.get("worm"));
    }
    

    // Test monster speed
    @Test
    public void monsterSpeed() {
        Monster test = new Monster(app, "gremlin", 19, 0, 1, 2, 1, 1);
        float startX = test.getXPixel();
        float startY = test.getYPixel();
        
        // Find monster's next destination (1st step)
        Tile destinationTile = test.getSteps().get(0);

        // Calculate the distance between the current position and the destination tile
        double deltaX = destinationTile.getXPixel() - test.getXPixel();
        double deltaY = destinationTile.getYPixel() - test.getYPixel();
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        double ratio = (test.getSpeed() / distance);

        // Calculated expected coordinates after 1 frame
        float ExpectedFinalX = ((float)(startX + (ratio * deltaX)));
        float ExpectedFinalY = ((float)(startY + (ratio * deltaY)));

        // Perform the movement
        test.tick();
        float ActualFinalX = (test.getXPixel());
        float ActualFinalY = (test.getYPixel());

        // Check they are the same
        assertEquals(ActualFinalX, ExpectedFinalX);
        assertEquals(ActualFinalY, ExpectedFinalY);
    }

    @Test
    public void monsterFFSpeed() {
        Monster test = new Monster(app, "gremlin", 19, 0, 1, 2, 1, 1);
        float initalSpeed = test.getSpeed();

        // Update game fast forward state and update monster
        app.fastforward = true;
        test.tick();

        float doubleSpeed = test.getSpeed();

        assertEquals(doubleSpeed, 2*initalSpeed);
    }

    // Test monster changing tiles 
    @Test 
    public void monsterTileChange() {
        // 32 pixels per frame speed - should move to next tile immediately 
        Monster test = new Monster(app, "gremlin", 19, 0, 1, 32, 1, 1);

        Tile destinationTile = test.getSteps().get(1);

        int ExpectedFinalX = destinationTile.getX();
        int ExpectedFinalY = destinationTile.getY();

        // Perform the movement (twice to get to first non-spawn tile)
        test.tick();
        test.tick();
        int ActualFinalX = test.getX();
        int ActualFinalY = test.getY();

        assertEquals(ActualFinalX, ExpectedFinalX);
        assertEquals(ActualFinalY, ExpectedFinalY);

    }
    // Test monster death sprite and state
    @Test
    public void monsterDeath() {
        // Initialise dead monster
        Monster dead = new Monster(app, "gremlin", 19, 0, 0, 32, 1, 1);
        
        assertEquals(dead.getSprite(), app.images.get("gremlin"));

        // Tick death frame; Image should last 4 frames
        dead.tick();
        dead.tick();
        dead.tick();
        dead.tick();
        assertEquals(dead.getSprite(), app.images.get("gremlin1"));

        dead.tick();
        dead.tick();
        dead.tick();
        dead.tick();
        assertEquals(dead.getSprite(), app.images.get("gremlin2"));

        dead.tick();
        dead.tick();
        dead.tick();
        dead.tick();
        assertEquals(dead.getSprite(), app.images.get("gremlin3"));

        dead.tick();
        dead.tick();
        dead.tick();
        dead.tick();
        assertEquals(dead.getSprite(), app.images.get("gremlin4"));

        // Check monster is dead
        assertFalse(dead.isAlive());


    }


    // Test monster respawn method
    @Test 
    public void monsterRespawn() {
        // Load in map with quick respawn layout
        app = new Appstub();
        app.configPath = "simpleWizard.json";
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.player.mute();
        app.delay(1000);


        Monster test = new Monster(app, "gremlin", 0, 1, 1, 32, 1, 1);

        // Monster should reach wizard after two ticks 
        test.tick();
        test.tick();

        // Check monster has been respawned outside of the map
        assertTrue(test.getXPixel() < 0);

    }

    // Test monster health changing
    @Test
    public void monsterHealth() {
        Monster test = new Monster(app, "gremlin", 0, 1, 50, 32, 1, 1);
        test.updateHealth(20);
        test.drawHealth();
        assertEquals(test.getHp(), 30);
        
    }
}
