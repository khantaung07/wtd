package WizardTD;

import java.util.*;

/**
 * The Path class is responsible for the pathfinding of monsters to the wizard's house.
 * It finds the shortest path from a given starting point to the wizard's house (W) on a map.
 */
public class Path {

    private int x; // Current x coordinate for pathfinding
    private int y; // Current y coordinate for pathfinding
    private int x_dest;
    private int y_dest;
    private GameBoard gameboard;
    private char[][] map;
    private HashMap<Tile, ArrayList<Tile>> map_graph;
    private ArrayList<Tile> steps; // ArrayList to store tile steps of path

    /**
     * Initializes a Path object with starting coordinates, a map layout, and the game board.
     * Builds an adjacency matrix for the path tiles on the map.
     *
     * @param x            The starting x coordinate.
     * @param y            The starting y coordinate.
     * @param layout_Array The map layout represented as a 2D char array.
     * @param gameboard    The GameBoard object representing the game environment.
     */
    public Path(int x, int y, char[][] layout_Array, GameBoard gameboard) { 
        this.x = x;
        this.y = y;
        this.map = layout_Array;
        this.gameboard = gameboard;
        this.map_graph = new HashMap<>();

        // Get coordinates of wizard's house using given layout_Array
        for (int k = 0; k < map.length; k++) {
            for (int l = 0; l < map[0].length; l++) {
                if (this.map[k][l] == 'W') {
                    this.x_dest = k;
                    this.y_dest = l;
                }
            }
        }

        // Build an adjacency graph using tiles with 'X' or 'W'
        buildMapGraph();


        // Using the graph built, find shortest path

        // Generate path using starting x, y and map and destination x, y
        this.steps = findShortestPath();
       
    }


    /**
     * Builds an adjacency matrix of path tiles and their neighbour tiles in the given map layout
     * and stores them to a Hashmap.
     */
    public void buildMapGraph() {
        int rows = this.map.length;
        int cols = this.map[0].length;


        // Iterate through the 'X' and 'W' characters and build the graph
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                
                if (map[i][j] == 'X' || map[i][j] == 'W') {
                    Tile current_tile = this.gameboard.getTile(i, j);
                    

                    ArrayList<Tile> neighbors = new ArrayList<>();

                    // Check adjacent cells for 'X' and add them as neighbors
                    int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

                    // Traverse randomly to create different paths
                    List<int[]> shuffledDirections = new ArrayList<>(Arrays.asList(directions));
                    Collections.shuffle(shuffledDirections);

                    for (int[] dir : shuffledDirections) {
                        int ni = i + dir[0];
                        int nj = j + dir[1];
                         // Avoid index errors - trying to get neighbours outside the map
                        if (ni >= 0 && ni < rows && nj >= 0 && nj < cols && (map[ni][nj] == 'X' || map[ni][nj] == 'W')) {
                            Tile neighborTile = this.gameboard.getTile(ni, nj);
                            neighbors.add(neighborTile);
                        }
                    }

                    
                    this.map_graph.put(current_tile, neighbors);
                
                }
            }
        }

    }

     
    
    /**
     * Finds the shortest path from the starting x,y to the wizard's housing 
     * using a breadth-first search algorithm. Specifically, this method builds the parent map.
     * 
     * @return An array list of tiles of the shortest path from the starting x,y to the wizard's house.
     */
    private ArrayList<Tile> findShortestPath() {
        Tile startTile = gameboard.getTile(x, y);
        Tile destinationTile = gameboard.getTile(x_dest, y_dest);

        Queue<Tile> queue = new LinkedList<Tile>(); // Queue for BFS algorithm
        Set<Tile> visited = new HashSet<Tile>(); // Set to store visited tiles
        HashMap<Tile, Tile> parentMap = new HashMap<>();

        // Add starting tile to queue and visited tiles
        queue.add(startTile);
        visited.add(startTile);

        while (!queue.isEmpty()) {
            Tile currentTile = queue.poll();

            if (currentTile.equals(destinationTile)) {
                
                // If we are at the destination tile, reconstruct the path using the map created
                return reconstructPath(startTile, destinationTile, parentMap);
            }

            for (Tile neighbor : map_graph.get(currentTile)) {
                if (!visited.contains(neighbor)) {
                    queue.add(neighbor);
                    visited.add(neighbor);
                    parentMap.put(neighbor, currentTile);
                }
            }
        }

        return null; // No path found
    }

    /**
     * Reconstructs the shortest path from the starting tile to the destination tile
     * using the parent map.
     * 
     * @param source The starting tile
     * @param destination The tile of the wizard's house
     * @param parent The parent map used for path reconstruction
     * @return An array list of tiles of the shortest path from the starting x,y to the wizard's house.
     */
    private ArrayList<Tile> reconstructPath(Tile source, Tile destination, HashMap<Tile,Tile> parent) {
        ArrayList<Tile> path = new ArrayList<>();
        Tile current = destination;

        while (current != null && !current.equals(source)) {
            path.add(current);
            current = parent.get(current);
        }
        
        // Add the source tile to the path
        if (current.equals(source)) {
            path.add(source);
            Collections.reverse(path); // Reverse the path to get it from source to destination
        }

        return path;

    }
    /**
     * Gets the steps of the path generated.
     * 
     * @return An array list of tiles of the shortest path from the starting x,y to the wizard's house.
     */
    public ArrayList<Tile> getSteps() {
        return this.steps;
    }


     
    
}
