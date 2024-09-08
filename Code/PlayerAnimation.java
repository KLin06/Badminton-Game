import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class PlayerAnimation extends JPanel implements KeyListener, Runnable {
    public static BufferedImage[] hittingAnimation = new BufferedImage [7];
    public static int xPos = 200;
    public static int yPos = 500;
    public static boolean wPressed = false;
    public static boolean aPressed = false;
    public static boolean sPressed = false;
    public static boolean dPressed = false;
    public static int frameController = 0;
    public static int spriteNumber = 0;


    public PlayerAnimation() { // change JPanel Settings
        setPreferredSize(new Dimension(1000, 600));
        setFocusable(true);
        addKeyListener(this);
        Thread t = new Thread(this);
        t.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(wPressed && yPos > 0){
            yPos -= 8;
        }
        if(sPressed && yPos < 750){
            yPos += 8;
        }
        if(aPressed && xPos > 0){
            xPos -= 8;
        }
        if(dPressed && xPos < 1400){
            xPos += 8;
        }
        g.drawImage(hittingAnimation[spriteNumber], xPos, yPos, null);
        if(frameController == 3){
            spriteNumber = (spriteNumber + 1) % 4;
            frameController = 0;
        }
        frameController++;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Lesson 7 ");
        PlayerAnimation panel = new PlayerAnimation();
        frame.add(panel);
        frame.setVisible(true);
        frame.pack();

        try {
           hittingAnimation[0] = ImageIO.read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\Unit 6\\pikachu0.png"));
           pikachu[1] = ImageIO.read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\Unit 6\\pikachu1.png"));
           pikachu[2] = ImageIO.read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\Unit 6\\pikachu2.png"));
           pikachu[3] = ImageIO.read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\Unit 6\\pikachu3.png"));
        } catch (Exception e) {
            System.out.println("Something wrong with image");
        }
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == 'w') {
            wPressed = true;
        }
        if (e.getKeyChar() == 'a') {
            aPressed = true;
        }
        if (e.getKeyChar() == 's') {
            sPressed = true;
        }
        if (e.getKeyChar() == 'd') {
            dPressed = true;
        }
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyChar() == 'w') {
            wPressed = false;
        }
        if (e.getKeyChar() == 'a') {
            aPressed = false;
        }
        if (e.getKeyChar() == 's') {
            sPressed = false;
        }
        if (e.getKeyChar() == 'd') {
            dPressed = false;
        }
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

    public void keyTyped(KeyEvent e) {
    }
}
