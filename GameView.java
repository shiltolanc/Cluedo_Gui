import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Set;

public class GameView extends JFrame {

    // Panels
    private JPanel boardPanel;
    private JPanel containerPanel;
    private JPanel infoPanel;

    // Labels
    private JLabel playerTurnLabel;
    private JLabel diceRollLabel;
    private JLabel movesRemainingLabel;

    // Buttons
    private JButton rollDiceButton;
    private JButton makeGuessButton;
    private JButton endTurnButton;

    // Text Components
    private JTextArea logs;
    private JList<String> playerCardsList;

    // Game Controllers and Renderers
    private GameController gameController;
    private BoardRenderer boardRenderer;
    
    // Constructors
    public GameView(Board board, GameController gameController) {
        this.gameController = gameController;
        this.boardRenderer = new BoardRenderer(board, gameController);
    
        // Basic JFrame settings
        setupFrame();
    
        // Initialize panels
        initBoardPanel();
        initInfoPanel();
        initLogger();
    
        // Display the JFrame
        setVisible(true);
    }

    // Public Methods
    public Dimension getBoardPanelSize() {
        return boardPanel.getSize();
    }

    public void logMessage(String msg) {
        logs.append(msg + "\n");
    }
    
    public void updatePlayerTurnLabel(String playerName) {
        Color playerColor = gameController.determinePlayerColor(playerName.charAt(0));
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

    public void addEndTurnButtonListener(ActionListener listener) {
        endTurnButton.addActionListener(listener);
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
    private void setupFrame() {
        setTitle("Hobby Detectives");
        setSize(940, 650);
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(940, 690));
        setAlwaysOnTop(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initBoardPanel() {
        boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                boardRenderer.drawBoard((Graphics2D) g, this);
            }
        };
    
        // Wrap boardPanel with another panel to create a margin
        containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 12));
        containerPanel.add(boardPanel);
        boardPanel.setBackground(Color.WHITE);
        add(containerPanel, BorderLayout.CENTER);
    }

    private void initInfoPanel() {
        setupInfoPanel();
        setupPlayerTurnPanel();
        setupDiceAndMovesPanel();
        setupButtons();
        setupPlayerCardsPanel();
        addComponentsToInfoPanel();
    }
    
    private void setupInfoPanel() {
        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setPreferredSize(new Dimension(275, 500));
        infoPanel.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(12, 12, 12, 12),
                BorderFactory.createTitledBorder(
                    BorderFactory.createBevelBorder(BevelBorder.LOWERED),
                    "Hobby Detectives",
                    TitledBorder.CENTER,
                    TitledBorder.TOP,
                    new Font("Arial", Font.BOLD, 24),
                    Color.BLACK
                )
            )
        );
    }
    
    private void setupPlayerTurnPanel() {
        playerTurnLabel = new JLabel("Player's turn: ");
        playerTurnLabel.setOpaque(true);
        playerTurnLabel.setHorizontalAlignment(JLabel.CENTER);
    
        JPanel playerTurnPanel = new JPanel(new BorderLayout());
        playerTurnPanel.setPreferredSize(new Dimension(getWidth(), 80));
        playerTurnPanel.setMaximumSize(new Dimension(getWidth(), 80));
        playerTurnPanel.add(playerTurnLabel, BorderLayout.CENTER);
        playerTurnPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(12,12,0,12),
            BorderFactory.createTitledBorder(
                BorderFactory.createBevelBorder(BevelBorder.LOWERED),
                "Player's turn",
                TitledBorder.CENTER,
                TitledBorder.CENTER,
                new Font("Arial", Font.PLAIN, 12),
                Color.BLACK
            )
        ));
    
        infoPanel.add(playerTurnPanel);
    }
    
    private void setupDiceAndMovesPanel() {
        diceRollLabel = createCenteredLabel("0", 32);
        movesRemainingLabel = createCenteredLabel("0", 32);
    
        JPanel diceRollPanel = createTitledPanel("Dice roll", diceRollLabel);
        JPanel movesRemainingPanel = createTitledPanel("Moves left", movesRemainingLabel);
    
        JPanel diceAndMovesPanel = new JPanel(new GridLayout(1, 2));
        diceAndMovesPanel.setPreferredSize(new Dimension(getWidth(), 80));
        diceAndMovesPanel.add(diceRollPanel);
        diceAndMovesPanel.add(movesRemainingPanel);
    
        infoPanel.add(diceAndMovesPanel);
    }
    
    private void setupButtons() {
        rollDiceButton = new JButton("Roll Dice!");
        makeGuessButton = new JButton("Make a Guess");
        endTurnButton = new JButton("End Turn");
    
        infoPanel.add(Box.createRigidArea(new Dimension(0,20)));
        infoPanel.add(rollDiceButton);
        infoPanel.add(Box.createRigidArea(new Dimension(0,20)));
        infoPanel.add(makeGuessButton);
        infoPanel.add(Box.createRigidArea(new Dimension(0,20)));
        infoPanel.add(endTurnButton);
        infoPanel.add(Box.createRigidArea(new Dimension(0,20)));
    }
    
    private void setupPlayerCardsPanel() {
        playerCardsList = new JList<>();
        playerCardsList.setFixedCellHeight(20);
        playerCardsList.setFixedCellWidth(220);
        playerCardsList.setBackground(Color.getColor(infoPanel.getName()));
        playerCardsList.setSelectionBackground(Color.getColor(infoPanel.getName()));
        playerCardsList.setFocusable(false);
    
        JScrollPane playerCardsScrollPane = new JScrollPane(playerCardsList);
        playerCardsScrollPane.setMinimumSize(new Dimension(220, 80));
        playerCardsScrollPane.setPreferredSize(new Dimension(220, 80));
        playerCardsScrollPane.setBorder(null);

        JPanel cardPanel = new JPanel();
        cardPanel.setBorder(BorderFactory.createTitledBorder("Your Cards"));
        cardPanel.add(playerCardsScrollPane);
    
        infoPanel.add(cardPanel);
    }
    
    private JLabel createCenteredLabel(String text, int fontSize) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, fontSize));
        label.setHorizontalAlignment(JLabel.CENTER);
        return label;
    }
    
    private JPanel createTitledPanel(String title, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(12,12,12,12),
            BorderFactory.createTitledBorder(
                BorderFactory.createBevelBorder(BevelBorder.LOWERED),
                title,
                TitledBorder.CENTER,
                TitledBorder.CENTER,
                new Font("Arial", Font.PLAIN, 12),
                Color.BLACK
            )
        ));
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }
    
    private void addComponentsToInfoPanel() {
        add(infoPanel, BorderLayout.EAST);
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