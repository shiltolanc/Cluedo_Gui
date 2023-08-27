import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.util.*;

public class Character {

    // Member variables
    private int x;
    private int y;
    private double animatedX;
    private double animatedY;
    private String initial = "";
    private Direction direction;
    private Set<Card> cards = new HashSet<>();
    // private boolean hasMadeFalseAccusation = false;

    // Constructor
    public Character(int x, int y, String initial, Direction direction) {
        this.x = x;
        this.y = y;
        this.animatedX = x;
        this.animatedY = y;
        this.initial += initial;
        this.direction = direction;
    }

    // Public methods
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }

    public Coord getCoord() {
        return new Coord(x, y);
    }

    public void setX(int x) { 
        this.x = x; 
        this.animatedX = x;
    }

    public void setY(int y) { 
        this.y = y; 
        this.animatedY = y;
    }

    public void setAnimatedX(double animatedX) {
        this.animatedX = animatedX;
    }

    public void setAnimatedY(double animatedY) {
        this.animatedY = animatedY;
    }

    public double getAnimatedX() {
        return animatedX;
    }

    public double getAnimatedY() {
        return animatedY;
    }
    
    public String getInitial() { 
        return initial;
    }

    public Direction getName(){
        return this.direction;
    }

    public void addCards(Card c){
        cards.add(c);
    }

    public Set<Card> getCards(){
        return cards;
    }

    public String toString(){
        return getName().toString();
    }

    /**
     * #######################################################################################################################
     *          TURN BASED SYSTEM - Below code used for Assignment 1 (Kept for reference, partially implemented)
     * #######################################################################################################################
     */
    // public void performTurn(Board b) {
    //     if (Main.UI.getPlayerCount() - Main.falseAccusationCount == 1) {  // check if only one player remains
    //         System.out.println("\nIt's " + this.getName() + "'s turn and they are the last player remaining!");
    //         System.out.println(this.getName() + " has won the game!");
    //         Main.isGameOn = false; 
    //         return;
    //     }

    //     if (!hasMadeFalseAccusation) {
    //         System.out.println("Players turn: " + this.getName() + "\n");
    //         System.out.println("please enter \"x\" ");

    //         Random rand = new Random();
    //         int moves = rand.nextInt(6) + 1 + rand.nextInt(6) + 1; // sum of two six-sided dice
    //         System.out.println('\n' + "================================");
    //         System.out.println("Players turn: " + this.getName() + "\n");
    //         System.out.println("your cards:");
    //         for(Card c : cards){
    //             System.out.println(c.toString());
    //         }
    //         System.out.println("\n" + this.getName() + " rolled " + moves + "!");
            
    //         // Check if the character can make a guess
    //         if (this.inEstate && !(this.e instanceof GreyArea)) {
    //             System.out.println("\nYou have entered " + e.getName() + ". You can make a guess.");
    //             makeGuess(b);
    //             if (!Main.isGameOn) {
    //                 return;
    //             }
    //             System.out.println("Guess completed. Please hand the tablet to the next player.");
    //         } else {
    //             System.out.println("You can't make a guess right now.");
    //         }

    //         // Update about the next turn
    //         System.out.println("Next player's turn: " + this.getName().next());

    //     } else {
    //         System.out.println("\nIt's " + this.getName() + "'s turn, but they made a false accusation. Skipping their turn.");
    //     }
    //     System.out.println("\nNext turn for: " + this.getName().next() + ". Hand over the device.");
    // }


    //     // Check if square is occupied by another character
    //     for(Character c: board.getCharacters()) {
    //         if(c != this && c.isHere(x, y) && !enterEstate(x, y)) { // If occupied character is not in estate
    //             errorMessage = "There is another character in your way";
    //             // return false;
    //         }
    //     }

    //     // Prevent users entering grey areas
    //     if (checkGrey(x, y)) {
    //         errorMessage = "You are trying to enter a grey area";
    //         // return false;
    //     }

    //     //prevents user from revisiting tiles
    //     for(Coord c : list){
    //         if(c.getX() == x && c.getY() == y){
    //             errorMessage = "You cannot backtrack";
    //             // return false;
    //         }
    //     }

    /**
     * #######################################################################################################################
     *        GUESS / REFUTE SYSTEM - Below code used for Assignment 1 (Kept for reference, requires implementation)
     * #######################################################################################################################
     */


    // /**
    //  * Asks the player to make a guess
    //  */
    // public void makeGuess(Board board) {
    //     Scanner scanner = new Scanner(System.in);
    //     Character characterGuessed = null;
    //     String weaponGuessed = null;
    //     Estate estateGuessed = this.estate;

    //     // Ask for guess type
    //     System.out.println("Would you like to hypothesize or take your final guess");
    //      System.out.println("1.Hypothesize \n2.Final Guess");
    //     String guessType = scanner.nextLine().toLowerCase();

    //     while (!(guessType.equals("1") || guessType.equals("2"))) {
    //         System.out.println("Invalid choice. Enter '1' or '2'.");
    //         guessType = scanner.nextLine().toLowerCase();
    //     }

    //     System.out.println("Pick a character:");
    //     int i = 1;
    //     for (Character c : board.getCharacters()) {
    //         System.out.println(i++ + ". " + c.getName());
    //     }
    //     int characterChoice = scanner.nextInt();
    //     characterGuessed = board.getCharacters().get(characterChoice - 1);

    //     System.out.println("Pick a weapon:");
    //     i = 1;
    //     for (String w : board.getWeapons()) {
    //         System.out.println(i++ + ". " + w);
    //     }
    //     int weaponChoice = scanner.nextInt();
    //     weaponGuessed = board.getWeapons().get(weaponChoice - 1);

    //     Initialiser.text.setText( board.toString());

    //     // Refute the guess
    //     if (guessType.equals("1")) {
    //         refuteGuess(characterGuessed, weaponGuessed, estateGuessed, board);
    //     } else { // Final guess
    //         if (!refuteFinalGuess(characterGuessed, weaponGuessed, estateGuessed, board)) {
    //             // Final guess was correct and no refutation was made. Guessing player wins.
    //             System.out.println(this.getName() + " has won the game!");
    //             Main.isGameOn = false;    // Set isGameOn to false when a player wins
    //             return;
    //         } else { // Final guess was incorrect
    //             Main.falseAccusationCount++;  // increase the count because of false accusation
    //             this.hasMadeFalseAccusation = true;  // set the flag that this player made a false accusation
    //             String in;
    //             System.out.println(this.getName() + " has been eliminated!");
    //             System.out.println("please enter \"x\" to accept your fate");
    //             in = sc.nextLine();
    //             while(!in.equals("x")){
    //                 System.out.println("please enter \"x\" to accept your fate");
    //                 in = sc.nextLine();
    //                 in = in.toLowerCase();
    //             }
    //             hasMadeFalseAccusation = true;
    //         }
    //     }
    // }

    // /**
    //  * Asks the next player to refute the guess
    //  */
    // public void refuteGuess(Character characterGuessed, String weaponGuessed, Estate estateGuessed, Board board) {
    //     Direction nextPlayerDirection = this.getName().next();
    //     boolean found = false;
    //     Card refute = null;
    //     String charName = "";
    //     List<Character> chars = board.getCharacters();
    //     int displace = 0;
    //     for(int i = 0; i  < chars.size()-1; i++){
    //         if(chars.get(i).getName().equals(this.getName())){
    //             displace = i;
    //         }
    //     }
    //     for(int i = 0; i  < displace; i++){
    //         chars.add(chars.get(0));
    //         chars.remove(0);
    //     }


    //     for(Character c : chars){
    //          if (c.getName() == nextPlayerDirection) {
    //             if(found){
    //                 System.out.println("\nHand the device to " + nextPlayerDirection
    //                     + "\n. Confirm you have taken the device and type 'x': ");

    //                 String confirmation = new Scanner(System.in).nextLine().toLowerCase();

    //                 while (!confirmation.equals("x")) {
    //                     System.out.println("Please type 'x' when you are ready:");
    //                     confirmation = new Scanner(System.in).nextLine().toLowerCase();
    //                 }

    //                 System.out.println("\n" + charName
    //                     + " has refuted using one of these cards \n" +
    //                     characterGuessed.getName() + "\n"+
    //                     weaponGuessed + "\n" +
    //                     estateGuessed.getName() + "\n");
                    

    //                 System.out.println("\n Please type 'x' to confirm");
    //                 confirmation = new Scanner(System.in).nextLine().toLowerCase();

    //                 while (!confirmation.equals("x")) {
    //                     System.out.println("Please type 'x' when you are ready:");
    //                     confirmation = new Scanner(System.in).nextLine().toLowerCase();
    //                 }
    //                 nextPlayerDirection = nextPlayerDirection.next();

    //             }
    //             else{

    //                 System.out.println("\nIt's time for " + nextPlayerDirection
    //                     + " to potentially refute the guess. \nConfirm you have taken the device and type 'x': ");

    //                 String confirmation = new Scanner(System.in).nextLine().toLowerCase();

    //                 while (!confirmation.equals("x")) {
    //                     System.out.println("Please type 'x' when you are ready:");
    //                     confirmation = new Scanner(System.in).nextLine().toLowerCase();
    //                 }

    //                 List<Card> chosenCards = List.of(
    //                     new Card(characterGuessed.getName().toString(), Card.CardType.CHARACTER),
    //                     new Card(weaponGuessed, Card.CardType.WEAPON),
    //                     new Card(estateGuessed.getName(), Card.CardType.ESTATE));

    //                 List<Card> matchingCards = chosenCards.stream().filter(card ->
    //                     c.getCards().contains(card)).collect(Collectors.toList());

    //                 if (!matchingCards.isEmpty()) {
    //                     System.out.println("You have these cards. Which one do you want to reveal?");

    //                     for (int i = 0; i < matchingCards.size(); i++) {
    //                         System.out.println((i+1) + ": " + matchingCards.get(i));
    //                     }

    //                     int revealedCardChoice = new Scanner(System.in).nextInt();
    //                     System.out.println("You have revealed: " + matchingCards.get(revealedCardChoice - 1));

    //                     found = true;
    //                     charName = nextPlayerDirection.toString();
    //                     refute = matchingCards.get(revealedCardChoice - 1);
    //                 } else {
    //                     System.out.println("\n" +  this.direction
    //                     + " has guessed: \n" + " - " +
    //                     characterGuessed.getName() + "\n" + " - " +
    //                     weaponGuessed + "\n" + " - " +
    //                     estateGuessed.getName() + "\n");
    //                     System.out.println("You don't have any of these cards.");
    //                     System.out.println("\n Please type 'x' to confirm");
    //                     confirmation = new Scanner(System.in).nextLine().toLowerCase();
    //                     while (!confirmation.equals("x")) {
    //                         System.out.println("Please type 'x' when you are ready:");
    //                         confirmation = new Scanner(System.in).nextLine().toLowerCase();
    //                     }

    //                 }
    //                 nextPlayerDirection = nextPlayerDirection.next();
    //             }
    //         }
    //     }
    //     System.out.println("Please pass to: " + this.getName() + "\n");
    //     System.out.println("please enter \"x\" to see your guess results");
    //     String in;
    //     in = sc.nextLine();
    //     in = in.toLowerCase();
    //     while(!in.equals("x")){
    //         System.out.println("please enter \"x\" to see your guess results");
    //         in = sc.nextLine();
    //         in = in.toLowerCase();
    //     }
    //     if(!found){
    //         System.out.println("No player was able to refute the guess!");
    //         System.out.println("please enter \"x\" to end your turn");
    //         in = sc.nextLine();
    //         while(!in.equals("x")){
    //             System.out.println("please enter \"x\" to end your turn");
    //             in = sc.nextLine();
    //             in = in.toLowerCase();
    //         }
    //     }
    //     else{
    //         System.out.println(charName + " refuted your guess using " + refute);
    //         System.out.println("please enter \"x\" to end your turn");
    //         in = sc.nextLine();
    //         while(!in.equals("x")){
    //             System.out.println("please enter \"x\" to end your turn");
    //             in = sc.nextLine();
    //             in = in.toLowerCase();
    //         }
    //     }
    // }

    // /**
    //  * Checks if the final guess is correct
    //  */
    // public boolean refuteFinalGuess(Character characterGuessed, String weaponGuessed, Estate estateGuessed, Board board) {
    //     Direction thisPlayerDirection = this.getName();
    //     Direction nextPlayerDirection = thisPlayerDirection.next();

    //     // Return true, signifying the guess was refuted
    //     for (Character character : board.getCharacters()) {
    //         if (character.getName() == nextPlayerDirection) {
    //             for (Card c : character.getCards()) {
    //                 if (c.equals(new Card(characterGuessed.getName().toString(), Card.CardType.CHARACTER)) ||
    //                         c.equals(new Card(weaponGuessed, Card.CardType.WEAPON)) ||
    //                         c.equals(new Card(estateGuessed.getName(), Card.CardType.ESTATE))) {
    //                     System.out.println("You've guessed wrong!");
    //                     return true;
    //                 }
    //             }
    //             nextPlayerDirection = nextPlayerDirection.next();
    //         }
    //     }

    //     // Return false, indicating that the final guess is correct
    //     for (Card c : board.getMurderCards()) {
    //         if (!c.equals(new Card(characterGuessed.getName().toString(), Card.CardType.CHARACTER)) &&
    //                 !c.equals(new Card(weaponGuessed, Card.CardType.WEAPON)) &&
    //                 !c.equals(new Card(estateGuessed.getName(), Card.CardType.ESTATE))) {
    //             return true;
    //         }
    //     }
    //     return false;
//     }

    // Enums
    /**
     * Order of rotation for characters following an anti-clockwise rotation
     */
    public enum Direction {
        /* Topmost position on the table. (NORTH) */
        LUCILLA,
        /* Rightmost position on the table. (EAST) */
        BERT,
        /* Bottom position on the table. (SOUTH) */
        MALINA,
        /* Leftmost position on the table. (WEST) */
        PERCY;

        /**
         * Returns the next direction to play after this one
         * @return The rotated direction.
         */

        public Direction next() {
            if (this.equals(LUCILLA)) {
                return BERT;
            }
            if (this.equals(BERT)) {
                return MALINA;
            }
            if (this.equals(MALINA)) {
                return (Main.UI.getPlayerCount() == 3) ? LUCILLA : PERCY;
            }
            return LUCILLA;
        }
    }
}