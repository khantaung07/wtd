package WizardTD;

import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

public class MapTest {
    private Appstub app; 

    @BeforeEach
    public void setup() {
        app = new Appstub();
        app.configPath = "testconfig.json";
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.player.mute();
        app.delay(1000);
    }


    // Test layout array is not null
    @Test
    public void testMapNull() {
        // Test if the layout array is not null
        assertNotNull(app.gameboard.getLayoutArray());

        // Test if the tiles are created correctly
        Tile[][] tiles = app.gameboard.getTiles();
        assertNotNull(tiles);
        // Test if correct number of tiles have been created
        assertEquals(App.BOARD_WIDTH, tiles.length);
        assertEquals(App.BOARD_WIDTH, tiles[0].length);

        // Test if each tile has been initialized
        for (int x = 0; x < App.BOARD_WIDTH; x++) {
            for (int y = 0; y < App.BOARD_WIDTH; y++) {
                assertNotNull(tiles[x][y]);
            }
        }
    }

    // Test specific tile locations (using level1.txt)
    @Test
    public void testMapLevel1() {
        // Check tile types for a few tiles
        assertEquals(app.gameboard.getTiles()[0][3].getType(), "path");
        assertEquals(app.gameboard.getTiles()[19][0].getType(), "shrub");
        assertEquals(app.gameboard.getTiles()[9][8].getType(), "wizard");
        assertEquals(app.gameboard.getTiles()[19][19].getType(), "grass");

    }

    // Edge case where paths are all on outer edge of map
    @Test
    public void testMapEdgePaths() {
        // Use edge map in config file (level5.txt)
        app = new Appstub();
        this.app.configPath = "edgeconfig.json";
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.player.mute();
        app.delay(1000);

        // Check corners
        assertEquals(app.gameboard.getTiles()[0][0].getType(), "path");
        assertEquals(app.gameboard.getTiles()[19][0].getType(), "path");
        assertEquals(app.gameboard.getTiles()[0][19].getType(), "path");
        assertEquals(app.gameboard.getTiles()[19][19].getType(), "path");

        // Check correct rotation (path 3 - connected to all paths)
        assertEquals(app.gameboard.getTiles()[0][0].getSprite(), this.app.images.get("path3"));
        assertEquals(app.gameboard.getTiles()[19][0].getSprite(), this.app.images.get("path3"));
        assertEquals(app.gameboard.getTiles()[0][19].getSprite(), this.app.images.get("path3"));
        assertEquals(app.gameboard.getTiles()[19][19].getSprite(),this.app.images.get("path3"));

    }

    @Test
    public void testMapEdgePaths2() {
        // Use edge map 2 in config file (level6.txt)
        app = new Appstub();
        this.app.configPath = "edgeconfig2.json";
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.player.mute();
        app.delay(1000);

        // Check corners
        assertEquals(app.gameboard.getTiles()[0][0].getType(), "path");
        assertEquals(app.gameboard.getTiles()[19][0].getType(), "path");
        assertEquals(app.gameboard.getTiles()[0][19].getType(), "path");
        assertEquals(app.gameboard.getTiles()[19][19].getType(), "path");

    }




    // Test correct layout array translates to correct tile creates
    @Test 
    public void layoutArrayToTiles() {
         // Iterate through every tile
         for (int x = 0; x < App.BOARD_WIDTH; x++) {
            for (int y = 0; y < App.BOARD_WIDTH; y++) {
                if (app.gameboard.getLayoutArray()[x][y] == ' ' || app.gameboard.getLayoutArray()[x][y] =='\0') {
                    assertEquals(app.gameboard.getTiles()[x][y].getType(), "grass");
                }
                else if (app.gameboard.getLayoutArray()[x][y] == 'S') {
                    assertEquals(app.gameboard.getTiles()[x][y].getType(), "shrub");
                }
                else if (app.gameboard.getLayoutArray()[x][y] == 'X') {
                    assertEquals(app.gameboard.getTiles()[x][y].getType(), "path");
                }
                else if (app.gameboard.getLayoutArray()[x][y] == 'W') {
                    assertEquals(app.gameboard.getTiles()[x][y].getType(), "wizard");
                }
            }
            
        }
    }

    // Test drawing methods are run
    @Test
    public void testDrawMap() {

        app = new Appstub();
        app.configPath = "testconfig.json";
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.player.mute();
        app.delay(1000);

        // Test drawing methods
        app.gameboard.drawBoard(app);
        app.gameboard.drawWizard(app);
        

    }

    // Test getting correct tile from mouse coordinates
    @Test
    public void testTileMouse() {

        assertEquals(this.app.gameboard.getTiles()[0][0], app.gameboard.getTileMouse(0, 0 + App.TOPBAR));
        assertEquals(this.app.gameboard.getTiles()[0][1], app.gameboard.getTileMouse(0, 0 + App.TOPBAR + 40));
        assertEquals(this.app.gameboard.getTiles()[6][7], app.gameboard.getTileMouse(6*App.CELLSIZE + 5, 7*App.CELLSIZE + App.TOPBAR + 5));
        assertEquals(this.app.gameboard.getTiles()[19][18], app.gameboard.getTileMouse(19*App.CELLSIZE + 5, 18*App.CELLSIZE + App.TOPBAR + 5));
        assertNull(app.gameboard.getTileMouse(-1, -1));
        
    }

    // Test correct shortest path
    @Test
    public void pathTest() {
        // Load in map with simple layout
        app = new Appstub();
        app.configPath = "simpleWizard.json";
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.player.mute();
        app.delay(1000);

        Path test = new Path(0, 1, app.gameboard.getLayoutArray(), app.gameboard);

        assertEquals(test.getSteps().get(0).getX(), 0);
        assertEquals(test.getSteps().get(0).getY(), 1);
        assertEquals(test.getSteps().get(1).getX(), 1);
        assertEquals(test.getSteps().get(1).getY(), 1);

    }

}
