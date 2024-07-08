package WizardTD;

import processing.data.JSONObject;
import processing.core.PImage;

import java.io.*;
import java.util.*;

/**
 * The GameBoard represents the game board of the Wizard Tower Defense game,
 * containing information about the layout of the map and tiles and is generated 
 * via the map layout in the configuration file.
 * @see WizardTD.Tile Tile
 */
public class GameBoard {

    private int width;
    private int height;
    private Tile[][] tiles; // x,y
    private JSONObject config;
    private String layout;
    private char[][] layout_Array; // x,y 

    /**
     * Constructs a new game board using the provided App instance's width and height values.
     * Loads required images and sets the sprite and type of each tile.
     *
     * @param app The main application instance.
     */
    public GameBoard(App app) {

        this.width = App.BOARD_WIDTH;
        this.height = App.BOARD_WIDTH;
        this.tiles = new Tile[width][height];

        this.config = app.config; // Reference to config file in main App class
        this.layout = this.config.getString("layout");

        // Load required images
        PImage grass =  app.images.get("grass");
        PImage wizard_house =  app.images.get("wizard_house");
        PImage shrub =  app.images.get("shrub");
        PImage path0 =  app.images.get("path0");
        PImage path1 =  app.images.get("path1");
        PImage path2 =  app.images.get("path2");
        PImage path3 =  app.images.get("path3");


        // Initialize tiles with X and Y coordinates
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = new Tile(x, y);
            }
        }


        // 2-D Array to store map details
        this.layout_Array = new char[width][height];
        

        // Read layout file and store it to 2-D Array
        File layoutFile = new File(this.layout);
        try {
            Scanner scan = new Scanner(layoutFile);

            int y_counter = 0; // Initialize the row counter

            while (scan.hasNextLine()) {
                String line = scan.nextLine();

                for (int x = 0; x < line.length(); x++) {
                    char tileSymbol = line.charAt(x);
                    
                    if (x < this.width) {
                        this.layout_Array[x][y_counter] = tileSymbol;
                    }

            }
            // Update counter variables
            y_counter++;
            if (y_counter == this.height){
                break;
            }
                
        } 
        scan.close();
        }
        catch (FileNotFoundException e) {
            System.err.println("Map file not found.");
        }


        // Set sprites for individual tiles

        for (int x = 0; x < this.width; x++) {

            for (int y = 0; y < this.height; y++) {

                char tileSymbol = this.layout_Array[x][y];

                Tile current_tile = this.tiles[x][y];

                if (tileSymbol == ' ' || tileSymbol == '\0') {
                    // Grass
                    current_tile.setSprite(grass, "grass"); 
                } else if (tileSymbol == 'S') {
                    // Shrub
                    current_tile.setSprite(shrub, "shrub"); 
                } else if (tileSymbol == 'X') {
                    
                    // Check if tile is on an edge
                    if (current_tile.getEdge()) {
                        // Check if left
                        if (x == 0 && !(y == 0 || y == App.BOARD_WIDTH -1)) {
                            char top = this.layout_Array[x][y-1];
                            char bottom = this.layout_Array[x][y+1];
                            char right = this.layout_Array[x+1][y];

                            if (top == 'X' && bottom == 'X' && right == 'X') {
                                current_tile.setSprite(path3, "path");
                            }
                            else if (top == 'X' && bottom == 'X') {
                                current_tile.setSprite(app.rotateImageByDegrees(path2, 90), "path"); // Rotate path2
                            }
                            else if (top == 'X' && right == 'X') {
                                current_tile.setSprite(app.rotateImageByDegrees(path2, 180), "path"); // Rotate path2 
                            }
                            else if (bottom == 'X' && right == 'X') {
                                current_tile.setSprite(path2, "path");
                            }
                            else if (top == 'X') {
                                current_tile.setSprite(app.rotateImageByDegrees(path1, 90), "path"); // Rotate path1
                            }
                            else if (bottom == 'X') {
                                current_tile.setSprite(path1, "path"); 
                            }
                            else {
                                // Default case
                                current_tile.setSprite(path0, "path"); 
                            }

                         
                        }
                        // Check if right
                        else if (x == App.BOARD_WIDTH - 1 && !(y == 0 || y == App.BOARD_WIDTH -1)) {
                            char top = this.layout_Array[x][y-1];
                            char bottom = this.layout_Array[x][y+1];
                            char left = this.layout_Array[x-1][y];

                            if (top == 'X' && bottom == 'X' && left == 'X') {
                                current_tile.setSprite(path3, "path");
                            }
                            else if (top == 'X' && bottom == 'X') {
                                current_tile.setSprite(app.rotateImageByDegrees(path2, 270), "path"); // Rotate path2
                            }
                            else if (top == 'X' && left == 'X') {
                                current_tile.setSprite(app.rotateImageByDegrees(path2, 180), "path"); // Rotate path2 
                            }
                            else if (bottom == 'X' && left == 'X') {
                                current_tile.setSprite(path2, "path");
                            }
                            else if (top == 'X') {
                                current_tile.setSprite(app.rotateImageByDegrees(path1,180), "path"); // Rotate path1
                            }
                            else if (bottom == 'X') {
                                current_tile.setSprite(app.rotateImageByDegrees(path1, 270), "path"); // Rotate path1
                            }
                            else {
                                // Default case
                                current_tile.setSprite(path0, "path"); 
                            }

                        }
                        // Check if top
                        else if (y == 0 && !(x == 0 || x == App.BOARD_WIDTH -1)) {

                            char left = this.layout_Array[x-1][y];
                            char right = this.layout_Array[x+1][y];
                            char bottom = this.layout_Array[x][y+1];
                            if (right == 'X' && bottom == 'X' && left == 'X') {
                                current_tile.setSprite(path3, "path");
                            }
                            else if (right == 'X' && bottom == 'X') {
                                current_tile.setSprite(app.rotateImageByDegrees(path2, 270), "path"); // Rotate path2
                            }
                            else if (right == 'X' && left == 'X') {
                                current_tile.setSprite(app.rotateImageByDegrees(path2, 180), "path"); // Rotate path2 
                            }
                            else if (bottom == 'X' && left == 'X') {
                                current_tile.setSprite(app.rotateImageByDegrees(path2, 90), "path"); // Rotate path2
                            }
                            else if (right == 'X') {
                                current_tile.setSprite(app.rotateImageByDegrees(path1,180), "path"); // Rotate path1
                            }
                            else if (left == 'X') {
                                current_tile.setSprite(app.rotateImageByDegrees(path1,90), "path"); // Rotate path1
                            }
                            else {
                                // Default case
                                current_tile.setSprite(app.rotateImageByDegrees(path0, 90), "path"); // Rotated path
                            }

                            
                        } 

                        // Check if bottom
                        else if (y == App.BOARD_WIDTH-1 && !(x == 0 || x == App.BOARD_WIDTH -1)) {
                            char left = this.layout_Array[x-1][y];
                            char right = this.layout_Array[x+1][y];
                            char top = this.layout_Array[x][y-1];

                            if (right == 'X' && top == 'X' && left == 'X') {
                                current_tile.setSprite(path3, "path");
                            }
                            else if (right == 'X' && top == 'X') {
                                current_tile.setSprite(app.rotateImageByDegrees(path2, 270), "path"); // Rotate path2
                            }
                            else if (right == 'X' && left == 'X') {
                                current_tile.setSprite(path2, "path");
                            }
                            else if (top == 'X' && left == 'X') {
                                current_tile.setSprite(app.rotateImageByDegrees(path2, 90), "path"); // Rotate path2
                            }
                            else if (right == 'X') {
                                current_tile.setSprite(app.rotateImageByDegrees(path1, 270), "path"); // Rotate path1
                            }
                            else if (left == 'X') {
                                current_tile.setSprite(path1, "path"); 
                            }
                            else {
                                // Default case
                                current_tile.setSprite(app.rotateImageByDegrees(path0, 90), "path"); // Rotated path
                            }

                        }
                        // Top left corner
                        else if (x == 0 && y == 0) {
                            char right = this.layout_Array[x+1][y];
                            char bottom = this.layout_Array[x][y+1];
                            if (right == 'X' && bottom == 'X') {
                                current_tile.setSprite(path3, "path");
                            }
                            else if (right == 'X') {
                                current_tile.setSprite(app.rotateImageByDegrees(path2, 180), "path"); // Rotate path2 
                            }
                            else {
                                current_tile.setSprite(app.rotateImageByDegrees(path0, 90), "path"); // Rotated path
                            }

                        }
                        // Top right corner
                        else if (x == App.BOARD_WIDTH-1 && y == 0) {
                            char left = this.layout_Array[x-1][y];
                            char bottom = this.layout_Array[x][y+1];
                            if (left == 'X' && bottom == 'X') {
                                current_tile.setSprite(path3, "path");
                            }
                            else if (left == 'X') {
                                current_tile.setSprite(app.rotateImageByDegrees(path2, 180), "path"); // Rotate path2 
                            }
                            else {
                                current_tile.setSprite(app.rotateImageByDegrees(path0, 90), "path"); // Rotated path
                            }
                            

                        }
                        // Bottom left corner
                        else if (x == 0 && y == App.BOARD_WIDTH-1) {
                            char right = this.layout_Array[x+1][y];
                            char top = this.layout_Array[x][y-1];
                            if (right == 'X' && top == 'X') {
                                current_tile.setSprite(path3, "path");
                            }
                            else if (right == 'X') {
                                current_tile.setSprite(app.rotateImageByDegrees(path2, 90), "path"); // Rotate path2
                            }
                            else {
                                current_tile.setSprite(app.rotateImageByDegrees(path0, 90), "path"); // Rotated path
                            }

                        }
                        // Bottom right corner
                        else if (x == App.BOARD_WIDTH-1 && y == App.BOARD_WIDTH-1) {
                            char top = this.layout_Array[x][y-1];
                            char left = this.layout_Array[x-1][y];

                            if (top == 'X' && left == 'X') {
                                current_tile.setSprite(path3, "path");
                            }
                            else if (left == 'X') {
                                current_tile.setSprite(app.rotateImageByDegrees(path2, 90), "path"); // Rotate path2
                            }
                            else {
                                current_tile.setSprite(app.rotateImageByDegrees(path0, 90), "path"); // Rotated path
                            }

                        }
                        
                    }

                    else {
                        
                        // Check surrounding elements to determine which type of path to use and whether to rotate 
                        char left = this.layout_Array[x-1][y];
                        char right = this.layout_Array[x+1][y];
                        char top = this.layout_Array[x][y-1];
                        char bottom = this.layout_Array[x][y+1];

                        
                        if (left == 'X' && right == 'X' && top == 'X' && bottom == 'X') {
                            // All adjacent tiles are path tiles, use path3
                            current_tile.setSprite(path3, "path");

                        } else if (left == 'X' && right == 'X' && top == 'X') {
                            // Top, left, and right are path tiles, use path2
                            current_tile.setSprite(app.rotateImageByDegrees(path2, 180), "path"); // Rotate path2 

                        } else if (left == 'X' && right == 'X' && bottom == 'X') {
                            // Bottom, left, and right are path tiles, use path2
                            current_tile.setSprite(path2, "path");

                        } else if (top == 'X' && bottom == 'X' && left == 'X') {
                            // Left, top, and bottom are path tiles, use path2
                            current_tile.setSprite(app.rotateImageByDegrees(path2, 90), "path"); // Rotate path2

                        } else if (top == 'X' && bottom == 'X' && right == 'X') {
                            // Right, top, and bottom are path tiles, use path2
                            current_tile.setSprite(app.rotateImageByDegrees(path2, 270), "path"); // Rotate path2

                        } else if (top == 'X' && bottom == 'X') {
                            // Top and bottom are path tiles
                            current_tile.setSprite(app.rotateImageByDegrees(path0, 90), "path"); // Rotate path0
                            
                        } else if (top == 'X' && right == 'X') {
                            // Top and right are path tiles
                            current_tile.setSprite(app.rotateImageByDegrees(path1,180), "path"); // Rotate path1
                            
                        } else if (top == 'X' && left == 'X') {
                            // Top and left are path tiles
                            current_tile.setSprite(app.rotateImageByDegrees(path1, 90), "path"); // Rotate path1
                            
                        } else if (left == 'X' && right == 'X') {
                            // Left and right are path tiles
                            current_tile.setSprite(path0, "path");
                            
                        } else if (left == 'X' && bottom == 'X') {
                            // Left and bottom are path tiles
                            current_tile.setSprite(path1, "path"); 
                            
                        } else if (right == 'X' && bottom == 'X') {
                            // Right and bottom are path tiles
                            current_tile.setSprite(app.rotateImageByDegrees(path1, 270), "path"); // Rotate path1
                            
                        } else if (top == 'X' || bottom == 'X') {
                            // Path at top or bottom tiles
                            current_tile.setSprite(app.rotateImageByDegrees(path0, 90), "path"); // Rotate path0

                        } else {
                            // Default case, use path0
                            current_tile.setSprite(path0, "path");
                        }

                    }

                } else if (tileSymbol == 'W') {
                    // Wizard's house

                    // Avoid index errors
                    if (!current_tile.getEdge()) {

                        // Check surrounding elements to determine which way wizard house should face
                        char right = this.layout_Array[x+1][y];
                        char top = this.layout_Array[x][y-1];
                        char bottom = this.layout_Array[x][y+1];

                        if (top == 'X') {
                            current_tile.setSprite(app.rotateImageByDegrees(wizard_house, 90), "wizard"); 
                        }
                        else if (right == 'X') {
                            current_tile.setSprite(app.rotateImageByDegrees(wizard_house, 180), "wizard"); 
                        }
                        else if (bottom == 'X') {
                            current_tile.setSprite(app.rotateImageByDegrees(wizard_house, 270), "wizard"); 
                        }
                        else {
                            current_tile.setSprite(wizard_house, "wizard"); // Default - facing left
                            }
                    }
                    else {
                        current_tile.setSprite(wizard_house, "wizard"); // Default - facing left
                    }
                }
                }

            }
        }
    

    /**
     * Retrieves a specific tile at the given coordinates.
     *
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
     * @return The tile at the specified coordinates or null if out of bounds.
     * @see WizardTD.Tile
     */
    public Tile getTile(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return tiles[x][y];
        } else {
            return null; // Return null if coordinates are out of bounds
        }
    }
    /**
     * Retrieves a tile based on the mouse coordinates.
     *
     * @param x The x-coordinate of the mouse.
     * @param y The y-coordinate of the mouse.
     * @return The tile under the mouse or null if not found.
     * @see WizardTD.Tile
     */
    public Tile getTileMouse(float x, float y) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if ((x >= this.tiles[i][j].getXPixel()) && (x <= this.tiles[i][j].getXPixel() + App.CELLSIZE) 
                && (y >= this.tiles[i][j].getYPixel()) && (y <= this.tiles[i][j].getYPixel() + App.CELLSIZE)) {
                    return this.tiles[i][j];
                }
            }
    }
    return null;
}

    /**
     * Draws the game board to the screen. The wizard's house is drawn last.
     *
     * @param app The main application instance used for drawing.
     */
    public void drawBoard(App app) {
        // Iterate through all tiles except the wizard's house
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Tile current_tile = this.tiles[x][y];
                if (this.layout_Array[x][y] != 'W') {
                    current_tile.draw(app);
                }
            }
        }

        // Draw the wizard's house
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Tile current_tile = this.tiles[x][y];
                if (this.layout_Array[x][y] == 'W') {
                    current_tile.draw(app);
                }
            }
        }
    }
    /**
     * Gets the layout array belonging to the game board. 
     * @return  A 2D array of characters representing the game board's grid (x,y).
     */
    public char[][] getLayoutArray() {
        return this.layout_Array;
    }

    /**
     * Gets the tiles belonging to the game board. 
     * @return  A 2D array of tiles representing the game board's grid (x,y).
     */
    public Tile[][] getTiles() {
        return this.tiles;
    }

    /**
     * Draws the wizard's house on the game board.
     *
     * @param app The main application instance used for drawing.
     */
    public void drawWizard(App app) {
        // Draw the wizard's house
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Tile current_tile = this.tiles[x][y];
                if (this.layout_Array[x][y] == 'W') {
                    current_tile.draw(app);
                }
            }
        }
    }
}


