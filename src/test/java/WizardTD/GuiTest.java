package WizardTD;

import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

public class GuiTest {
    private App app;

    @BeforeEach
    public void setup() {
        app = new Appstub();
        app.configPath = "testconfig.json";
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.player.mute();
        app.loop();
        app.delay(1000);
    }

    // Test constructor
    @Test
    public void testConstructor() {
        // Test config values are loaded in correctly
        assertEquals(this.app.gui.getCurrentMana(), app.config.getFloat("initial_mana"));
        assertEquals(this.app.gui.getMaxMana(), app.config.getFloat("initial_mana_cap"));
        assertEquals(this.app.gui.getCurrentManaPoolCost(), app.config.getFloat("mana_pool_spell_initial_cost"));

        // Check all buttons are initialised
        assertEquals(this.app.gui.getButtons().size(), 8);
        // Check no towers
        assertTrue(this.app.gui.getTowers().isEmpty());

    }

    // Test drawing
    @Test
    public void testDraw() {
        this.app.gui.draw();
    }

    // Test mana pool spell 
    @Test
    public void ManaPoolSpellTest() {
        float startMaxMana = app.gui.getMaxMana();
        float startPassiveMana = app.gui.getPassiveMana();
        float startManaPoolCost = app.gui.getCurrentManaPoolCost();
        this.app.gui.manaPoolSpell();
        float endMaxMana = app.gui.getMaxMana();
        float endPassiveMana = app.gui.getPassiveMana();
        float endManaPoolCost = app.gui.getCurrentManaPoolCost();

        // Test expected values
        assertTrue(Math.round(endMaxMana) == Math.round(app.config.getFloat("mana_pool_spell_cap_multiplier")*startMaxMana));

        assertTrue(Math.round(endPassiveMana) == Math.round(startPassiveMana + 
        (startPassiveMana*(app.config.getFloat("mana_pool_spell_mana_gained_multiplier") - 1))));

        assertTrue(Math.round(endManaPoolCost) == Math.round(startManaPoolCost + 
        app.config.getFloat("mana_pool_spell_cost_increase_per_use")));
    }

    // Passive mana FF test
    @Test
    public void PassiveMana() {
        float startMana = app.gui.getCurrentMana();

        // Toggle fast forward
        app.key = 'f';
        app.keyPressed = true;

        // Tick 30 frames (half a second)
        int i = 0;
        while (i < 31) {
            app.gui.tick(app);
            i++;
        }

        float endMana = app.gui.getCurrentMana();
        assertEquals(endMana, startMana + app.config.getFloat("initial_mana_gained_per_second"));


    }
    
}
