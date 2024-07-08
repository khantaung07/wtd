package WizardTD;

import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ButtonTest {
    private App app;

    @BeforeAll
    public void setup() {
        this.app = new Appstub();
        this.app.configPath = "testconfig.json";
        this.app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        this.app.setup();
        this.app.delay(1000);
    }

    // Simple hover and click test
    @Test
    public void HoverClickButton() {
        Button test = new Button(0, 0, "test", "", app);
        app.mouseX = 1;
        app.mouseY = 1;

        assertTrue(test.isMouseOver(app));
        // Test colour

        test.tick();

        assertEquals(test.getFill()[0], 200);
        assertEquals(test.getFill()[1], 200);
        assertEquals(test.getFill()[2], 200);
        
        // Test mouse press
        app.mousePressed = true;

        test.tick();

        assertTrue(test.getPressed());
    }

    // Test tower button
   @Test
    public void TowerButton() {
        this.app.start = true;
        // Create button (on map, so tower can be placed at same coordinate)
        Button test = new Button((int)app.gameboard.getTile(0,2).getXPixel() + 5, (int)app.gameboard.getTile(0,2).getYPixel() + 5, "T", "", app);

        // Check inital towers none
        assertEquals(this.app.gui.getTowers().size(), 0);

        // Move mouse to button
        app.mouseX = (int)app.gameboard.getTile(0,2).getXPixel() + 5;
        app.mouseY = (int)app.gameboard.getTile(0,2).getYPixel() + 5;
        app.mousePressed = true;
        test.tick();
        
        assertTrue(test.getPressed());

        app.mousePressed = false;
        app.mouseReleased();

        // Check still pressed
        assertTrue(test.getPressed());

        // Update mouse
        app.mousePressed = true;
        test.tick();

        // Check tower has been placed
        assertEquals(this.app.gui.getTowers().size(), 1);

    }

    @Test
    public void BasicButton() {
        // Set button states to default
        this.app.fastforward = false;
        this.app.paused = false;
        this.app.gui.resetUpgradeDamage();
        this.app.gui.resetUpgradeSpeed();
        this.app.gui.resetUpgradeRange();
        
        // Stack all buttons on one location and ensure they work
        Button ff = new Button(0, 0, "FF", "2x speed", app);
        Button p = new Button(0,0, "P" , "PAUSE", app);
        Button u1 = new Button(0,0, "U1" , "Upgrade \nrange", app);
        Button u2 = new Button(0,0, "U2" , "Upgrade \nspeed", app);
        Button u3 = new Button(0, 0, "U3" , "Upgrade \ndamage", app);
        Button m = new Button(0,0, "M" , "Mana pool \ncost: " + Integer.toString((int)this.app.gui.getCurrentManaPoolCost()), app);
        Button mute = new Button(0,0, "m" , "Mute \nmusic", app);

        // Press buttons
        app.mouseX = 5;
        app.mouseY = 5;
        app.mousePressed = true;

        // Update button states
        ff.tick();
        p.tick();
        u1.tick();
        u2.tick();
        u3.tick();
        m.tick();
        mute.tick();

        // Check necessary values have been updated 
        assertTrue(this.app.fastforward);
        assertTrue(this.app.paused);
        assertTrue(this.app.gui.getUpgradeDamage());
        assertTrue(this.app.gui.getUpgradeRange());
        assertTrue(this.app.gui.getUpgradeSpeed());
        assertTrue(this.app.gui.getUpgradeDamage());
        assertTrue(this.app.gui.getMaxMana() > app.config.getFloat("initial_mana_gained_per_second"));
        assertTrue(this.app.player.isMuted());

    }

    // Test button function using key presses
    @Test
    public void BasicButtonKey() {
        // Set button states to default
        this.app.fastforward = false;
        this.app.paused = false;
        this.app.gui.resetUpgradeDamage();
        this.app.gui.resetUpgradeSpeed();
        this.app.gui.resetUpgradeRange();

        Button ff = new Button(0, 0, "FF", "2x speed", app);
        Button p = new Button(0,0, "P" , "PAUSE", app);
        Button u1 = new Button(0,0, "U1" , "Upgrade \nrange", app);
        Button u2 = new Button(0,0, "U2" , "Upgrade \nspeed", app);
        Button u3 = new Button(0, 0, "U3" , "Upgrade \ndamage", app);
        Button m = new Button(0,0, "M" , "Mana pool \ncost: " + Integer.toString((int)this.app.gui.getCurrentManaPoolCost()), app);

        // Press keys
        app.keyPressed = true;
        app.key = 'f';
        ff.tick();
        app.key = 'p';
        p.tick();
        app.key = '1';
        u1.tick();
        app.key = '2';
        u2.tick();
        app.key = '3';
        u3.tick();
        app.key = 'm';
        m.tick();
        app.key = 't';

        // Check necessary values have been updated 
        assertTrue(this.app.fastforward);
        assertTrue(this.app.paused);
        assertTrue(this.app.gui.getUpgradeDamage());
        assertTrue(this.app.gui.getUpgradeRange());
        assertTrue(this.app.gui.getUpgradeSpeed());
        assertTrue(this.app.gui.getUpgradeDamage());
        assertTrue(this.app.gui.getMaxMana() > app.config.getFloat("initial_mana_gained_per_second"));

    }
    
}
