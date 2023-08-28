import java.util.ArrayList;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.event.*;  

public class Initialiser implements ActionListener{
    private int playerCount = 0;
    JRadioButton a,b,c,d;
    
    JFrame playerSelect = new JFrame("player select");
    private static ArrayList<String> list = new ArrayList<String>();
    JLabel l;

    /**
     * Determines the amount of players in the game
     */
    Initialiser() {
        // Scanner sc;
        // //System.out.println("Type below how many players are playing (Choose between 3 or 4): ");
        // while (true) {
        //     sc = new Scanner(System.in);
        //     if (sc.hasNextInt()) {
        //         playerCount = sc.nextInt();
        //         if (playerCount == 3 || playerCount == 4) {
        //             break;
        //         }
        //     }
        //     System.out.println("Please input a valid player count :");
        // }
    }

    public void actionPerformed(ActionEvent e){  
        if(e.getActionCommand().equals("Select")&&list.size()!=4){
            if(a.isSelected()&&a.isEnabled()){
                a.setEnabled(false);
                a.setForeground(Color.gray);
                list.add(a.getText().toUpperCase());
            }
               if(b.isSelected()&&b.isEnabled()){
                b.setEnabled(false);
                b.setForeground(Color.gray);
                list.add(b.getText().toUpperCase());
            }
               if(c.isSelected()&&c.isEnabled()){
                c.setEnabled(false);
                c.setForeground(Color.gray);
                list.add(c.getText().toUpperCase());
            }
               if(d.isSelected()&&d.isEnabled()){
                d.setEnabled(false);
                d.setForeground(Color.gray);
                list.add(d.getText().toUpperCase());
            }
            if(list.size()!=4)
            l.setText("Player " + (list.size() + 1) + " please select your character:");

            if(list.size()>= 3){
                JButton thing2 = new JButton("Start Game");
                thing2.setBounds(40, 320 , 150 , 50);
                thing2.addActionListener(this);
                playerSelect.add(thing2);
                SwingUtilities.updateComponentTreeUI(playerSelect);
            }
        }
        if(e.getActionCommand().equals("Start Game")){
            
            if(list.size()==3){
                playerCount = 3;
                
            }
            if(list.size()==4){
                playerCount = 4;
            }

            
        }
      
    }  

    /**
     * Getter method
     *
     * @return The playerCount
     */
    public int getPlayerCount() {
        if(playerCount == 0){
            
            a = new JRadioButton("Lucilla");
            b = new JRadioButton("Bert");
            c = new JRadioButton("Malina");
            d = new JRadioButton("Percy");
            a.setBounds(80 , 50 , 200 , 30);
            b.setBounds(80 , 100 , 200 , 30);
            c.setBounds(80 , 150 , 200 , 30);
            d.setBounds(80 , 200 , 200 , 30);
            ButtonGroup bg = new ButtonGroup();
            bg.add(a);
            bg.add(b);
            bg.add(c);
            bg.add(d);
            playerSelect.add(a);
            playerSelect.add(b);
            playerSelect.add(c);
            playerSelect.add(d);
            JButton thing = new JButton("Select");
            
            thing.setBounds(40, 250 , 150 , 50);
            
            thing.addActionListener(this);
            
            playerSelect.add(thing);
            
            l = new JLabel("Player 1 please select your character:");
            l.setBounds(10, -70, 300, 200);
            playerSelect.add(l);
            playerSelect.setSize(265,450);
            playerSelect.setLayout(null);
            playerSelect.setVisible(true);
            while (playerCount < 3){
                //do nothing
                try {
                    Thread.sleep(100); // Delay for 1 second
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            playerSelect.dispose();
            //Main.stopped = false;
            return playerCount;
        }
        else
        return playerCount;
    }

    /**
     * Setup GUI for game start
     */
    public static void setup() {
        Main.board.takeList(list);
    }
}