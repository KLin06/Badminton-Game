import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.event.MouseListener;
import java.awt.event.*;

public class CircleDetection extends JPanel implements MouseListener, Runnable {
    public static BufferedImage[] gamestate = new BufferedImage [9];
    public static int gamestateNum = 0;

    public CircleDetection() {
        setPreferredSize(new Dimension(1000, 600));
        setFocusable(true);
        addMouseListener(this);
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

    public static void main(String[] args) {
        JFrame frame = new JFrame("CircleDetection");
        CircleDetection panel = new CircleDetection();
        frame.add(panel);
        frame.setVisible(true);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            gamestate[0] = ImageIO.read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\gamestate1.png"));
            gamestate[1] = ImageIO.read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\gamestate2.png"));
            gamestate[5] = ImageIO.read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\gamestate6.png"));
            gamestate[7] = ImageIO.read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\gamestate8.png"));
            gamestate[8] = ImageIO.read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\gamestate9.png"));
            
        } catch (Exception e) {
            System.out.println("Something wrong with image");
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(gamestate[gamestateNum], 0, 0, null);
    }

    public void mousePressed(MouseEvent e) {
        /* 
        if(gamestate == 0){
            if(e.getX() <=200 && e.getX() >= 100 && e.getY() >= 500 && e.getY() <= 550){
                gamestate = 1;
                repaint();
            }
        } else if (gamestate == 1){
            if(e.getX() <=200 && e.getX() >= 100 && e.getY() >= 500 && e.getY() <= 550){
                gamestate = 3;
                repaint();
            }
        } else if (gamestate == 3){
            gamestate = 0;
            repaint();
        }
        */
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
