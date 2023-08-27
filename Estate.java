import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Estate {
    
    // Member variables
    private int x;
    private int y;
    private int x2;
    private int y2;
    private String name;
    private List<Coord> entrances;
    private List<Character> chars = new ArrayList<>();
    private List<Character> weapons = new ArrayList<>();

    // Constructor
    public Estate(int x, int y, int x2, int y2, String name, List<Coord> entrances) {
        this.x = x;
        this.y = y;
        this.x2 = x2;
        this.y2 = y2;
        this.name = name;
        this.entrances = entrances;
    }

    // Public methods
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getX2() {
        return x2;
    }

    public int getY2() {
        return y2;
    }

    public String getName() {
        return name;
    }

    public List<Coord> getEntrances() {
        return entrances;
    }

    public String toString() {
        return this.name;
    }

//     public List<Character> getChars() {
//         return chars;
//     }

    // public void addChars(Character character) {
    //     chars.add(character);
    // }

    // public void removeChars(Character character) {
    //     chars.remove(character);
    // }

    public List<Coord> getAllSquaresInsideEstate() {
        List<Coord> squares = new ArrayList<>();
        for (int i = x; i <= x2; i++) {
            for (int j = y; j <= y2; j++) {
                squares.add(new Coord(i, j));
            }
        }
        return squares;
    }

    public Set<Coord> getOccupiedCells() {
        Set<Coord> occupied = new HashSet<>();
        for (Character character : chars) {
            occupied.add(new Coord(character.getX(), character.getY()));
        }
        return occupied;
    }

    public List<Coord> getUnoccupiedSquares() {
        List<Coord> squares = getAllSquaresInsideEstate();
        squares.removeAll(getOccupiedCells());
        return squares;
    }

    public boolean isInside(Coord coord) {
        return false;
    }
}