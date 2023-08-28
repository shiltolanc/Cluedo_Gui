import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.Timer;

public class AnimationController {

    // Member variables
    private GameView view;
    private boolean isAnimating = false;
    private Board board;
    private int remainingMoves;

    // Constructor
    public AnimationController(GameView view, Board board, Character currentPlayer, int remainingMoves) {
        this.view = view;
        this.board = board;
        this.remainingMoves = remainingMoves;
    }

    // Public methods
    public void beginMoveAnimation(Character currentPlayer, List<Coord> path, int durationMs, Runnable onFinished) {
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
                    double progress = elapsed / (double) totalTime;
                    int pathIndex = Math.min((int) (progress * (path.size() - 1)), path.size() - 2);  // Ensure valid range

                    Coord start = path.get(pathIndex);
                    Coord end = path.get(pathIndex + 1);

                    double cellProgress = progress * (path.size() - 1) - pathIndex;
                    double newX = start.getX() + (end.getX() - start.getX()) * cellProgress;
                    double newY = start.getY() + (end.getY() - start.getY()) * cellProgress;

                    if (currentPlayer == null) {
                        // Log an error or handle this case appropriately
                        System.err.println("Error: currentPlayer is null");
                        timer.stop();
                        return;
                    }

                    currentPlayer.setAnimatedX(newX);
                    currentPlayer.setAnimatedY(newY);
                    view.repaint();
                }
            }
        });

        timer.start();
    }

    public boolean isAnimating() {
        return isAnimating;
    }
}