import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JPanel;

public class BoardRenderer {

    // Member variables
    private Board board;
    private GameController gameController;
    private ImageLoader imageLoader = new ImageLoader();
    private Map<String, BufferedImage> weaponImages = imageLoader.getImages();
    private Estate hoverableEstate;
    private List<Estate> unreachableEstates = new ArrayList<>();
    private Font glutenFont;

    // Constructor
    public BoardRenderer(Board board, GameController gameController) {
        this.board = board;
        this.gameController = gameController;
        this.weaponImages = imageLoader.getImages();

        // Load custom font
        try {
            glutenFont = FontLoader.loadFont("/fonts/Gluten-Light.ttf");
        } catch (IOException | FontFormatException e) {
            glutenFont = new Font("Arial", Font.PLAIN, 12); // fallback
        }
        if (glutenFont == null) {
            glutenFont = new Font("Arial", Font.PLAIN, 12); // additional safety fallback
        }
    }

    // Public methods
    public void drawBoard(Graphics2D g, JPanel boardPanel) {
        // Enable Anti-aliasing for shapes, text and images
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // Calculate the size of each cell dynamically
        int cellSize = Math.min(boardPanel.getWidth() / board.getBoardWidth(), boardPanel.getHeight() / board.getBoardHeight());

        // Draw the board grid
        g.setColor(Color.LIGHT_GRAY);
        for (int j = 0; j <= board.getBoardWidth(); j++) {
            g.drawLine(j * cellSize, 0, j * cellSize, board.getBoardHeight() * cellSize); // Vertical grid lines
        }
        for (int i = 0; i <= board.getBoardHeight(); i++) {
            g.drawLine(0, i * cellSize, board.getBoardWidth() * cellSize, i * cellSize); // Horizontal grid lines
        }

        // Draw highlighted cells
        g.setColor(new Color(0, 255, 0, 150)); // Green
        for (Coord cell : board.getValidHighlightedCells()) {
            g.fillRect(cell.getX() * cellSize, cell.getY() * cellSize, cellSize, cellSize);
        }
        g.setColor(new Color(255, 0, 0, 150)); // Red
        for (Coord cell : board.getInvalidHighlightedCells()) {
            g.fillRect(cell.getX() * cellSize, cell.getY() * cellSize, cellSize, cellSize);
        }

        // Draw visited cells in lighter gray
        g.setColor(new Color(220, 220, 220, 100)); // Lighter gray
        Set<Coord> visitedCellsThisTurn = gameController.getVisitedCellsThisTurn();
        for (Coord cell : visitedCellsThisTurn) {
            g.fillRect(cell.getX() * cellSize, cell.getY() * cellSize, cellSize, cellSize);
        }

        // Draw estates and grey areas
        for (Estate estate : board.getEstates()) {
            int thickness = 3;
            g.setStroke(new BasicStroke(thickness));

            if (!(estate instanceof GreyArea)) {
                g.setColor(Color.WHITE);
                g.fillRect(estate.getX() * cellSize, estate.getY() * cellSize, (estate.getX2() - estate.getX() + 1) * cellSize, (estate.getY2() - estate.getY() + 1) * cellSize);
                g.setColor(Color.BLACK);
                g.drawRect(estate.getX() * cellSize, estate.getY() * cellSize, (estate.getX2() - estate.getX() + 1) * cellSize, (estate.getY2() - estate.getY() + 1) * cellSize);
            } else {
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(estate.getX() * cellSize, estate.getY() * cellSize, (estate.getX2() - estate.getX() + 1) * cellSize, (estate.getY2() - estate.getY() + 1) * cellSize);
            }
        }

        // Draw estate entrances
        int thickness = 3;
        g.setStroke(new BasicStroke(thickness));

        List<Coord> redEntrances = gameController.getUnreachableEntrances();
        List<Coord> greenEntrances = gameController.getHoverableEntrances();
        hoverableEstate = gameController.getHoverableEstate();
        unreachableEstates = gameController.getUnreachableEstates();

        for (Estate estate : board.getEstates()) {
            if (!(estate instanceof GreyArea) && estate.getEntrances() != null) {
                for (Coord entrance : estate.getEntrances()) {
                    int rectX = entrance.getX() * cellSize;
                    int rectY = entrance.getY() * cellSize;

                    // Check if the entrance is the closest one
                    if (redEntrances.contains(entrance) || greenEntrances.contains(entrance)) {
                        if (redEntrances.contains(entrance)) {
                            g.setColor(new Color(255, 105, 105)); // Red
                        } else if (greenEntrances.contains(entrance)) {
                            g.setColor(new Color(105, 255, 105)); // Green
                        }
                    } else {
                        g.setColor(Color.ORANGE);
                    }

                    // Determine which side the entrance is on
                    if (entrance.getX() == estate.getX()) { // Left side
                        g.drawLine(rectX, rectY, rectX, rectY + cellSize);
                    } else if (entrance.getX() == estate.getX2()) { // Right side
                        g.drawLine(rectX + cellSize, rectY, rectX + cellSize, rectY + cellSize);
                    } else if (entrance.getY() == estate.getY()) { // Top side
                        g.drawLine(rectX, rectY, rectX + cellSize, rectY);
                    } else if (entrance.getY() == estate.getY2()) { // Bottom side
                        g.drawLine(rectX, rectY + cellSize, rectX + cellSize, rectY + cellSize);
                    }
                }
            }
        }

        // Draw estates with hover effect
        if (hoverableEstate != null) {
            g.setColor(new Color(0, 255, 0, 150)); // Green
            g.fillRect(hoverableEstate.getX() * cellSize + 2, hoverableEstate.getY() * cellSize + 2, (hoverableEstate.getX2() - hoverableEstate.getX() + 1) * cellSize - 3, (hoverableEstate.getY2() - hoverableEstate.getY() + 1) * cellSize - 3);
        }

        for (Estate estate : unreachableEstates) {
            g.setColor(new Color(255, 0, 0, 150)); // Red            
            g.fillRect(estate.getX() * cellSize + 2, estate.getY() * cellSize + 2, (estate.getX2() - estate.getX() + 1) * cellSize - 3, (estate.getY2() - estate.getY() + 1) * cellSize - 3);
        }

        // Reset the color and stroke to default for any further drawing
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1));

        // Print names on each estate and dynamically adjust font size based on cell size
        Font boardFont = glutenFont.deriveFont(Font.BOLD, cellSize / 2f);

        g.setFont(boardFont);
        g.setColor(Color.BLACK);

        for (Estate estate : board.getEstates()) {
            if (!(estate instanceof GreyArea)) {
                // Convert estate name to uppercase and split by spaces
                String[] nameParts = estate.getName().toUpperCase().split(" ");

                // Calculate total height of all lines combined
                int totalHeight = nameParts.length * cellSize / 2;

                // Calculate the starting y coordinate
                int offsetY = cellSize / 2;
                int nameY = estate.getY() * cellSize + (estate.getY2() - estate.getY() + 1) * cellSize / 2 - totalHeight / 2 + offsetY;

                for (String part : nameParts) {
                    // Calculate width of the text
                    int textWidth = g.getFontMetrics().stringWidth(part);

                    // Calculate the x coordinate to center the text
                    int nameX = estate.getX() * cellSize + (estate.getX2() - estate.getX() + 1) * cellSize / 2 - textWidth / 2;

                    g.drawString(part, nameX, nameY);
                    nameY += cellSize / 2; // Move to the next line
                }
            }
        }

        // Draw weapons at specific coordinates
        updateWeapons(g, boardPanel);

        // Draw characters
        FontMetrics fm = g.getFontMetrics();
        for (Character character : board.getCharacters()) {
            switch (character.getInitial()) {
                case "L":
                    g.setColor(Color.GREEN);
                    break;
                case "B":
                    g.setColor(Color.YELLOW);
                    break;
                case "M":
                    g.setColor(Color.BLUE);
                    break;
                case "P":
                    g.setColor(Color.RED);
                    break;
            }
            g.fillOval((int)(character.getAnimatedX() * cellSize) + 2,  (int)(character.getAnimatedY() * cellSize) + 2, cellSize - 3, cellSize - 3);

            // Set text color based on the character for easier reading
            if ("M".equals(character.getInitial()) || "P".equals(character.getInitial())) {
                g.setColor(Color.WHITE);
            } else {
                g.setColor(Color.BLACK);
            }
            
            int textWidth = fm.stringWidth(character.getInitial());
            int textHeight = fm.getAscent();
            int centeredX = (int) (character.getAnimatedX() * cellSize) + (cellSize - textWidth) / 2;
            int centeredY = (int) (character.getAnimatedY() * cellSize) + (cellSize + textHeight) / 2;
            g.drawString(character.getInitial(), centeredX, centeredY);
        }
    }

    public void updateWeapons(Graphics2D g, JPanel boardPanel){
        for(Weapon w: board.getWeapons()){
            drawWeapon(g, w.getName(), w.getX(), w.getY(), boardPanel);
        }
    }

    // Private methods
    private void drawWeapon(Graphics2D g, String weaponName, int x, int y, JPanel boardPanel) {
        int cellSize = Math.min(boardPanel.getWidth() / board.getBoardWidth(), boardPanel.getHeight() / board.getBoardHeight());
        BufferedImage weaponImage = weaponImages.get(weaponName);
        if (weaponImage != null) {
            g.drawImage(weaponImage, x * cellSize, y * cellSize, cellSize, cellSize, null);
        }
    }
}