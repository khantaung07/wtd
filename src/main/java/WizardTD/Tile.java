package WizardTD;

import processing.core.PImage;

    /**
     * The Tile class is an individual tile on the game board in the game.
     * It extends the Drawable class and contains information about the tile's properties
     * including its type, sprite and whether it has a tower placed.
     */
    public class Tile extends Drawable {
        
        private boolean hasTower;
        private boolean edge; // Boolean attribute that represents whether the tile is on the edge of the board
        private String type;

        /**
         * Constructs a new tile at the specified coordinates on the game board.
         * Determines whether the tile is on the edge of the board or not.
         *
         * @param x The x-coordinate of the tile.
         * @param y The y-coordinate of the tile.
         */
        public Tile(int x, int y) {
            super(x, y); 
            this.hasTower = false;

            // Determine whether tile is on an edge
            if (this.x == 0 || this.x == (App.BOARD_WIDTH - 1) || this.y == 0 ||  this.y == (App.BOARD_WIDTH - 1)) {
                this.edge = true;
            }
            else {
                this.edge = false;
            }
        }

        /**
         * Sets the sprite and type for this tile.
         *
         * @param sprite The image representing the tile's appearance.
         * @param type   The type of the tile ("grass," "shrub," "wizard," or "path").
         */
        public void setSprite(PImage sprite, String type) {
            this.sprite = sprite;
            this.type = type;
        }

        /**
        * Returns whether the tile is on the edge of the game board.
        *
         * @return true if the tile is on the edge, false otherwise.
         */
        public boolean getEdge() {
            return edge;
        }

        public String getType() {
            return this.type;
        }

        /**
         * Places a tower on this tile.
         */
        public void placeTower() {
            this.hasTower = true;
        }

        /**
         * Returns whether a tower has been placed on this tile.
         *
         * @return true if a tower has been placed, false otherwise.
         */
        public boolean hasTower() {
            return this.hasTower;
        }

    }