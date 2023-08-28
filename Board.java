import java.util.*;

public class Board {

    // Member variables
    private final int boardHeight = 24;
    private final int boardWidth = 24;

    private final List<Coord> hhEntrances = List.of(new Coord(5, 6), new Coord(6, 3));
    private final List<Coord> mmEntrances = List.of(new Coord(17, 5), new Coord(20, 6));
    private final List<Coord> ccEntrances = List.of(new Coord(3, 17), new Coord(6, 18));
    private final List<Coord> ppEntrances = List.of(new Coord(18, 17), new Coord(17, 20));
    private final List<Coord> vvEntrances = List.of(new Coord(9, 12), new Coord(12, 10), new Coord(14, 11), new Coord(11, 13));

    private final Estate HH = new Estate(2, 2, 6, 6, "Haunted House", hhEntrances);
    private final Estate MM = new Estate(17, 2, 21, 6, "Manic Manor", mmEntrances);
    private final Estate CC = new Estate(2, 17, 6, 21, "Calamity Castle", ccEntrances);
    private final Estate PP = new Estate(17, 17, 21, 21, "Peril Palace", ppEntrances);
    private final Estate VV = new Estate(9, 10, 14, 13, "Visitation Villa", vvEntrances);

    //private final List<String> Weapons = List.of("Broom", "Scissors", "Knife", "Shovel", "iPad");
    private final ArrayList<Weapon> Weapons = new ArrayList<>(List.of(
            new Weapon(5,20,"Broom"),
            new Weapon(18,6,"Scissors"),
            new Weapon(3,3,"Knife"),
            new Weapon(10,10,"Shovel"),
            new Weapon(20,21,"Ipad")
    ));
    private List<Character> characters = new ArrayList<>(List.of(
        new Character(11, 1, "L", Character.Direction.LUCILLA),
        new Character(1, 9, "B", Character.Direction.BERT),
        new Character(9, 22, "M", Character.Direction.MALINA),
        new Character(22, 14, "P", Character.Direction.PERCY)
    ));

    private final ArrayList<Estate> estates = new ArrayList<>(List.of(HH, MM, CC, PP, VV,
        new GreyArea(5, 11, 6, 12, "GREY AREA"), 
        new GreyArea(17, 11, 18, 12, "GREY AREA"),
        new GreyArea(11, 5, 12, 6, "GREY AREA"),
        new GreyArea(11, 17, 12, 18, "GREY AREA")
    ));
    private final ArrayList<Card> deck = new ArrayList<>();
    private final Set<Card> murderCards = new HashSet<>();

    private List<Coord> validHighlightedCells;
    private List<Coord> invalidHighlightedCells;
    private ArrayList<Character> chars = new ArrayList<Character>();
    int playerCount;

    // Constructor
    public Board() {
        // Initialize highlightedCells
        validHighlightedCells = new ArrayList<>();
        invalidHighlightedCells = new ArrayList<>();

        populateDeck();
        setMurderCards();
        playerCount = Main.UI.getPlayerCount();

        // displayCards();
    }

    // Public methods
    public int getBoardHeight() {
        return boardHeight;
    }

    public int getBoardWidth() {
        return boardWidth;
    }

    public List<Estate> getEstates() {
        return Collections.unmodifiableList(estates);
    }

    public List<Character> getCharacters() {
        return characters;
    }
    
    public ArrayList<Weapon> getWeapons() {
        return Weapons;
    }

    public Set<Card> getMurderCards() {
        return murderCards;
    }

    /**
     * Retrieve the estate at the specified coordinates.
     */
    public Estate getEstateAt(int x, int y) {
        for (Estate estate : this.getEstates()) {
            if (x >= estate.getX() && x <= estate.getX2() && y >= estate.getY() && y <= estate.getY2()) {
                return estate;
            }
        }
        return null;
    }

    /**
     * Check if the provided coordinate is an estate.
     */
    public boolean isEstate(Coord coord) {
        Estate estate = getEstateAt(coord.getX(), coord.getY());
        return estate != null;
    }

    public void highlightCells(List<Coord> shortestPath, int remainingMoves) {
        validHighlightedCells.clear();
        invalidHighlightedCells.clear();

        for (int i = 1; i < shortestPath.size(); i++) {
            if (i <= remainingMoves) {
                validHighlightedCells.add(shortestPath.get(i));
            } else {
                invalidHighlightedCells.add(shortestPath.get(i));
            }
        }
    }

    public List<Coord> getValidHighlightedCells() {
        return validHighlightedCells;
    }

    public List<Coord> getInvalidHighlightedCells() {
        return invalidHighlightedCells;
    }
    
    public void setRemainingMoves(int remainingMoves) {}
    
    public void clearHighlightedCells() {
        validHighlightedCells.clear();
        invalidHighlightedCells.clear();    
    }


    public void takeList(ArrayList<String> names) {
        for(String name : names) {
          Character character = getCharacterByName(name);
          if(character != null) {
            chars.add(character);
          }
        }
        shuffleCharacters(chars);
        distributeCards();
      }

    // Private methods
    private void populateDeck() {
        for (Estate e : estates) {
            if (!(e instanceof GreyArea)) {
                deck.add(new Card(e.getName(), Card.CardType.ESTATE));
            }
        }
        for (Weapon w : Weapons) {
            deck.add(new Card(w.getName(), Card.CardType.WEAPON));
        }
        for (Character c : characters) {
            deck.add(new Card(c.getName().toString(), Card.CardType.CHARACTER));
        }
    }

    private Character getCharacterByName(String name) {
        return characters.stream()
                .filter(character -> character.getName().toString().equals(name))
                .findFirst()
                .orElse(null);
    }

    private void shuffleCharacters(List<Character> chars) {
        List<Character> mutableChars = new ArrayList<>(chars);
        Collections.shuffle(mutableChars);
        characters = mutableChars;
    }

    private void setMurderCards() {
        Collections.shuffle(deck);
        murderCards.add(pickCardOfType(Card.CardType.WEAPON));
        murderCards.add(pickCardOfType(Card.CardType.CHARACTER));
        murderCards.add(pickCardOfType(Card.CardType.ESTATE));

        deck.removeAll(murderCards);
    }

    private Card pickCardOfType(Card.CardType type) {
        for (Card c : deck) {
            if (c.getType() == type) {
                return c;
            }
        }
        return null;
    }

    private void distributeCards() {
        int playerCount = Main.UI.getPlayerCount();
        if (playerCount < 3 || playerCount > 4) {
            throw new IllegalArgumentException("The number of characters must be between 3 and 4.");
        }
        Collections.shuffle(deck);
        int numCardsPerPlayer = deck.size() / playerCount;
        int remainingCards = deck.size() % playerCount;

        distributeCardsToPlayers(numCardsPerPlayer, remainingCards);
    }

    private void distributeCardsToPlayers(int numCardsPerPlayer, int remainingCards) {
        int cardIndex = 0;
        while (cardIndex < deck.size()) {
            int numCardsToDeal = numCardsPerPlayer + (remainingCards > 0 ? 1 : 0);
            for (int i = 0; i < numCardsToDeal; i++) {
                characters.get(cardIndex % Main.UI.getPlayerCount()).addCards(deck.get(cardIndex));
                cardIndex++;
            }
            remainingCards--;
        }
    }

    // private void displayCards() {
    //     for (Character c : characters) {
    //         System.out.println(c.getName() + " cards:");
    //         System.out.println(c.getCards() + "\n");
    //     }
    //     System.out.println("Murder cards :");
    //     System.out.println(murderCards);
    // }

}