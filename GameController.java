import java.awt.Dimension;
import java.awt.event.*;
import java.util.*;
import javax.swing.Timer;

public class GameController implements MouseListener, MouseMotionListener {
    
    // Member variables
    private Board board;
    private GameView view;
    private Character currentPlayer;
    private int currentRoll;
    private boolean rollCompleted;
    private int currentTurn = 0;
    private int remainingMoves;
    private Timer diceRollTimer;
    private int diceRollCounter = 0;
    private boolean isAnimating = false;
    private Estate hoverableEstate = null;
    private List<Estate> unreachableEstates = new ArrayList<>();
    private List<Coord> hoverableEntrances = new ArrayList<>();
    private List<Coord> unreachableEntrances = new ArrayList<>();

    // Constructor
    public GameController(Board board){
        this.board = board;
        // Update animated positions for all characters
        for (Character character : board.getCharacters()) {
            character.setAnimatedX(character.getX());
            character.setAnimatedY(character.getY());
        }
        currentPlayer = board.getCharacters().get(0);  // Set currentPlayer to the first character at the start
        currentPlayer.setAnimatedX(currentPlayer.getX());
        currentPlayer.setAnimatedY(currentPlayer.getY());
    }
    
    // Public methods
    public List<Estate> getUnreachableEstates() {
        return new ArrayList<>(unreachableEstates);
    }
    
    public Estate getHoverableEstate() {
        return hoverableEstate;
    }

    public List<Coord> getUnreachableEntrances() {
        return new ArrayList<>(unreachableEntrances);
    }

    public List<Coord> getHoverableEntrances() {
        return new ArrayList<>(hoverableEntrances);
    }

    public void setView(GameView view) {
        this.view = view;
        this.view.addBoardMouseListener(this);
        this.view.addBoardMouseMotionListener(this);
        this.view.addRollDiceButtonListener(this::handleRollDiceButton);

        currentRoll = 0;  // Set roll to zero at start
        rollCompleted = false;
        view.updatePlayerTurnLabel(currentPlayer.getName().name()); // Set initial current player turn label
        remainingMoves = currentRoll; // initialize remainingMoves with currentRoll
        view.updateMovesRemainingLabel(remainingMoves); // display remainingMoves  
    }

