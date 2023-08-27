import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
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
    private JButton endTurnButton;
    // private JButton makeGuessButton;

    // Text Components
    private JTextArea logs;
    private JList<String> playerCardsList;

    // Game Controllers and Renderers
    private GameController gameController;
    private BoardRenderer boardRenderer;

    private Font glutenLightFont;
    private Font glutenBoldFont;
    private Font glutenExtraLightFont;
    private Font glutenRegularFont;
    private Font glutenFont;

    // Constructors
    public GameView(Board board, GameController gameController) {
        this.gameController = gameController;
        this.boardRenderer = new BoardRenderer(board, gameController);

        // Load custom font
        try {
            glutenRegularFont = FontLoader.loadFont("/fonts/Gluten-Regular.ttf");
            glutenBoldFont = FontLoader.loadFont("/fonts/Gluten-Bold.ttf");
            glutenExtraLightFont = FontLoader.loadFont("/fonts/Gluten-ExtraLight.ttf");
            glutenLightFont = FontLoader.loadFont("/fonts/Gluten-Light.ttf");
        } catch (IOException | FontFormatException e) {
            glutenFont = new Font("Arial", Font.PLAIN, 12); // fallback
        }
        if (glutenFont == null) {
            glutenFont = new Font("Arial", Font.PLAIN, 12); // additional safety fallback
        }

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
        Font playerTurnLabelFont = glutenRegularFont.deriveFont(Font.BOLD, 24);

        playerTurnLabel.setFont(playerTurnLabelFont);
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

    // public void addMakeGuessButtonListener(ActionListener listener) {
    //     makeGuessButton.addActionListener(listener);
    // }

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
        Font titleFont = glutenBoldFont.deriveFont(Font.BOLD, 22);

        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setPreferredSize(new Dimension(275, 500));
        infoPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(20, 12, 12, 12),
                        BorderFactory.createTitledBorder(
                                BorderFactory.createBevelBorder(BevelBorder.LOWERED),
                                "Hobby Detectives",
                                TitledBorder.CENTER,
                                TitledBorder.TOP,
                                titleFont,
                                Color.BLACK
                        )
                )
        );
    }

    private void setupPlayerTurnPanel() {
        playerTurnLabel = new JLabel("Player's turn: ");
        playerTurnLabel.setOpaque(true);
        playerTurnLabel.setHorizontalAlignment(JLabel.CENTER);

        Font playerTurnFont = glutenExtraLightFont.deriveFont(Font.BOLD, 14);

        JPanel playerTurnPanel = new JPanel(new BorderLayout());
        playerTurnPanel.setPreferredSize(new Dimension(getWidth(), 80));
        playerTurnPanel.setMaximumSize(new Dimension(getWidth(), 80));
        playerTurnPanel.add(playerTurnLabel, BorderLayout.CENTER);
        playerTurnPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(12, 12, 0, 12),
                BorderFactory.createTitledBorder(
                        BorderFactory.createBevelBorder(BevelBorder.LOWERED),
                        "Player's turn",
                        TitledBorder.CENTER,
                        TitledBorder.CENTER,
                        playerTurnFont,
                        Color.BLACK
                )
        ));

        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(playerTurnPanel);
    }

    private void setupDiceAndMovesPanel() {
        diceRollLabel = createCenteredLabel("0");
        movesRemainingLabel = createCenteredLabel("0");

        JPanel diceRollPanel = createTitledPanel("Dice roll", diceRollLabel);
        JPanel movesRemainingPanel = createTitledPanel("Moves left", movesRemainingLabel);

        JPanel diceAndMovesPanel = new JPanel(new GridLayout(1, 2));
        diceAndMovesPanel.setPreferredSize(new Dimension(getWidth(), 80));
        diceAndMovesPanel.add(diceRollPanel);
        diceAndMovesPanel.add(movesRemainingPanel);

        infoPanel.add(diceAndMovesPanel);
    }

    private void setupButtons() {
        Font rollDiceButtonFont = glutenBoldFont.deriveFont(Font.BOLD, 20);
        Font endTurnButtonFont = glutenLightFont.deriveFont(Font.BOLD, 15);

        rollDiceButton = new JButton("Roll Dice!");
        rollDiceButton.setPreferredSize(new Dimension(220, 40));
        rollDiceButton.setMaximumSize(new Dimension(220, 40));
        rollDiceButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        rollDiceButton.setBackground(Color.GREEN);
        rollDiceButton.setFont(rollDiceButtonFont);
        rollDiceButton.setBackground(new Color(0x2dce98));
        rollDiceButton.setForeground(Color.white);
        rollDiceButton.setUI(new StyledButtonUI());

        endTurnButton = new JButton("End Turn");
        endTurnButton.setPreferredSize(new Dimension(220, 30));
        endTurnButton.setMaximumSize(new Dimension(220, 30));
        endTurnButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        endTurnButton.setBackground(Color.GREEN);
        endTurnButton.setFont(endTurnButtonFont);
        endTurnButton.setBackground(Color.WHITE);
        endTurnButton.setForeground(Color.BLACK);
        endTurnButton.setUI(new StyledButtonUI());

        // makeGuessButton = new JButton("Make a Guess");

        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(rollDiceButton);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(endTurnButton);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        // infoPanel.add(makeGuessButton);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    }


    private void setupPlayerCardsPanel() {
        playerCardsList = new JList<>();
        playerCardsList.setFixedCellHeight(20);
        playerCardsList.setFixedCellWidth(210);
        playerCardsList.setBackground(Color.getColor(infoPanel.getName()));
        playerCardsList.setSelectionBackground(Color.getColor(infoPanel.getName()));
        playerCardsList.setFocusable(false);

        JScrollPane playerCardsScrollPane = new JScrollPane(playerCardsList);
        playerCardsScrollPane.setMinimumSize(new Dimension(210, 80));
        playerCardsScrollPane.setPreferredSize(new Dimension(210, 80));
        playerCardsScrollPane.setBorder(null);

        JPanel cardPanel = new JPanel();
        cardPanel.setBorder(BorderFactory.createTitledBorder("Your Cards"));
        cardPanel.add(playerCardsScrollPane);

        Font playerCardsFont = glutenExtraLightFont.deriveFont(Font.BOLD, 15);
        playerCardsList.setFont(playerCardsFont);

        infoPanel.add(cardPanel);
    }

    private JLabel createCenteredLabel(String text) {
        Font centeredLabelFont = glutenLightFont.deriveFont(Font.BOLD, 32);

        JLabel label = new JLabel(text);
        label.setFont(centeredLabelFont);
        label.setHorizontalAlignment(JLabel.CENTER);
        return label;
    }

    private JPanel createTitledPanel(String title, JComponent component) {
        Font titledPanelFont = glutenExtraLightFont.deriveFont(Font.BOLD, 12);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(12, 12, 12, 12),
                BorderFactory.createTitledBorder(
                        BorderFactory.createBevelBorder(BevelBorder.LOWERED),
                        // BorderFactory.createBevelBorder(BevelBorder.RAISED),
                        title,
                        TitledBorder.CENTER,
                        TitledBorder.CENTER,
                        titledPanelFont,
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
        logs.setMargin(new Insets(6, 8, 0, 8));

        // Enable line wrapping to prevent horizontal scrolling
        logs.setLineWrap(true);
        logs.setWrapStyleWord(true);

        // Keep the log scrolled to the bottom
        DefaultCaret caret = (DefaultCaret) logs.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

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