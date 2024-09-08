import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GamestateTransitions extends JPanel implements Runnable, KeyListener, MouseListener {
    // Variables
    public static BufferedImage[] stateImage = new BufferedImage[9];
    public static BufferedImage[] levelImage = new BufferedImage[6];
    public static int gameState = 0; // which game state number
    public static int levelState = 0; // which game state number

    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g);
        g.drawImage(stateImage[gameState], 0, 0, null);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("GamestateTransitions");
        GamestateTransitions panel = new GamestateTransitions();
        frame.add(panel);
        frame.setVisible(true);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            stateImage[0] = ImageIO.read(new File("CS Game\\Pictures\\gamestate1.png"));
            stateImage[1] = ImageIO.read(new File("CS Game\\Pictures\\gamestate2.png"));
            stateImage[2] = ImageIO.read(new File("CS Game\\Pictures\\gamestate3.png"));
            stateImage[3] = ImageIO.read(new File("CS Game\\Pictures\\gamestate4.png"));
            stateImage[5] = ImageIO.read(new File("CS Game\\Pictures\\gamestate6.png"));
            stateImage[7] = ImageIO.read(new File("CS Game\\Pictures\\gamestate8.png"));
            stateImage[8] = ImageIO.read(new File("CS Game\\Pictures\\gamestate9.png"));
        } catch (Exception e) {
            System.out.println("Something wrong with image");
        }
    }

    public GamestateTransitions() { // change JPanel Settings
        setPreferredSize(new Dimension(1000, 600));
        this.setFocusable(true);
        addMouseListener(this);
        addKeyListener(this);
        Thread t = new Thread(this);
        t.start();
    }

    public void run() {
        while (true) {
            try {
                repaint();
                Thread.sleep(20); // 20 is 50 fps, 17 is approx 60

            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    // Code for key events
    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    // code for mouse events
    public void mousePressed(MouseEvent e) {
        // on gamestate 1
        if (gameState == 0) {
            if (e.getX() >= 300 && e.getX() <= 700 && e.getY() >= 235 && e.getY() <= 355) {
                gameState = 1;
            }
            if (e.getX() >= 300 && e.getX() <= 700 && e.getY() >= 385 && e.getY() <= 505) {
                gameState = 5;
            }
        }
        // on gamestate 2
        else if (gameState == 1) { // circle option
            if (Math.pow(e.getX() - 104, 2) + Math.pow(e.getY() - 87, 2) <= Math.pow(68, 2)) {
                gameState = 0;
            }
            // boxes
            else if (e.getX() >= 70 && e.getX() <= 320 && e.getY() >= 175 && e.getY() <= 340) {
                levelState = 0;
                gameState = 2;
            } else if (e.getX() >= 370 && e.getX() <= 620 && e.getY() >= 175 && e.getY() <= 340) { 
                levelState = 1;
                gameState = 2;
            } else if (e.getX() >= 670 && e.getX() <= 920 && e.getY() >= 175 && e.getY() <= 340) {
                levelState = 2;
                gameState = 2;
            } else if (e.getX() >= 70 && e.getX() <= 320 && e.getY() >= 375 && e.getY() <= 540) {
                levelState = 3;
                gameState = 2;
            } else if (e.getX() >= 370 && e.getX() <= 620 && e.getY() >= 375 && e.getY() <= 540) {
                levelState = 4;
                gameState = 2;
            } else if (e.getX() >= 670 && e.getX() <= 920 && e.getY() >= 375 && e.getY() <= 540) {
                levelState = 5;
                gameState = 3;
            }
        } else if (gameState == 2) { //gamestate 3
            gameState = 7;
        } else if(gameState == 3){ //gamestate 4
            gameState = 8;
        } else if (gameState == 5) { // gamestate 6
            if (Math.pow(e.getX() - 109.0, 2) + Math.pow(e.getY() - 114.0, 2) <= Math.pow(68, 2)) {
                gameState = 0;
            }
        } else if (gameState == 7) { //gamestate 8
            if (e.getX() >= 94 && e.getX() <= 459 && e.getY() >= 231 && e.getY() <= 364) {
                levelState++;
                if(levelState == 5){ //making sure at final level they get send to the stadium
                    gameState = 3;
                } else {
                    gameState = 2;
                }
            } else if (e.getX() >= 527 && e.getX() <= 892 && e.getY() >= 231 && e.getY() <= 364){
                gameState = 1;
            }
        } else if (gameState == 8) { //gamestate 9
            if (e.getX() >= 94 && e.getX() <= 459 && e.getY() >= 231 && e.getY() <= 364) {
                gameState = 3;
            } else if (e.getX() >= 527 && e.getX() <= 892 && e.getY() >= 231 && e.getY() <= 364){
                gameState = 1;
            }
        }

    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }
}