    @Override
    public void mousePressed(MouseEvent e){
       handleMouseAction(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        hoverableEntrances.clear();
        unreachableEntrances.clear();
        hoverableEstate = null;
        unreachableEstates.clear();

        if (rollCompleted && !isAnimating) {
            Dimension boardPanelSize = view.getBoardPanelSize();
            int cellSize = Math.min(boardPanelSize.width / board.getBoardWidth(), boardPanelSize.height / board.getBoardHeight());

            int x = e.getX() / cellSize;
            int y = e.getY() / cellSize;

            Coord currentPlayerPosition = new Coord(currentPlayer.getX(), currentPlayer.getY());
            // Coord hoverPosition = new Coord(x, y);
            Coord targetPosition = new Coord(x, y);

            Estate currentEstate = board.getEstateAt(currentPlayerPosition.getX(), currentPlayerPosition.getY());
            Estate hoveredEstate = board.getEstateAt(x, y);

            if (currentEstate != null) {
                Coord closestEntrance = findClosestEntrance(targetPosition, currentEstate.getEntrances());

                currentPlayer.setX(closestEntrance.getX());
                currentPlayer.setY(closestEntrance.getY());

                currentPlayerPosition = closestEntrance; 
            }

            if (hoveredEstate != null && !(hoveredEstate instanceof GreyArea)) {
                targetPosition = findClosestEntrance(currentPlayerPosition, hoveredEstate.getEntrances());
            }

            Set<Coord> occupiedCells = getOccupiedCells();
            List<Coord> shortestPath = DijkstraShortestPath.minimumDistance(board, currentPlayerPosition, targetPosition, occupiedCells);

            if (hoveredEstate != null && !(hoveredEstate instanceof GreyArea)) {
                Coord closestEntrance = findClosestEntrance(currentPlayerPosition, hoveredEstate.getEntrances());
                List<Coord> pathToClosestEntrance = DijkstraShortestPath.minimumDistance(board, currentPlayerPosition, closestEntrance, getOccupiedCells());
        
                if (pathToClosestEntrance.size() - 1 <= remainingMoves) {
                    hoverableEntrances.add(closestEntrance);
                    hoverableEstate = hoveredEstate;  // Set the hoverable estate
                } else {
                    unreachableEntrances.add(closestEntrance);
                    unreachableEstates.add(hoveredEstate); // Add the hovered estate to unreachable since it's not reachable
                }
            }
        
            if (shortestPath.size() <= remainingMoves) {
                board.highlightCells(shortestPath, remainingMoves);
            } else {
                board.highlightCells(shortestPath, remainingMoves);
            }
            view.repaint();
        }
    }

    // Empty overridden methods
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseDragged(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    // Private methods
    private void handleMouseAction(MouseEvent e) {
        if (rollCompleted && !isAnimating) {
            Dimension boardPanelSize = view.getBoardPanelSize();
            int cellSize = Math.min(boardPanelSize.width / board.getBoardWidth(), boardPanelSize.height / board.getBoardHeight());
    
            int x = e.getX() / cellSize;
            int y = e.getY() / cellSize;
    
            Coord currentPlayerPosition = new Coord(currentPlayer.getX(), currentPlayer.getY());
            Coord targetPosition = new Coord(x, y);

            // Check if the clicked position is the current players position
            if (currentPlayerPosition.equals(targetPosition)) {
                view.logMessage("You are already on this square!");
                return;
            }
    
            // Check if the clicked position is an estate
            if (board.isEstate(targetPosition)) {
                moveToEstate(targetPosition);
            } else {
                // Proceed with the usual movement
                Set<Coord> occupiedCells = getOccupiedCells();
                List<Coord> shortestPath = DijkstraShortestPath.minimumDistance(board, currentPlayerPosition, targetPosition, occupiedCells);
    
                if (shortestPath.size() - 1 <= remainingMoves && shortestPath.size() > 1) {
                    beginMoveAnimation(shortestPath, 100, () -> {
                        currentPlayer.setX(x);
                        currentPlayer.setY(y);
                        view.repaint();
    
                        remainingMoves -= (shortestPath.size() - 1);
                        board.setRemainingMoves(remainingMoves);
                        view.updateMovesRemainingLabel(remainingMoves);
                        if (remainingMoves == 0) {
                            handleEndTurnButton();
                        }
                    });
                } else {
                    view.logMessage("You can't move that far!");
                }
            }
            board.clearHighlightedCells();
        } else {
            view.logMessage("You need to roll first!");
        }
    }
    
    private void handleRollDiceButton(ActionEvent e) {
        if (!rollCompleted) {
            diceRollCounter = 0;

            // Disable rollDiceButton
            view.setRollDiceButtonEnabled(false);
    
            // Create a timer that updates the diceRollLabel rapidly
            diceRollTimer = new Timer(35, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    int tempRoll = new Random().nextInt(6) + 1;
                    view.updateDiceRollLabel(tempRoll);
                    diceRollCounter++;
    
                    // Stop the timer after 10 iterations and show the final dice roll
                    if (diceRollCounter > 10) {
                        diceRollTimer.stop();
                        currentRoll = GameController.Dice.roll();
                        view.updateDiceRollLabel(currentRoll);
                        remainingMoves = currentRoll;
                        board.setRemainingMoves(remainingMoves); // update remainingMoves on the board
                        view.updateMovesRemainingLabel(remainingMoves);
                        view.logMessage("You rolled a " + currentRoll + "!");
                        rollCompleted = true;
                        // Re-enable rollDiceButton
                        view.setRollDiceButtonEnabled(true);
                    }
                }
            });
    
            diceRollTimer.start();
        } else {
            view.logMessage("You already rolled a " + currentRoll + "! Move or pass the turn to the next player.");
        }
    }

    private void showCurrentPlayerCards() {
        Set<Card> cards = currentPlayer.getCards();
        view.updatePlayerCards(cards);
    }

    private void handleEndTurnButton() {
        view.updatePlayerCards(new HashSet<>()); // Clear the card list as soon as a player's turn ends.
        currentTurn = (currentTurn + 1) % board.getCharacters().size();
        currentPlayer = board.getCharacters().get(currentTurn);
        currentPlayer.setAnimatedX(currentPlayer.getX());
        currentPlayer.setAnimatedY(currentPlayer.getY());
        view.updatePlayerTurnLabel(currentPlayer.getName().toString());
        view.logMessage("It's now " + currentPlayer.getName() + "'s turn!");
        rollCompleted = false; // Reset roll to false so the next player must roll the dice at the start of their turn
        currentRoll = 0; // Reset the current roll count for the new turn
        showCurrentPlayerCards(); // Update the card list when it's a new player's turn
    }

