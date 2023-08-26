import java.util.*;

/* 
 * This class is used to find the shortest path between two coordinates on the board.
 * It uses Dijkstra's algorithm to calculate this.
 */
public class DijkstraShortestPath {

    // Public methods
    public static List<Coord> minimumDistance(Board board, Coord source, Coord target, Set<Coord> occupiedCells, Set<Coord> visitedCellsThisTurn) {
        Map<Coord, Integer> dist = new HashMap<>();
        Map<Coord, Coord> prev = new HashMap<>();
        List<Coord> unvisited = new ArrayList<>();

        for (int x = 0; x < board.getBoardWidth(); x++) {
            for (int y = 0; y < board.getBoardHeight(); y++) {
                Coord coord = new Coord(x, y);
                if (isValid(board, coord, occupiedCells, target, visitedCellsThisTurn)) {
                    if (coord.equals(source)) {
                        dist.put(coord, 0);
                    } else {
                        dist.put(coord, Integer.MAX_VALUE);
                    }
                    prev.put(coord, null);
                    unvisited.add(coord);
                }
            }
        }

        dist.put(source, 0);
        unvisited.add(source);
        dist.put(target, Integer.MAX_VALUE);
        unvisited.add(target);

        while (!unvisited.isEmpty()) {
            Coord current = getSmallestDist(unvisited, dist);
            unvisited.remove(current);

            for (Coord neighbor : getNeighbors(board, current, occupiedCells, target, visitedCellsThisTurn)) {
                Integer currDist = dist.get(current);
                if (currDist != null && currDist != Integer.MAX_VALUE) {
                    int alt = currDist + 1;
                    Integer neighborDist = dist.get(neighbor);
                    if (neighborDist == null || alt < neighborDist) {
                        dist.put(neighbor, alt);
                        prev.put(neighbor, current);
                    } else {
                    }
                }
            }
        }

        List<Coord> path = new ArrayList<>();
        Coord step = target;
        while (step != null) {
            path.add(step);
            step = prev.get(step);
        }
        Collections.reverse(path);

        return path;
    }

    // Private methods
    private static Coord getSmallestDist(List<Coord> coords, Map<Coord, Integer> dist) {
        Coord smallest = null;
        for (Coord coord : coords) {
            if (smallest == null) {
                smallest = coord;
            } else {
                Integer currentDist = dist.get(coord);
                Integer smallestDist = dist.get(smallest);
                if (currentDist != null && smallestDist != null && currentDist < smallestDist) {
                    smallest = coord;
                }
            }
        }
        return smallest;
    }

    /**
     * Returns the neighbors of the given coordinate
     */
    private static List<Coord> getNeighbors(Board board, Coord coord, Set<Coord> occupiedCells, Coord target, Set<Coord> visitedCellsThisTurn) {
        List<Coord> neighbors = new ArrayList<>();

        int[][] offsets = {
            {0, -1},
            {1, 0},
            {0, 1},
            {-1, 0}
        };

        for (int[] offset : offsets) {
            Coord neighbor = new Coord(coord.getX() + offset[0], coord.getY() + offset[1]);
            if (isValid(board, neighbor, occupiedCells, target, visitedCellsThisTurn)) {
                neighbors.add(neighbor);
            }
        }        
        return neighbors;
    }

    /**
     * Checks if a given coordinate is valid based on the board, occupied cells, and target
     */
    private static boolean isValid(Board board, Coord coord, Set<Coord> occupiedCells, Coord target, Set<Coord> visitedCellsThisTurn) {
        // Ensure the coordinate is not out of the board
        if (coord.getX() < 0 || coord.getX() >= board.getBoardWidth() || coord.getY() < 0 || coord.getY() >= board.getBoardHeight()) {
            return false;
        }
    
        // Check if the cell has been visited during this turn
        if (visitedCellsThisTurn.contains(coord) && !coord.equals(target)) {
            return false;
        }

        // Ensure the coordinate is not occupied by another player
        if (occupiedCells.contains(coord)) {
            return false;
        }
    
        // If the target is inside an estate, allow movement inside the estate
        Estate targetEstate = board.getEstateAt(target.getX(), target.getY());
        if (targetEstate != null && targetEstate.isInside(coord)) {
            return true;  // If the target is inside an estate, and the current coord is inside the same estate, it's valid.
        }
    
        // Otherwise, ensure the coordinate is not inside an estate that is not an entrance
        Estate estate = board.getEstateAt(coord.getX(), coord.getY());
        if (estate != null) {
            List<Coord> entrances = estate.getEntrances();
            if (entrances == null || !entrances.contains(coord)) {
                return false;
            }
        }
        return true;
    }
}