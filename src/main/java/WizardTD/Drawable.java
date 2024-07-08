package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * The Drawable class is an abstract class that serves as the base class for all objects
 * that can be drawn on the screen in the game. It provides basic graphical capabilities 
 * and properties for drawable objects.
 */
public abstract class Drawable {
    protected int x;
    protected int y;
    protected float xPixel;
    protected float yPixel;
    protected PImage sprite;

    /**
     * Basic constructor to create a drawable object with specified coordinates.
     * 
     * @param x The x coordinate of the object (Using the map grid, x,y = 0 at the top left corner).
     * @param y The y coordinate of the object.
     */
    public Drawable(int x, int y) {
        this.x = x;
        this.y = y;
        this.xPixel = (this.x * App.CELLSIZE);
        this.yPixel = (this.y * App.CELLSIZE + App.TOPBAR);
    }

    /**
     * Draws the drawable object on the screen if it has a sprite image.
     * Calculates the centre of the image based on the App's cellsize to draw.
     *
     * @param app The PApplet object for rendering.
     */
    public void draw(PApplet app) {
        if (this.sprite != null) {
            
            // Calculate the position to center the image within the tile
            float xPos = this.xPixel + (App.CELLSIZE - this.sprite.width) / 2;
            float yPos = this.yPixel + (App.CELLSIZE - this.sprite.height) / 2;
    
            app.image(this.sprite, xPos, yPos); 
             
        } 
    }

    public int getX() {
        return x;
    }

    public float getXPixel() {
        return xPixel;
    }

    public int getY() {
        return y;
    }

    public float getYPixel() {
        return yPixel;
    }
    /**
     * Gets the object's sprite.
     * 
     * @return The image sprite associated with the drawable object.
     */
    public PImage getSprite() {
        return sprite;
    }

    
}
