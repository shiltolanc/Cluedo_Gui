import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GameView extends JFrame {

    // Member Variables
    private Board board;
    private JPanel boardPanel;
    private JPanel containerPanel;
    private JPanel infoPanel;
    private JLabel playerTurnLabel;
    private JLabel diceRollLabel;
    private JLabel movesRemainingLabel;
    private JButton rollDiceButton;
    private JButton makeGuessButton;
    private JButton endTurnButton;
    private JTextArea logs;
    private JList<String> playerCardsList;
    private GameController gameController;
    private Estate hoverableEstate;
    private List<Estate> unreachableEstates = new ArrayList<>();
    private Map<String, BufferedImage> weaponImages = new HashMap<>();
    
    // Constructors
    public GameView(Board board, GameController gameController) {
        this.board = board;
        this.gameController = gameController;

        // Load weapon images
        loadWeaponImages();
    
        // Basic JFrame settings
        setTitle("Hobby Detectives");
        setSize(940, 650);
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(940, 690));
        setAlwaysOnTop(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
        // Initialize panels
        initBoardPanel();
        initInfoPanel();
        boardPanel.setBackground(Color.WHITE);    
    
        // Add panels to the JFrame
        add(containerPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.EAST);
    
        // Styling for infoPanel
        infoPanel.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(12, 12, 12, 12),
                BorderFactory.createTitledBorder(
                    BorderFactory.createBevelBorder(BevelBorder.LOWERED),
                    "Hobby Detectives",
                    TitledBorder.CENTER,
                    TitledBorder.TOP,
                    new Font("Gilroy", Font.BOLD, 24),
                    Color.BLACK
                )
            )
        );
    
        // Set initial state for the game
        updatePlayerTurnLabel(board.getCharacters().get(0).getName().name());
    
        // Logger initialization
        initLogger();
    
        // Display the JFrame
        setVisible(true);
    }

    // Public Methods
    public Dimension getBoardPanelSize() {
        // Get board panel dimensions so mouselistener works correctly when window is resized
        return boardPanel.getSize();
    }

    public void logMessage(String msg) {
        logs.append(msg + "\n");
    }
    
    public void updatePlayerTurnLabel(String playerName) {
        Color playerColor = getPlayerColor(playerName.charAt(0));
        Icon playerIcon = new PlayerIcon(playerColor);
        playerTurnLabel.setFont(new Font("Arial", Font.BOLD, 18));
        playerTurnLabel.setIcon(playerIcon);
        playerTurnLabel.setText(" " + playerName);
    }

    public void setRollDiceButtonEnabled(boolean enabled) {
        rollDiceButton.setEnabled(enabled);
    }

    public void updateDiceRollLabel(int diceNumber) {
        diceRollLabel.setText(String.valueOf(diceNumber));
    }
    
    public void updateMovesRemainingLabel(int remainingMoves) {
        movesRemainingLabel.setText(String.valueOf(remainingMoves));
    }

    public void addRollDiceButtonListener(ActionListener listener) {
        rollDiceButton.addActionListener(listener);
    }    

    public void addMakeGuessButtonListener(ActionListener listener) {
        makeGuessButton.addActionListener(listener);
    }

    public void addBoardMouseListener(MouseListener listener) {
        boardPanel.addMouseListener(listener);
    }

    public void addBoardMouseMotionListener(MouseMotionListener listener) {
        boardPanel.addMouseMotionListener(listener);
    }

    public void updatePlayerCards(Set<Card> cards) {
        DefaultListModel<String> model = new DefaultListModel<>();
        for (Card card : cards) {
            model.addElement(card.toString());
        }
        SwingUtilities.invokeLater(() -> {
            playerCardsList.setModel(model);
            playerCardsList.revalidate();
            playerCardsList.repaint();
        });
    }

    // Private Methods
    private void initBoardPanel() {
        boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard((Graphics2D) g);
            }
        };
    
        // Wrap boardPanel with another panel to create a margin
        containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 12));
        containerPanel.add(boardPanel);
    }

    private void initInfoPanel() {
        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setPreferredSize(new Dimension(275, 500));

        // Players turn label
        playerTurnLabel = new JLabel("Player's turn: ");
        playerTurnLabel.setOpaque(true);
        playerTurnLabel.setHorizontalAlignment(JLabel.CENTER);

        // Create a panel to house the label
        JPanel playerTurnPanel = new JPanel(new BorderLayout());

        // Set the height for the panel
        int panelHeight = 80;
        playerTurnPanel.setPreferredSize(new Dimension(getWidth(), panelHeight));
        playerTurnPanel.setMaximumSize(new Dimension(getWidth(), panelHeight));
        playerTurnPanel.add(playerTurnLabel, BorderLayout.CENTER);

        playerTurnPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(12,12,0,12), BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED),"Player's turn", TitledBorder.CENTER, TitledBorder.CENTER, new Font("Arial", Font.PLAIN, 12), Color.BLACK)));

        // Dice roll
        diceRollLabel = new JLabel("0");
        JPanel diceRollPanel = new JPanel();
        diceRollPanel.setLayout(new BorderLayout());
        diceRollPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(12,12,12,12), BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED),"Dice roll", TitledBorder.CENTER, TitledBorder.CENTER, new Font("Arial", Font.PLAIN, 12), Color.BLACK)));
        diceRollPanel.add(diceRollLabel, BorderLayout.CENTER);
        // Font settings for moves remaining label
        Font labelFont2 = diceRollLabel.getFont();
        diceRollLabel.setFont(new Font(labelFont2.getName(), Font.PLAIN, 32));
        diceRollLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Moves remaining
        movesRemainingLabel = new JLabel("0");
        JPanel movesRemainingPanel = new JPanel();
        movesRemainingPanel.setLayout(new BorderLayout());
        movesRemainingPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(12,12,12,12), BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED),"Moves left", TitledBorder.CENTER, TitledBorder.CENTER, new Font("Arial", Font.PLAIN, 12), Color.BLACK)));

        movesRemainingPanel.add(movesRemainingLabel, BorderLayout.CENTER);
        // Font settings for moves remaining label
        Font labelFont = movesRemainingLabel.getFont();
        movesRemainingLabel.setFont(new Font(labelFont.getName(), Font.PLAIN, 32));
        movesRemainingLabel.setHorizontalAlignment(JLabel.CENTER);

        // Panel to hold dice roll and moves remaining side by side
        JPanel diceAndMovesPanel = new JPanel(new GridLayout(1, 2));
        diceAndMovesPanel.setPreferredSize(new Dimension(getWidth(), 80)); // Row height
        
        diceAndMovesPanel.add(diceRollPanel);
        diceAndMovesPanel.add(movesRemainingPanel);

        rollDiceButton = new JButton("Roll Dice!");
        makeGuessButton = new JButton("Make a Guess");
        endTurnButton = new JButton("End Turn");

        // Display the current players cards
        playerCardsList = new JList<>(); 
        playerCardsList.setFixedCellHeight(20);
        playerCardsList.setFixedCellWidth(220);
        JScrollPane playerCardsScrollPane = new JScrollPane(playerCardsList);
        // Set a minimum and preferred size for the JScrollPane
        Dimension listSize = new Dimension(infoPanel.getWidth(), 80);
        playerCardsScrollPane.setMinimumSize(listSize);
        playerCardsScrollPane.setPreferredSize(listSize);
        
        JPanel cardPanel = new JPanel();
        cardPanel.add(playerCardsList);

        playerCardsList.setBackground(Color.getColor(getName())); // Set the JList item color to the same color as the background color
        playerCardsList.setSelectionBackground(Color.getColor(getName())); // Set the JList selection background color to backround color
        playerCardsList.setFocusable(false); // Make JList items unselectable

        cardPanel.setBorder(BorderFactory.createTitledBorder("Your Cards")); // Set the border
        cardPanel.add(playerCardsScrollPane);

        infoPanel.add(playerTurnPanel);
        infoPanel.add(diceAndMovesPanel);
        infoPanel.add(Box.createRigidArea(new Dimension(0,20)));
        infoPanel.add(rollDiceButton);
        infoPanel.add(Box.createRigidArea(new Dimension(0,20)));
        infoPanel.add(makeGuessButton);
        infoPanel.add(Box.createRigidArea(new Dimension(0,20)));
        infoPanel.add(endTurnButton);
        infoPanel.add(Box.createRigidArea(new Dimension(0,20)));
        infoPanel.add(cardPanel);
    }

    private void initLogger() {
        logs = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(logs);
        logs.setEditable(false);
    
        JPanel loggerPanel = new JPanel(new BorderLayout());
        loggerPanel.setBorder(BorderFactory.createTitledBorder("History"));
        loggerPanel.add(scrollPane, BorderLayout.CENTER);

        // Font size
        Font labelFont = logs.getFont();
        logs.setFont(new Font(labelFont.getName(), Font.PLAIN, 11));

        // Add the logger JPanel to the infoPanel
        infoPanel.add(loggerPanel);
    }

    private Color getPlayerColor(char initial) {
        switch(initial) {
            case 'L':
                return Color.GREEN;
            case 'B':
                return Color.YELLOW;
            case 'M':
                return Color.BLUE;
            case 'P':
                return Color.RED;
            default:
                return Color.BLACK;
        }
    }

    private void loadWeaponImages() {
        // Initialize weapon images
        try {
            weaponImages.put("Broom", ImageIO.read(getClass().getResource("/images/broom.png")));
            weaponImages.put("Scissors", ImageIO.read(getClass().getResource("/images/scissors.png")));
            weaponImages.put("Knife", ImageIO.read(getClass().getResource("/images/knife.png")));
            weaponImages.put("Shovel", ImageIO.read(getClass().getResource("/images/shovel.png")));
            weaponImages.put("iPad", ImageIO.read(getClass().getResource("/images/ipad.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawBoard(Graphics2D g) {
        // Enable Anti-aliasing for shapes and text
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

        // Draw estates and grey areas
        for (Estate estate : board.getEstates()) {
            int thickness = 3;  // Line thickness
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
        // // Draw the estates with colors
        // for (Estate estate : board.getEstates()) {
        //     switch (estate.getName()) {
        //         case "Haunted House":
        //             g.setColor(new Color(205, 92, 92, 150));  // Light Coral
        //             break;
        //         case "Manic Manor":
        //             g.setColor(new Color(85, 107, 47, 150));  // Dark Olive Green
        //             break;
        //         case "Calamity Castle":
        //             g.setColor(new Color(70, 130, 180, 150));  // Steel Blue
        //             break;
        //         case "Peril Palace":
        //             g.setColor(new Color(139, 69, 19, 150));  // Saddle Brown
        //             break;
        //         case "Visitation Villa":
        //             g.setColor(new Color(255, 140, 0, 150));  // Dark Orange
        //             break;
        //         default:
        //             g.setColor(Color.LIGHT_GRAY);
        //             break;
        //     }
        //     g.fillRect(estate.getX() * cellSize + 2, estate.getY() * cellSize + 2, (estate.getX2() - estate.getX() + 1) * cellSize - 3, (estate.getY2() - estate.getY() + 1) * cellSize - 3);
        // }

        // Draw estate entrances
        int thickness = 3;  // Line thickness
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

        // Print names on each estate
        Font boardFont = new Font("Gilroy", Font.BOLD, cellSize / 2); // Dynamically adjust font size based on cell size
        g.setFont(boardFont);
        g.setColor(Color.BLACK);
        // g.setColor(Color.WHITE);

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
        drawWeapon(g, "Broom", 5, 20);
        drawWeapon(g, "Scissors", 18, 6);
        drawWeapon(g, "Knife", 3, 3);
        drawWeapon(g, "Shovel", 10, 10);
        drawWeapon(g, "iPad", 20, 21);

        // Draw characters
        FontMetrics fm = g.getFontMetrics();
        for (Character character : board.getCharacters()) {
            switch(character.getInitial()) {
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
            g.setColor(Color.BLACK);
            
            int textWidth = fm.stringWidth(character.getInitial());
            int textHeight = fm.getAscent();
            int centeredX = (int)(character.getAnimatedX() * cellSize) + (cellSize - textWidth) / 2;
            int centeredY = (int)(character.getAnimatedY() * cellSize) + (cellSize + textHeight) / 2;
            g.drawString(character.getInitial(), centeredX, centeredY);
        }
    }

    private void drawWeapon(Graphics2D g, String weaponName, int x, int y) {
        int cellSize = Math.min(boardPanel.getWidth() / board.getBoardWidth(), boardPanel.getHeight() / board.getBoardHeight());
        BufferedImage weaponImage = weaponImages.get(weaponName);
        if (weaponImage != null) {
            g.drawImage(weaponImage, x * cellSize, y * cellSize, cellSize, cellSize, null);
        }
    }
    
    // Nested
    class PlayerIcon implements Icon {
        private final int size = 24;
        private Color color;
    
        public PlayerIcon(Color color) {
            this.color = color;
        }
    
        @Override
        public int getIconWidth() {
            return size;
        }
    
        @Override
        public int getIconHeight() {
            return size;
        }
    
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.fillOval(x, y, size, size);
        }
    }
}