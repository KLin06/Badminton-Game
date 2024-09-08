import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.awt.event.*;

public class Template extends JPanel implements Runnable, KeyListener, MouseListener {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Template");
        Template panel = new Template();
        frame.add(panel);
        frame.setVisible(true);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            // import gamestate backgrounds
            stateImage[0] = ImageIO.read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\gamestate1.png"));
            stateImage[1] = ImageIO.read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\gamestate2.png"));
            stateImage[2] = ImageIO.read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\gamestate3.png"));
            stateImage[3] = ImageIO.read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\gamestate4.png"));
            stateImage[5] = ImageIO.read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\gamestate6.png"));
            stateImage[7] = ImageIO.read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\gamestate8.png"));
            stateImage[8] = ImageIO.read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\gamestate9.png"));
        } catch (Exception e) {
            System.out.println("Something wrong with image");
        }
    }

    public Template() { // change JPanel Settings
        setPreferredSize(new Dimension(1000, 600));
        this.setFocusable(true);
        addKeyListener(this);
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

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void mousePressed(MouseEvent e) {
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
