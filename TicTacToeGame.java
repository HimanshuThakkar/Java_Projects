import java.util.Scanner;
import javax.swing.event.*;
import java.util.Random;
import java.io.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.*;
import javax.swing.*;
import java.applet.*;
public class TicTacToeGame extends JApplet implements ActionListener {
    
    
    private JButton Buttonforo, Buttonforx;
    private Board board;
    private int Thicknessofline =14;
    static final char Space=' ', O='O', X='X';
    private char position[]={
    Space, Space, Space,
    Space, Space, Space,
    Space, Space, Space};
    private Color colorforo=Color.RED, colorforx=Color.BLUE;
    public int winning=0, loss=0, Gamedraw=0;  // game count by user
    
    
    
    // Initialize
    public void init() {
        JPanel topPanel=new JPanel();
        topPanel.add(Buttonforo=new JButton("O Color"));
        topPanel.add(Buttonforx=new JButton("X Color"));
        Buttonforo.addActionListener(this);
        Buttonforx.addActionListener(this);
        add(topPanel, BorderLayout.SOUTH);
        add(board=new Board(), BorderLayout.CENTER);
        setVisible(true);
    }
    
    
    
    // Change color of O or X
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==Buttonforo) {
            Color newColor = JColorChooser.showDialog(this, "Select color for O", colorforo);
            if (newColor!=null)
                colorforo=newColor;
        }
        else if (e.getSource()==Buttonforx) {
            Color newColor = JColorChooser.showDialog(this, "select color for X", colorforx);
            if (newColor!=null)
                colorforx=newColor;
        }
        board.repaint();
    }
    
    // Board –game play and display
    
    private class Board extends JPanel implements MouseListener {
        private Random random=new Random();
        private int rows[][]={{0,2},{3,5},{6,8},{0,6},{1,7},{2,8},{0,8},{2,6}};
        
        public Board() {
            addMouseListener(this);
        }
        
        // board redraw
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            int w=getWidth();
            int h=getHeight();
            Graphics2D g2d = (Graphics2D) g;
            
            // Draw the grid
            g2d.setPaint(Color.YELLOW);
            g2d.fill(new Rectangle2D.Double(0, 0, w, h));
            g2d.setPaint(Color.WHITE);
            g2d.draw(new Line2D.Double(0, h/3, w, h/3));
            g2d.draw(new Line2D.Double(w/3, 0, w/3, h));
            g2d.draw(new Line2D.Double(w*2/3, 0, w*2/3, h));
            g2d.draw(new Line2D.Double(0, h*2/3, w, h*2/3));
            
            
            // Draw the Os and Xs
            for (int i=0; i<9; ++i) {
                double posinx=(i%3+0.5)*w/3.0;
                double posiny=(i/3+0.5)*h/3.0;
                double XR=w/8.0;
                double YR=h/8.0;
                if (position[i]==O) {
                    g2d.setPaint(colorforo);
                    g2d.draw(new Ellipse2D.Double(posinx-XR, posiny-YR, XR*2, YR*2));
                }
                else if (position[i]==X) {
                    g2d.setPaint(colorforx);
                    g2d.draw(new Line2D.Double(posinx-XR, posiny-YR, posinx+XR, posiny+YR));
                    g2d.draw(new Line2D.Double(posinx-XR, posiny+YR, posinx+XR, posiny-YR));
                }
            }
        }
        
        // Computer plays X
        void Xplay()
        {
            
            // Check if game is over
            if (won(O))                                     // check if user won the game?
                newGame(O);
            else if (isDraw())                              // checks whether all blocks are filled?
                newGame(Space);
            
            // Play X, possibly ending the game
            else {
                CPUplay();                                 // play for cpu to end the game
                if (won(X))
                    newGame(X);
                else if (isDraw())                          // checks whether all blocks are filled?
                    newGame(Space);                        // declare the winner and clear the board for new game
            }
        }
        
        // Draw an O where the mouse is clicked
        public void mouseClicked(MouseEvent e) {
            int posinx=e.getX()*3/getWidth();
            int posiny=e.getY()*3/getHeight();
            int pos=posinx+3*posiny;
            if (pos>=0 && pos<9 && position[pos]==Space) {
                position[pos]=O;
                repaint();
                Xplay();  // computer plays
                repaint();
            }
        }
        
        //  other events ignored
        public void mousePressed(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
        
        
        // player win in the row from a to b?
        boolean testRow(char player, int a, int b) {
            return position[a]==player && position[b]==player
            && position[(a+b)/2]==player;
        }
        
        // Return is player is winning true
        boolean won(char player) {
            for (int i=0; i<8; ++i)
                if (testRow(player, rows[i][0], rows[i][1]))
                    return true;
            return false;
        }
        
        // Return the position of a empty block  in a row if the
        // other 2 block are used by player, or -1 .
        int emptyrowspace(char player) {
            for (int i=0; i<8; ++i) {
                int result=emptyspace(player, rows[i][0], rows[i][1]);
                if (result>=0)
                    return result;
            }
            return -1;
        }
        
        // Play X in the best spot
        void CPUplay() {
            int r=emptyrowspace(X);  // try a row of X and win if possible
            if (r<0)
                r=emptyrowspace(O);  //  try to block user from winning
            if (r<0) {  //  move randomly
                do
                    r=random.nextInt(9);
                while (position[r]!=Space);
            }
            position[r]=X;
        }
        
        
    
        // If 2 spots in the row from a to b
        // are  by user and the any 1  is emplty, then return the
        // number of the empty block else return -1.
        int emptyspace(char player, int x, int y) {
            int z=(x+y)/2;  // middle spot
            if (position[x]==player && position[z]==player && position[y]==Space)
                return y;
            if ( position[y]==player &&  position[x]==player && position[z]==Space)
                return z;
            if (position[y]==player  && position[x]==Space && position[z]==player)
                return x;
            return -1;
        }
        
        // Are all 9 spots filled?
        boolean isDraw() {
            for (int i=0; i<9; ++i)
                if (position[i]==Space)
                    return false;
            return true;
        }
        
        // Start for new game and check the status
        
        void newGame(char winner) {
            repaint();
            
            // Announce result of last game.  Ask user to play again.
            String result;
            if (winner==O) {
                ++winning;
                result = "player1 Won the match!";
            }
            else if (winner==X) {
                ++loss;
                result = "I Win!";
            }
            else {
                result = "Undecided";
                ++Gamedraw;
            }
            
            if (JOptionPane.showConfirmDialog(null,
                                              "You  "+winning+ " wins, "+loss+" losses, "+Gamedraw+" undecided\n"
                                              +"Wants to Play again?", result, JOptionPane.YES_NO_OPTION)
                !=JOptionPane.YES_OPTION) {
                winning=loss=Gamedraw=0;
            }
            
            
            // clear empty board to start  game
            for (int j=0; j<9; ++j)
                position[j]=Space;
            
            // Computer starts here after every game
            if ((winning+loss+Gamedraw)%2 == 1)
                CPUplay();
        }
        
        
    } // Board
    public  void main ( String [] args) throws IOException {
        String outfile = "/Users/Himanshu/Desktop/java/classnotes/him.txt";
        try
        {
            FileWriter fwrite = new FileWriter(outfile);
            PrintWriter out = new  PrintWriter(fwrite);
            
            out.println("GOOD");
            
            
            out.close();
        }
        catch (IOException f)
        {
            System.out.println("Error" + f);
        }
    }
}// class end TicTacToe

