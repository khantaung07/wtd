package WizardTD;

/**
 * The Fireball is a projectile that is fired by towers to track and
 * damage monsters in the game.
 */
public class Fireball extends Drawable {
    /**
    * The target monster that the fireball is tracking and will damage upon collision.
    */
    private Monster target;
    private float damage;
    private float defaultSpeed;
    private float speed;
    /**
    * Double the speed of the fireball when fast-forward mode is active.
    */
    private float TwoXSpeed;
    /**
    * A boolean indicating whether the fireball has collided with its target.
    */
    private boolean collided;
    /**
     * A boolean indicating whether the fireball is currently active and tracking a valid target.
     */
    private boolean active;
    private App app;

    /**
     * Constructs a Fireball object with the specified attributes.
     * The target monster is calculated in the tower class.
     * @see WizardTD.Tower#findMonster() findMonster
     *
     * @param app     The main application.
     * @param x       The initial x-coordinate of the fireball.
     * @param y       The initial y-coordinate of the fireball.
     * @param damage  The damage inflicted by the fireball.
     * @param target  The target monster to track and damage.
     */
    public Fireball(App app, int x, int y, float damage, Monster target) {
        super(x, y);
        this.damage = damage;
        this.target = target;
        this.app = app;
        this.speed = (float)Math.max(5, this.target.getSpeed() + 1);
        this.defaultSpeed = speed;
        
        this.TwoXSpeed = 2*speed;
        this.sprite = app.images.get("fireball");
        this.collided = false;
        this.active = true;

    }

    /**
     * Updates the state of the fireball every frame. This includes
     * updating its speed, location, active state and collision state.
     */
    public void tick() {

        if (this.app.fastforward) {
            this.speed = this.TwoXSpeed;
        }
        else {
            this.speed = this.defaultSpeed;
        }

        if (!this.collided && this.active) {
            // Move towards the monster
            
            // Calculate the distance between the current position and the destination tile
            double deltaX = this.target.getXPixel() - this.xPixel;
            double deltaY = this.target.getYPixel() - this.yPixel;
            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

            // If the monster's coordinates are ever outside the map (monster was banished),
            // fireball should despawn
            if (this.target.getXPixel() <= 0 || this.target.getXPixel() >= App.WIDTH - App.SIDEBAR 
            || this.target.getYPixel() <= App.TOPBAR || this.target.getYPixel () >= App.HEIGHT) {
                this.active = false;
            }
            
            // If the monster dies while being tracked, fireball should also despawn
            if (this.target.getHp() <= 0) {
                this.active = false;
            }
                
                
            // If the distance is greater than the speed, move towards the destination
            if (distance > this.speed) {
                double ratio = this.speed / distance;
                this.xPixel += ratio * deltaX;
                this.yPixel += ratio * deltaY;
            } 
            else {
                // If the distance is smaller than the speed, directly reach the destination
                this.xPixel = this.target.getXPixel();
                this.yPixel = this.target.getYPixel();
            }

        }

        else if (this.collided && this.active) {
            this.target.updateHealth(this.damage);
            this.active = false;
        }

        // Check if the fireball has hit the target
        checkCollision();

    }

     /**
     * Checks for collision between the fireball and its target.
     */
    public void checkCollision() {
        if (this.xPixel == this.target.getXPixel() && this.yPixel == this.target.getYPixel()) {
            this.collided = true;
        }
    }
    /**
     * Returns whether or not the fireball is currently active and tracking a valid target.
     * @return A boolean indicating whether the fireball is active.
     */
    public boolean isActive() {
        return this.active;
    }
    
}
