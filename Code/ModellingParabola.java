import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ModellingParabola extends JPanel implements KeyListener, Runnable {
    public static BufferedImage birdie;
    public static int initialY = -30;
    public static int startX = 00;
    public static int startY = 550;
    public static int xPos = 200;
    public static int yPos = 550;
    public static int frameController = 0;
    public static int spriteNumber = 0;
    public static double degRotate = 0;
    public static int xVelocity = 15;
    public static int yVelocity = initialY;
    public static int gravity = 2;
    public static boolean direction = true; // true is to the left, false is to the right

    public ModellingParabola() { // change JPanel Settings
        setPreferredSize(new Dimension(1000, 600));
        setFocusable(true);
        addKeyListener(this);
        Thread t = new Thread(this);
        t.start();
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g);
        g2d.rotate(Math.toRadians(degRotate), xPos + 25, yPos + 25);
        g.drawImage(birdie, xPos, yPos, null);
        if (frameController == 3) {
            yPos += yVelocity;
            yVelocity += gravity;
            xPos += xVelocity;
            if (yPos >= 550) {
                yPos = startY;
                yVelocity = initialY; // need initial y
            }
            spriteNumber = (spriteNumber + 1) % 4;
            frameController = 0;
        }
        frameController++;
        if (xPos >= 950 || xPos <= 0) { // if its hits sides
            direction = !direction; // changes the direction
            xVelocity = -xVelocity;
        }
        if (direction) {
            degRotate = -(90 * yVelocity / initialY) % 360; // -(90*yVelocity/initialY) % 360 for L to R,
                                                            // (90*yVelocity/initialY- 180) % 360 is R to L
        } else {
            degRotate = (90*yVelocity/initialY- 180) % 360;
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Parabola");
        ModellingParabola panel = new ModellingParabola();
        frame.add(panel);
        frame.setVisible(true);
        frame.pack();

        try {
            birdie = ImageIO.read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\Unit 6\\birdie.png"));
        } catch (Exception e) {
            System.out.println("Something wrong with image");
        }
    }

    public void keyPressed(KeyEvent e) {
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

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }
}