    private Coord findClosestEntrance(Coord destination, List<Coord> entrances) {
        if (entrances == null || entrances.isEmpty()) {
            return null;
        }
    
        Coord closest = null;
        double minDistance = Double.MAX_VALUE;
        for (Coord entrance : entrances) {
            double distance = Math.sqrt(Math.pow(entrance.getX() - destination.getX(), 2) + Math.pow(entrance.getY() - destination.getY(), 2));
            if (distance < minDistance) {
                minDistance = distance;
                closest = entrance;
            }
        }
        return closest;
    }
    
    private void moveToRandomPositionInsideEstate(Estate estate) {
        List<Coord> unoccupiedSquares = new ArrayList<>(estate.getUnoccupiedSquares());
        
        // Remove entrances from the unoccupiedSquares list
        unoccupiedSquares.removeAll(estate.getEntrances());
    
        // Remove squares that are occupied by other players
        for (Character character : board.getCharacters()) {
            if (!character.equals(currentPlayer)) {
                unoccupiedSquares.remove(new Coord(character.getX(), character.getY()));
            }
        }
    
        if (!unoccupiedSquares.isEmpty()) {
            int randomIndex = new Random().nextInt(unoccupiedSquares.size());
            Coord randomSquare = unoccupiedSquares.get(randomIndex);
            currentPlayer.setX(randomSquare.getX());
            currentPlayer.setY(randomSquare.getY());
            view.repaint();
        } else {
            view.logMessage("No available space inside the estate!");
        }
    }
    
    private void moveToEstate(Coord targetPosition) {
        Coord currentPlayerCoord = new Coord(currentPlayer.getX(), currentPlayer.getY());
        Estate estate = board.getEstateAt(targetPosition.getX(), targetPosition.getY());
        List<Coord> entrances = estate.getEntrances();

        if (entrances == null || entrances.isEmpty()) {
            view.logMessage("That area is inaccessible!");
            return;
        }

        // Choose the closest entrance to the player's current position
        Coord entrance = findClosestEntrance(currentPlayerCoord, entrances);
        List<Coord> pathToEntrance = DijkstraShortestPath.minimumDistance(board, currentPlayerCoord, entrance, getOccupiedCells());
    
        if (pathToEntrance.size() - 1 <= remainingMoves) {
            beginMoveAnimation(pathToEntrance, 100, () -> {
                // Once the player reaches the estate entrance, move them to a random unoccupied square inside the estate
                moveToRandomPositionInsideEstate(estate);

                handleEndTurnButton();  // End the player's turn after entering the estate
            });
        } else {
            view.logMessage("Not enough moves to enter estate!");
        }
    }

    private Set<Coord> getOccupiedCells() {
        Set<Coord> occupiedCells = new HashSet<>();
        for (Character character : board.getCharacters()) {
            if (!character.equals(currentPlayer)) { 
                occupiedCells.add(new Coord(character.getX(), character.getY()));
            }
        }
        return occupiedCells;
    }

    private void beginMoveAnimation(List<Coord> path, int durationMs, Runnable onFinished) {
        // Ensure there's a valid path
        if (path.isEmpty() || path.size() < 2) {  
            return;
        }
        isAnimating = true;
        board.highlightCells(path, remainingMoves);

        int totalTime = durationMs * path.size();
        
        Timer timer = new Timer(15, null); 
        long animationStart = System.currentTimeMillis(); 
    
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long elapsed = System.currentTimeMillis() - animationStart;
    
                if (elapsed >= totalTime) {
                    timer.stop();
                    isAnimating = false;
                    onFinished.run();
                } else {
                    double progress = elapsed / (double)totalTime; 
                    int pathIndex = (int)(progress * (path.size() - 1));
    
                    Coord start = path.get(pathIndex);
                    Coord end = path.get(pathIndex + 1);
    
                    double cellProgress = progress * (path.size() - 1) - pathIndex;
                    double newX = start.getX() + (end.getX() - start.getX()) * cellProgress; 
                    double newY = start.getY() + (end.getY() - start.getY()) * cellProgress; 
    
                    currentPlayer.setAnimatedX(newX);
                    currentPlayer.setAnimatedY(newY);
                    view.repaint();
                }
            }
        });

        timer.start(); 
    }

    // Nested
    class Dice {
        private static final Random rand = new Random();
    
        public static int roll() {
            return rand.nextInt(6) + 1 + rand.nextInt(6) + 1; // sum of two six-sided dice
        }        
    }
}
