public class Main {

    // Member variables
    static Initialiser UI = new Initialiser();
    static Board board = new Board();
    public static boolean isGameOn = true;
    public static int falseAccusationCount = 0;

    // Main method
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
    
        board.determineCharacters();
        Initialiser.setup();
    
        GameController controller = new GameController(board);
        GameView view = new GameView(board, controller);
        controller.setView(view);
        controller.initListeners();

        // while (Main.isGameOn) {
        //     for (Character character : board.getCharacters()) {
        //         character.performTurn(board);
        //         if (!Main.isGameOn) break;
        //     }
        // }
        // System.out.println("Game over, please close the window.");
    }
}