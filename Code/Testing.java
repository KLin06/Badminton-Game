import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Testing extends JPanel implements KeyListener, Runnable {
    public static BufferedImage birdie;
    public static int xPos = 200;
    public static int yPos = 300;
    public static boolean wPressed = false;
    public static boolean aPressed = false;
    public static boolean sPressed = false;
    public static boolean dPressed = false;
    public static int frameController = 0;
    public static int spriteNumber = 0;
    public static int degRotate = 0;
    public static int xVelocity = 10;
    public static int yVelocity = -10;
    public static int gravity = 5;


    public Testing() { // change JPanel Settings
        setPreferredSize(new Dimension(1400, 750));
        setFocusable(true);
        addKeyListener(this);
        Thread t = new Thread(this);
        t.start();
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
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
        g2d.rotate(Math.toRadians(degRotate), xPos + 25 , yPos + 25);
        g.drawImage(birdie, xPos, yPos, null);
        if(frameController == 4){
            degRotate = (degRotate + 1) % 360;
            yPos += yVelocity;
            yVelocity += gravity;
            xPos += xVelocity;
            if(yPos >= 330) {
                yPos = 300;
                yVelocity = -10;
            }
            if(xPos >= 1000) {
                xPos = 200;
            }
            spriteNumber = (spriteNumber + 1) % 4;
            frameController = 0;
        }
        frameController++;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Lesson 7 ");
        Testing panel = new Testing();
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
