package WizardTD;

import java.util.*;

/**
 * A wave of monsters in the game that are spawned periodically.
 * @see WizardTD.Monster Monster
 */
public class Wave {

    private double duration;
    private double pre_wave_pause;
     // HashMap to store monster and quantity of each monster type
    private HashMap<Monster, Integer>  monsters_quantity;
    // List to store monsters
    private ArrayList<Monster> monsters; 
    // List to store monsters currently spawned
    private ArrayList<Monster> monsters_spawned; 
    private boolean complete;
    private boolean started;
    private int total_monsters;

    /**
     * Constructs a new wave of monsters.
     *
     * @param duration         The duration of the wave.
     * @param pre_wave_pause   The pause time before the wave starts.
     * @param monsters_quantity A map containing monster types and their quantities for this wave.
     */
    public Wave(double duration, double pre_wave_pause, HashMap<Monster, Integer>  monsters_quantity) {
        this.duration = duration;
        this.pre_wave_pause = pre_wave_pause;
        this.monsters_quantity = monsters_quantity;
        this.monsters = new ArrayList<>();
        this.monsters_spawned = new ArrayList<>();
        this.complete = false;
        this.started = false;

        // Create a list of monsters based on passed in monsters_quantity

        for (Map.Entry<Monster, Integer> entry : this.monsters_quantity.entrySet()) {

            Monster monsterType = entry.getKey();
            int quantityToAdd = entry.getValue();
            this.total_monsters += quantityToAdd;

             for (int j = 0; j < quantityToAdd; j++) {

                Monster newMonster = new Monster(monsterType.getApp(),monsterType.getType(), 
                monsterType.getX(), monsterType.getY(), monsterType.getHp(), monsterType.getSpeed(), 
                monsterType.getArmour(), monsterType.getManaGainedOnKill());
                 
                this.monsters.add(newMonster);
            }
        }


    }

    /**
     * Spawns a monster in the wave.
     *
     * @param app The main application instance.
     */
    public void spawn_monster(App app) {

        this.started = true;

        if (!this.monsters.isEmpty()) {
            // Pick a random monster
            Random random = new Random();
            int min = 0;
            int max = this.monsters.size();

            int random_num = random.nextInt((max - min));
             // Remove the monster from the list after spawning and add it to a monsters_spawned list
            Monster random_monster = this.monsters.get(random_num);
            
            // Pick a random spawn
            random_monster.respawn();

            this.monsters_spawned.add(random_monster);
            this.monsters.remove(random_monster);

        }
        // If the list is empty, toggle wave complete
        else {
            this.complete = true;
        }
    }

    /**
     * Updates all monsters within the wave and checks if the wave is complete.
     * Monsters are removed from the wave when they die.
     *
     * @param app The main application instance.
     */
    public void wave_action(App app) {

        // For each monster (that has been spawned), call its tick method once
        // this will make the monster move

        // For each monster, call its draw method once
        // this will update the position of the sprite
        for (Monster cur_monster : this.monsters_spawned) {
            if (cur_monster != null) {
                if (!app.paused && !app.dead) {
                    cur_monster.tick();
                }
            cur_monster.draw(app);
            // Draw health bar if has hp
            if (cur_monster.getHp() > 0) {
            cur_monster.drawHealth();
            }
            }
        }

        Iterator<Monster> itr = this.monsters_spawned.iterator();
        while (itr.hasNext()) {
            Monster cur_monster = itr.next();
            if (!cur_monster.isAlive()) {
                itr.remove();
            }
        }


        // Check if wave is complete
        if (this.monsters.isEmpty()) {
            this.complete = true;
        }

    }

    /**
     * Gets a list of monsters that are yet to be spawned in the wave.
     *
     * @return A list of monsters to be spawned.
     */
    public ArrayList<Monster> getMonstersToSpawn() {
        return this.monsters;
    }

    /**
     * Gets a list of monsters that have been spawned in the wave and are
     * still alive.
     *
     * @return A list of spawned monsters.
     */
    public ArrayList<Monster> getSpawnedMonsters() {
        return this.monsters_spawned;
    }

    public double getDuration() {
        return this.duration;
    }

    public double getPreWavePause() {
        return this.pre_wave_pause;
    }

    public boolean isComplete() {
        return this.complete;
    }

    public boolean hasStarted() {
        return this.started;
    }

    /**
     * Gets the total number of monsters in the wave.
     *
     * @return The total number of monsters in the wave.
     */
    public int getTotalMonsters() {
        return this.total_monsters;
    }
    
}
